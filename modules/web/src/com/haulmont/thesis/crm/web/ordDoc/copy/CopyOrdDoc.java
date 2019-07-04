/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ordDoc.copy;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.core.app.mathematic.ProductCalculate;
import com.haulmont.thesis.gui.app.DocumentCopySupport;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by d.ivanlov on 21.11.2017.
 */
public class CopyOrdDoc {

    protected Map<String, Object> paramsResult = new HashMap<>();
    protected List<String> notCreateOrdDetail = new ArrayList<>();
    protected OrdDoc salesOrder;
    protected List<OrderDetail> salesOrderDetails;

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;
    protected ExtCompany company;
    protected ExtProject project;

    public CopyOrdDoc(Map<String, Object> params) {

        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.orgStructureService = AppBeans.get(OrgStructureService.class);
        this.documentCopySupport = AppBeans.get(DocumentCopySupport.class);
        this.salesOrder = documentCopySupport.reloadSrcDoc((OrdDoc) params.get("ordDoc"));
        this.salesOrderDetails = this.salesOrder.getOrderDetails();
        this.company = (ExtCompany) params.get("company");
        this.project = (ExtProject) params.get("project");
    }

    public Map<String, Object> copy() {

        OrdDoc template = getTemplate();
        if (template == null){
            this.paramsResult.put("error", "Template document is not specified");
        } else {
            template = documentCopySupport.reloadSrcDoc(template);
            copyFrom(template);
        }
        return this.paramsResult;
    }

    protected void copyFrom(@Nonnull OrdDoc sourceDoc){
        Preconditions.checkNotNullArgument(sourceDoc, "Template document is not specified");
        OrdDoc ord = metadata.create(OrdDoc.class);

        ord.copyFrom(sourceDoc); //???
        ord.setCreator(userSessionSource.getUserSession().getUser());
        ord.setSubstitutedCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        ord.setTemplate(false);
        ord.setDocCategory(this.salesOrder.getDocCategory());
        ord.setParentCardAccess(this.salesOrder.getParentCardAccess());
        ord.setCompany(this.company);
        ord.setCurrency(this.salesOrder.getCurrency());
        ord.setProject(this.project);
        ord.setPrintInEnglish(this.salesOrder.getPrintInEnglish());
        ord.setTax(this.salesOrder.getTax());
        ord.setOrganization(this.salesOrder.getOrganization());
        ord.setOwner(orgStructureService.getEmployeeByUser(userSessionSource.getUserSession().getUser()));
        ord.setIntegrationResolver(null);
        ord.setTypeSale(this.salesOrder.getTypeSale());
        //// TODO:
        ArrayList<OrderDetail> arrayOrdDetail = new ArrayList<>();
        for (OrderDetail item : salesOrderDetails) {
            OrderDetail ordDetail = metadata.create(OrderDetail.class);
            BigDecimal i = getCost(item);
            if (i.signum() != -1) {
                ordDetail.setOrdDoc(ord);
                ordDetail.setCostPerPiece(i);
                ordDetail.setProduct(item.getProduct());
                ordDetail.setAmount(item.getAmount());
                ordDetail.setSecondaryAmount(item.getSecondaryAmount());
                ordDetail.setDiscountType(DiscountTypeEnum.sumTotal);
                ordDetail.setMarginSum(BigDecimal.ZERO);
                ordDetail.setDiscountSum(BigDecimal.ZERO);
                ordDetail.setTax(item.getTax());
                ordDetail.setAlternativeName_ru(item.getAlternativeName_ru());
                ordDetail.setAlternativeName_en(item.getAlternativeName_en());
                ordDetail.setOrderDetailGroup(item.getOrderDetailGroup());
                ordDetail.setDependentDetail(null);
                ordDetail.setTempId(item.getId());
                arrayOrdDetail.add(new ProductCalculate(ordDetail).costCalculation());
            } else {
                notCreateOrdDetail.add(item.getAlternativeName_ru());
            }
        }
        //пересчет полной стоимости заказа
        BigDecimal fullSum = BigDecimal.valueOf(0);
        BigDecimal taxSum = BigDecimal.valueOf(0);
        for (OrderDetail item : arrayOrdDetail) {
            if(item.getTotalSum()!=null){
                fullSum = fullSum.add(item.getTotalSum());
            }
            if(item.getTaxSum()!=null){
                taxSum = taxSum.add(item.getTaxSum());
            }
        } //
        ord.setTaxSum(taxSum);
        ord.setFullSum(fullSum);
        ord.setOrderDetails(arrayOrdDetail);
        dependentDetail(ord);
        this.paramsResult.put("ordDoc", ord);
        this.paramsResult.put("notCreateOrdDetail", notCreateOrdDetail);
    }

    protected OrdDoc getTemplate() {
        // TODO: Get string "DefaultSalesOrd" from CrmConfig
        String templateName = "DefaultSalesOrd";
        Preconditions.checkNotNullArgument(templateName, "Template name is not specified");
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);
        return dataManager.load(loadContext);
    }

    protected BigDecimal getCost(OrderDetail orderDetail) {
        Cost costBase = loadCost(orderDetail);
        List<Cost> costProjectList = loadProjectCost(orderDetail);
        List<Cost> resultCostList = new ArrayList<>();
        resultCostList.addAll(costProjectList);
        resultCostList.add(costBase);
        BigDecimal i = BigDecimal.valueOf(-1);
        if (resultCostList.get(0) != null) {
            if (this.salesOrder.getCurrency().getCode().equals("643")) {
                i = resultCostList.get(0).getPrimaryCost();
            } else {
                i = resultCostList.get(0).getSecondaryCost();
            }
            if(i.signum() == 0) {
                i = orderDetail.getCostPerPiece();
            }
        }
        return i;
    }

    private Cost loadCost(OrderDetail orderDetail) { //Поиск Цены по коду
        LoadContext loadContext = new LoadContext(Cost.class).setView("edit");
        loadContext.setQueryString("select c from crm$Cost c where c.product.id = :product and c.startDate <= :date " +
                "and c.project.id is null and (c.product.notInUse is null or c.product.notInUse = false) " +
                "and (c.product.nomenclature.notInUse is null or c.product.nomenclature.notInUse = false) " +
                "and (c.product.exhibitSpace.id = :exhibitSpace or c.product.exhibitSpace is null) " +
                "order by c.startDate desc ")
                .setParameter("product", orderDetail.getProduct())
                .setParameter("date", java.util.Calendar.getInstance().getTime())
                .setParameter("exhibitSpace", this.project.getExhibitSpace());
        return dataManager.load(loadContext);
    }

    private List<Cost> loadProjectCost(OrderDetail orderDetail) { //Поиск Цены по коду
        LoadContext loadContext = new LoadContext(Cost.class).setView("edit");
        loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.project.id = :projectId and c.startDate <= :date) " +
                "and e.product.id = :product and e.project.id = :projectId and (e.product.notInUse is null or e.product.notInUse = false) " +
                "and (e.product.nomenclature.notInUse is null or e.product.nomenclature.notInUse = false) " +
                "and (e.product.exhibitSpace.id = :exhibitSpace or e.product.exhibitSpace is null)" )
                .setParameter("product", orderDetail.getProduct())
                .setParameter("date", java.util.Calendar.getInstance().getTime())
                .setParameter("projectId", this.project.getId())
                .setParameter("exhibitSpace", this.project.getExhibitSpace());
        return dataManager.loadList(loadContext);
    }

    private void dependentDetail(OrdDoc ord) {
        for (OrderDetail orderDetailCopyFrom:this.salesOrderDetails) {
            if (orderDetailCopyFrom.getDependentDetail() != null) {
                OrderDetail orderDetail = findOrderDetail(ord, orderDetailCopyFrom.getDependentDetail().getId());
                if (orderDetail != null) {
                    OrderDetail orderDetailSetDependentDetail = findOrderDetail(ord, orderDetailCopyFrom.getId());
                    if (orderDetailSetDependentDetail != null) {
                        orderDetailSetDependentDetail.setDependentDetail(orderDetail);
                    }
                }
            }
        }
    }

    private OrderDetail findOrderDetail(OrdDoc ordDoc, UUID Id) {
        for (OrderDetail orderDetail:ordDoc.getOrderDetails()) {
            if (orderDetail.getTempId().equals(Id)) {
                return orderDetail;
            }
        }
        return null;
    }

}
