/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.invDoc.surcharge;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.gui.app.DocumentCopySupport;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by d.ivanov on 21.12.2018.
 */
public class SurchargeInvoice {


    protected Set<Entity> toCommit = new HashSet<>();
    protected OrdDoc salesOrder;
    protected OrdDoc doc;
    protected BigDecimal sum;

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;



    public SurchargeInvoice(Map<String, Object> params) {

        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.orgStructureService = AppBeans.get(OrgStructureService.class);
        this.documentCopySupport = AppBeans.get(DocumentCopySupport.class);

        this.sum = ((BigDecimal) params.get("sum"));
        this.doc = (OrdDoc)params.get("ordDoc");
    }

    public InvDoc getInvSurcharge() {
        this.salesOrder = documentCopySupport.reloadSrcDoc(this.doc);
        InvDoc template = getTemplate();
        template = documentCopySupport.reloadSrcDoc(template);
        return copyFrom(template);
    }

    protected InvDoc copyFrom(@Nonnull InvDoc sourceDoc){
        Preconditions.checkNotNullArgument(sourceDoc, "Template document is not specified");

        Product product = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getProductAdvance();
        BigDecimal taxRate = product.getNomenclature().getTax().getRate();
        BigDecimal sumWithoutNds = this.sum.divide(taxRate.add(BigDecimal.valueOf(1)), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal taxSum = this.sum.subtract(sumWithoutNds);

        InvDoc invoice = metadata.create(InvDoc.class);

        invoice.copyFrom(sourceDoc, toCommit);

        invoice.setCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        invoice.setSubstitutedCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        invoice.setTemplate(false);

        invoice.setParentCard(this.salesOrder);
        invoice.setParentCardAccess(this.salesOrder.getParentCardAccess());
        invoice.setCompany(this.salesOrder.getCompany());
        invoice.setCurrency(this.salesOrder.getCurrency());
        invoice.setProject(this.salesOrder.getProject());
        if (this.salesOrder.getProject() != null) {
            invoice.setChifAccount(loadEmployee(1, this.salesOrder.getProject())); //Глав. бух.
            invoice.setGeneralDirector(loadEmployee(2, this.salesOrder.getProject())); //Ген. дир.

            invoice.setAdditionalDetail(String.format("Участие в мероприятии %s", this.salesOrder.getProject().getName())); //Детализация одной строкой
        }

        invoice.setPrintSingleLine(true);
        invoice.setPrintInEnglish(this.salesOrder.getPrintInEnglish());
        invoice.setIntegrationResolver(null);

        int i = getVersionNumber();
        i = (i > 0) ? i + 1 : 1;
        if (this.salesOrder.getNumber() != null) {
            String str = this.salesOrder.getNumber();
            invoice.setNumber(String.format("%1$s/%2$d", str, i));
        }
        invoice.setContract(this.salesOrder.getContract());

        invoice.setDepartment(this.salesOrder.getDepartment());
        invoice.setTax(product.getNomenclature().getTax());
        invoice.setTaxSum(taxSum);
        invoice.setFullSum(this.sum);
        invoice.setOrganization(this.salesOrder.getOrganization());
        invoice.setOwner(orgStructureService.getEmployeeByUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser()));

        //// TODO: Необходимо передавать счет организации в зависимости от валюты счета или искать его при ините счета (Чижиков П.С)

        ArrayList<InvoiceDetail> inv = new ArrayList<>();

        InvoiceDetail invoiceDetail = metadata.create(InvoiceDetail.class);
        invoiceDetail.setInvDoc(invoice);
        invoiceDetail.setProduct(product);
        invoiceDetail.setAmount(BigDecimal.ONE);
        invoiceDetail.setCost(this.sum);
        invoiceDetail.setTax(product.getNomenclature().getTax());
        invoiceDetail.setTaxSum(taxSum);
        invoiceDetail.setAlternativeName_ru(product.getTitle_ru());
        invoiceDetail.setAlternativeName_en(product.getTitle_en());
        invoiceDetail.setSumWithoutNds(sumWithoutNds);
        invoiceDetail.setTotalSum(this.sum);
        invoiceDetail.setOrdDetail(null);
        inv.add(invoiceDetail);

        invoice.setInvoiceDetails(inv);

        return invoice;
    }

    protected InvDoc getTemplate() {
        // TODO: 30.01.2017 Get string "DefaultSalesInvoice" from CrmConfig
        String templateName = "DefaultSalesInvoice";
        Preconditions.checkNotNullArgument(templateName, "Template name is not specified");

        LoadContext loadContext = new LoadContext(InvDoc.class)
                .setView("_minimal");

        loadContext.setQueryString("select e from crm$InvDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);
        return dataManager.load(loadContext);
    }

    protected ExtEmployee loadEmployee(int param, Project project) {
        LoadContext loadContext = new LoadContext(ExtEmployee.class)
                .setView("edit");

        if(param == 1){
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 1 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        else {
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 2 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        List<ExtEmployee> employees = dataManager.loadList(loadContext);

        return employees.isEmpty() ? null : employees.get(0);
    }

    protected int getVersionNumber() {
        LoadContext loadContext = new LoadContext(InvDoc.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$InvDoc e where e.parentCard.id = :salesOrder")
                .setParameter("salesOrder", this.salesOrder);

        List<InvDoc> invoices = dataManager.loadList(loadContext);

        return invoices.isEmpty() ? 0 : invoices.size();
    }

}
