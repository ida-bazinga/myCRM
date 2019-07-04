/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.cost;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.core.app.mathematic.ProductCalculate;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class CostSelect extends AbstractWindow  {

    @Named("mainDs")
    protected CollectionDatasource mainDs;

    @Named("selectDs")
    protected GroupDatasource<OrderDetail, UUID> selectDs;

    @Inject
    protected Metadata metadata;

    protected TextField searchSimpleText;

    @Inject
    protected Table mainTable;

    @Inject
    protected GroupTable mainTableSelect;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    protected MyUtilsService myUtilsService;

    @Inject
    private DataManager dataManager;

    private OrdDoc ordDoc;
    private UUID exhibitSpaceId;




    @Override
    public void init(final Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidth(1024).setHeight(768).setResizable(true);
        selectDs.clear();
        ordDoc = (OrdDoc) params.get("ordDoc");
        Collection<OrderDetail> orderDetailCollection = (Collection<OrderDetail>) params.get("ordDocDetails");
        exhibitSpace(ordDoc.getProject().getId());
        //int flg = ordDoc.getDocCategory().getCode().equals("travel") ? 1 : 0;
        params.put("cost", myUtilsService.getCostList(exhibitSpaceId,ordDoc.getProject().getId(), ordDoc.getOrganization().getId()));
        mainDs.refresh(params);

        if (orderDetailCollection.size() > 0) {
            for (OrderDetail ordDetail : orderDetailCollection) {
                OrderDetail ordDetailNew = new OrderDetail(); //что бы не обновлялась запись в родители
                ordDetailNew.setProduct(ordDetail.getProduct());
                ordDetailNew.setAmount(ordDetail.getAmount());
                ordDetailNew.setSecondaryAmount(ordDetail.getSecondaryAmount());
                ordDetailNew.setCost(ordDetail.getCost());
                ordDetailNew.setTotalSum(ordDetail.getTotalSum());
                ordDetailNew.setTaxSum(ordDetail.getTaxSum());
                ordDetailNew.setOrdDoc(ordDetail.getOrdDoc());
                ordDetailNew.setTax(ordDetail.getTax());
                ordDetailNew.setSumWithoutNds(ordDetail.getSumWithoutNds());
                ordDetailNew.setAlternativeName_ru(ordDetail.getAlternativeName_ru());
                ordDetailNew.setAlternativeName_en(ordDetail.getAlternativeName_en());
                ordDetailNew.setDiscountType(ordDetail.getDiscountType());
                ordDetailNew.setDiscountSum(ordDetail.getDiscountSum());
                ordDetailNew.setMarginSum(ordDetail.getMarginSum());
                ordDetailNew.setCostPerPiece(ordDetail.getCostPerPiece());
                ordDetailNew.setOrderDetailGroup(ordDetail.getOrderDetailGroup());
                ordDetailNew.setTempId(ordDetail.getId());
                ordDetailNew.setDependentDetail(ordDetail.getDependentDetail());
                selectDs.addItem(ordDetailNew);
            }
        }

        final Button searchSimple = getComponentNN("searchSimple");
        searchSimpleText = getComponent("searchSimpleText");
        searchSimple.setAction(new AbstractAction(PickerField.OpenAction.NAME) {
            public void actionPerform(Component component) {
                params.put("searchSimpleText", searchSimpleText.getValue());
                mainDs.refresh(params);
            }
            @Override
            public String getCaption() {
                return getMessage("actions.Apply");
            }
        });
        createColumnMainTable();
        createTableColumn();
    }

    private void exhibitSpace(UUID Id) {
        LoadContext loadContext = new LoadContext(ExtProject.class).setView("browse");
        loadContext.setQueryString("select e from crm$Project e where e.id = :Id")
                .setParameter("Id", Id).setMaxResults(1);
        ExtProject extProject = dataManager.load(loadContext);
        if(extProject != null) {
            exhibitSpaceId = extProject.getExhibitSpace() != null ? extProject.getExhibitSpace().getId() : null;
        }
    }

    public void createColumnMainTable() {
        String cellCostName = "primaryCost";
        if ("978".equals(ordDoc.getCurrency().getCode())) {
            cellCostName = "secondaryCost";
        }
        mainTable.addGeneratedColumn(cellCostName, new Table.ColumnGenerator<Cost>() {
            @Override
            public Component generateCell(final Cost entity) {
                LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                BigDecimal cost = entity.getPrimaryCost();
                if ("978".equals(ordDoc.getCurrency().getCode())) {
                    cost = entity.getSecondaryCost();
                }
                linkButton.setCaption(cost != null ? String.format("%,.2f", cost.setScale(2, BigDecimal.ROUND_HALF_EVEN)): BigDecimal.valueOf(0).toString());
                linkButton.setAction(new AbstractAction("") {
                    @Override
                    public void actionPerform(Component component) {
                        setValue(entity);
                    }
                });
                return linkButton;
            }
        });
        mainTable.getColumn(cellCostName).setCaption("Цена");
        mainTable.getColumn(cellCostName).setWidth(80);
        mainTable.addGeneratedColumn("product", new Table.ColumnGenerator<Cost>() {
            @Override
            public Component generateCell(final Cost entity) {
                LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                linkButton.setCaption(entity.getProduct().getTitle_ru());
                linkButton.setAction(new AbstractAction("") {
                    @Override
                    public void actionPerform(Component component) {
                        setValue(entity);
                    }
                });
                return linkButton;
            }
        });
    }

    public void setValue(Cost entity) {
        addSelectDs(entity);
    }

    public void onRemove() {
        Set<OrderDetail> orderDetailList = mainTableSelect.getSelected();
        for (OrderDetail orderDetail:orderDetailList) {
            selectDs.removeItem(orderDetail);
        }
    }

    public void onCalc() {
        isCalc();
    }

    protected boolean isCalc() {
        boolean isVaid = true;
        for (OrderDetail ordDetail:selectDs.getItems()) {
            if (isVaid) {
                ProductCalculate productCalculate = new ProductCalculate(ordDetail);
                Map<String, Object> validate = productCalculate.validateCalculate();
                if (validate.size() > 0) {
                    showNotification(getMessage(validate.get("message").toString()), NotificationType.HUMANIZED);
                    isVaid = false;
                    break;
                }
                if (isVaid) productCalculate.costCalculation();
            }
        }
        return isVaid;
    }


    private boolean isValidAmountMin(BigDecimal minQuantity) {
        if(minQuantity == null) {
            return false;
        }
        if(minQuantity.signum() != 1) {
            return false;
        }
        return true;
    }

    private boolean isValidAmountMax(BigDecimal maxQuantity) {
        if(maxQuantity == null) {
            return false;
        }
        return true;
    }

    public void addSelectDs(Cost entity) {
        if(!isValidAmountMin(entity.getProduct().getMinQuantity())) {
            return;
        }
        if(!isValidAmountMax(entity.getProduct().getMaxQuantity())) {
            return;
        }

        OrderDetail ordDetail = metadata.create(OrderDetail.class);
        ordDetail.setAmount(entity.getProduct().getMinQuantity());
        ordDetail.setOrdDoc(ordDoc);
        BigDecimal newCost = entity.getPrimaryCost(); //руб
        if ("978".equals(ordDoc.getCurrency().getCode())) {
            newCost = entity.getSecondaryCost(); //евро
        }
        String characteristicNameRu = (entity.getProduct().getCharacteristic() != null ? entity.getProduct().getCharacteristic().getName_ru() : "");
        String characteristicNameEn = (entity.getProduct().getCharacteristic() != null ? entity.getProduct().getCharacteristic().getName_en() : "");
        characteristicNameRu = characteristicNameRu != null ? characteristicNameRu : "";
        characteristicNameEn = characteristicNameEn != null ? characteristicNameEn : "";
        String printNameRu = (entity.getProduct().getNomenclature().getPrintName_ru() != null ? entity.getProduct().getNomenclature().getPrintName_ru() : "");
        String printNameEn = (entity.getProduct().getNomenclature().getPrintName_en() != null ? entity.getProduct().getNomenclature().getPrintName_en() : "");
        ordDetail.setProduct(entity.getProduct());
        ordDetail.setTax(entity.getProduct().getNomenclature().getTax());
        ordDetail.setAlternativeName_ru(String.format("%s %s", printNameRu.trim(), characteristicNameRu.trim()));
        ordDetail.setAlternativeName_en(String.format("%s %s", printNameEn.trim(), characteristicNameEn.trim()));
        ordDetail.setCost(newCost);
        ordDetail.setTotalSum(newCost.multiply(ordDetail.getAmount()));
        ordDetail.setTaxSum(newCost.multiply(ordDetail.getAmount()).multiply(entity.getProduct().getNomenclature().getTax().getRate()).divide(entity.getProduct().getNomenclature().getTax().getRate().add(BigDecimal.valueOf(1)),2,BigDecimal.ROUND_HALF_UP));
        ordDetail.setSumWithoutNds(ordDetail.getTotalSum().subtract(ordDetail.getTaxSum()));
        ordDetail.setDiscountSum(BigDecimal.valueOf(0));
        ordDetail.setMarginSum(BigDecimal.valueOf(0));
        ordDetail.setDiscountType(DiscountTypeEnum.sumTotal);
        ordDetail.setCostPerPiece(newCost);
        ordDetail.setSecondaryAmount(BigDecimal.valueOf(1));
        ordDetail.setDependentDetail(null);
        //ordDetail.setSecondaryAmount(entity.getProduct().getSecondaryUnit() != null ? BigDecimal.valueOf(1) : null);
        selectDs.addItem(ordDetail);
    }

    @Override
    public void ready() {
        RowsCount rowsCount1 = componentsFactory.createComponent(RowsCount.NAME);
        rowsCount1.setDatasource(selectDs);
        mainTableSelect.setRowsCount(rowsCount1);
        RowsCount rowsCount2 = componentsFactory.createComponent(RowsCount.NAME);
        rowsCount2.setDatasource(mainDs);
        mainTable.setRowsCount(rowsCount2);
        mainTableSelect.expandAll();
    }

    protected void createTableColumn() {
        mainTableSelect.addGeneratedColumn("costPerPiece", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                final OrderDetail orderDetail = (OrderDetail) entity;
                final TextField field = componentsFactory.createComponent(TextField.NAME);
                field.setValue(orderDetail.getCostPerPiece());
                field.setWidth("100%");
                BigDecimal i = getCost(orderDetail);
                field.setEditable(i.signum() == 0);
                field.addListener(new ValueListener() {
                    @Override
                    public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                        BigDecimal i = formatStringToBigDecimal(value);
                        orderDetail.setCostPerPiece(i);
                        field.setValue(i);
                    }
                });
                return field;
            }



        });

        mainTableSelect.addGeneratedColumn("secondaryAmount", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                final OrderDetail orderDetail = (OrderDetail) entity;
                final TextField field = componentsFactory.createComponent(TextField.NAME);
                field.setValue(orderDetail.getSecondaryAmount());
                field.setWidth("100%");
                field.setEditable(orderDetail.getProduct().getSecondaryUnit() == null ? false : true);
                field.addListener(new ValueListener() {
                    @Override
                    public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                        BigDecimal i = formatStringToBigDecimal(value);
                        orderDetail.setSecondaryAmount(i);
                        field.setValue(i);
                    }
                });
                return field;
            }
        });
    }

    public BigDecimal formatStringToBigDecimal(Object value) {
        BigDecimal i = BigDecimal.ZERO;
        if (value != null) {
            String s = value.toString();
            s = s.replaceAll("[^\\d.,]", "").replace(",",".");
            i = new BigDecimal(s);
        }
        return i;
    }

    protected BigDecimal getCost(OrderDetail orderDetail) {
        Cost costBase = loadCost(orderDetail);
        List<Cost> costProjectList = loadProjectCost(orderDetail);
        List<Cost> resultCostList = new ArrayList<>();
        resultCostList.addAll(costProjectList);
        resultCostList.add(costBase);
        BigDecimal i = BigDecimal.ZERO;
        if (resultCostList.get(0) != null) {
            if (ordDoc.getCurrency().getCode().equals("643")) {
                i = resultCostList.get(0).getPrimaryCost();
            } else {
                i = resultCostList.get(0).getSecondaryCost();
            }
        }
        return i;
    }

    private Cost loadCost(OrderDetail orderDetail) { //Поиск Цены по коду
        LoadContext loadContext = new LoadContext(Cost.class).setView("edit");
        loadContext.setQueryString("select c from crm$Cost c where c.product.id = :product and c.startDate <= :date and c.project.id is null order by c.startDate desc")
                .setParameter("product", orderDetail.getProduct())
                .setParameter("date", java.util.Calendar.getInstance().getTime());
        return dataManager.load(loadContext);
    }

    private List<Cost> loadProjectCost(OrderDetail orderDetail) { //Поиск Цены по коду
        LoadContext loadContext = new LoadContext(Cost.class).setView("edit");
        loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.project.id = :id and c.startDate <= :date) and e.product.id = :product and e.project.id = :id")
                .setParameter("product", orderDetail.getProduct())
                .setParameter("date", java.util.Calendar.getInstance().getTime())
                .setParameter("id", ordDoc.getProject());
        return dataManager.loadList(loadContext);
    }


    private void closeMyWindow(String actionId) {
        if ("close".equals(actionId)) {
            selectDs.clear();
        }
        selectDs.setAllowCommit(false);
        mainDs.setAllowCommit(false);
    }

    @Override
    public boolean close(String actionId) {
        closeMyWindow("close");
        return ((Window) frame).close(actionId);
    }

    @Override
    public void closeAndRun(String actionId, Runnable runnable) {
        closeMyWindow("close");
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }

    public void selectButton() {
        if (isCalc()) {
            closeMyWindow("select");
            ((Window) frame).close(Window.CLOSE_ACTION_ID);
        }
    }

    public void closeButton() {
        closeMyWindow("close");
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }

}