package com.haulmont.thesis.crm.web.orderDetail;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.core.app.mathematic.ProductCalculate;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class OrderDetailEditor<T extends OrderDetail> extends AbstractEditor<T> {

    @Inject
    protected LookupPickerField product, hotel, excursion, transfer;

    @Inject
    protected LookupField discountType;

    @Inject
    protected GroupDatasource productDs;

    @Inject
    protected Label secondaryLabel, secondaryUnit, clientNameLabel, hotelLabel, excursionLabel, transferLabel;

    @Inject
    protected TextField amount, marginSum, discountSum, costPerPiece, secondaryAmount, clientName;

    @Inject
    private DataManager dataManager;

    @Inject
    private GroupBoxLayout discountBox, altNameBox;

    @Inject
    private HBoxLayout taxHBox, totalSumHBox, hotelBox, excursionBox, clientNameBox, transferTypeBox;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionDatasource<OrdResource, UUID> resourceDs;
    @Inject
    protected Datasource<OrderDetail> detailDs;

    @Named("dependentDetail")
    private LookupField dependentDetail;
    @Named("tax")
    private Label taxLb;



    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }


    @Override
    protected void postInit() { //Обработчик: после загрузки карточки
        //Map<String, Object> optionalValue = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        /*
        param.put("type", "004");
        if (getItem().getOrdDoc().getDocCategory().getCode().equals("travel")){
            param.put("flg", 1);
            optionalValue.put("flg", 1);
        }
        else {
            param.put("flg", 0);
            optionalValue.put("flg", 0);
        }
        */
        UUID projectId =  getItem().getOrdDoc().getProject().getId();
        UUID exhibitSpaceId = getExhibitSpace(projectId);
        UUID organizationId = getItem().getOrdDoc().getOrganization().getId();
        param.put("project", projectId);
        param.put("exhibitSpace", exhibitSpaceId);
        param.put("organization", organizationId);
        productDs.refresh(param);
        //optionalValue.put("exhibitSpace", exhibitSpaceId);
        //optionalValue.put("project", projectId);
        //optionalValue.put("organization", organizationId);
        PickerField.LookupAction projectLookupAction = (PickerField.LookupAction)product.getActionNN(PickerField.LookupAction.NAME);
        projectLookupAction.setLookupScreen("crm$Product.lookup");
        projectLookupAction.setLookupScreenParams(param);
        product.addOpenAction();
        setTourismFields();
        optionsMapDependentDetail();
        componentSecondaryVisible();
        componentListener();
        editableCost();
    }


    private void optionsMapDependentDetail() {
        Map<String, Object> map = new LinkedHashMap<>();
        List<OrderDetail> details = getItem().getOrdDoc().getOrderDetails();
        if (details != null) {
            for (OrderDetail det : details) {
                map.put(det.getInstanceName(), det);
            }
        }
        dependentDetail.setOptionsMap(map);
    }


    @Override
    protected void initNewItem(T item) {    //Значения для новой записи
        super.initNewItem(item);
        //if(item.getOrdDoc().getTax()!=null) item.setTax(item.getOrdDoc().getTax());
        item.setDiscountType(DiscountTypeEnum.sumTotal);
    }

    //todo check item.Product is null
    private void costCalculation(){
        setAmount();
        setSecondaryAmount();
        OrderDetail item = getItem();
        ProductCalculate productCalculate = new ProductCalculate(item);
        Map<String, Object> validate = productCalculate.validateCalculate();
        if (validate.size() > 0) {
            showNotification(getMessage(validate.get("message").toString()), NotificationType.HUMANIZED);
            return;
        }
        productCalculate.costCalculation();
    }

    @Override
    protected boolean preCommit() {
        OrderDetail item = getItem();
        if(item.getProduct() == null) {
            showNotification(getMessage("Услуга не выбрана!"), NotificationType.HUMANIZED);
            return false;
        }
        if (item.getOrdDoc().getCurrency().getCode().equals("643")) {
            if (item.getCostPerPiece().signum() == 0 || item.getCostPerPiece().signum() == -1) {
                showNotification(getMessage("Нельзя указать цену меньше 1 рубля"), NotificationType.HUMANIZED);
                return false;
            }
        }
        else {
            if (item.getCostPerPiece().signum() == -1) {
                showNotification(getMessage("Нельзя указывать отрицательную цену"), NotificationType.HUMANIZED);
                return false;
            }
        }
        return true;
    }

    private void editableCost() {
        OrderDetail item = getItem();
        BigDecimal i = getCost();
        if (item.getCostPerPiece() != null) {
            if (item.getCostPerPiece().signum() == -1 || item.getCostPerPiece().signum() == 0) {
                item.setCostPerPiece(i);
            }
        }
        costPerPiece.setEditable(i.signum() == 0);
    }

    private void setNullFields(){
        OrderDetail item = getItem();
        item.setCost(BigDecimal.valueOf(0));
        item.setCostPerPiece(BigDecimal.valueOf(0));
        item.setDiscountPercent(BigDecimal.valueOf(0));
        item.setMarginPercent(BigDecimal.valueOf(0));
        item.setMarginSum(BigDecimal.valueOf(0));
        item.setDiscountSum(BigDecimal.valueOf(0));
        item.setSumWithoutNds(BigDecimal.valueOf(0));
        item.setTaxSum(BigDecimal.valueOf(0));
        item.setTotalSum(BigDecimal.valueOf(0));
    }

    private void setAlternateNames(){

        if (getItem().getProduct() != null) {
            OrderDetail item = getItem();
            if (item.getProduct().getNomenclature().getPrintName_ru() != null) {
                String characteristicName = (item.getProduct().getCharacteristic() != null ? item.getProduct().getCharacteristic().getName_ru() : "");
                characteristicName = characteristicName != null ? characteristicName : "";
                String printNameRu = (item.getProduct().getNomenclature().getPrintName_ru() != null ? item.getProduct().getNomenclature().getPrintName_ru() : "");
                String additionalPart = "";
                if (item.getHotel() != null && item.getHotel().getName_ru() != null) {
                    additionalPart += ", " + item.getHotel().getName_ru();
                }
                if (item.getExcursion() != null && item.getExcursion().getName_ru() != null)
                {
                    additionalPart += ", " + item.getExcursion().getName_ru();
                }
                if (item.getTransferType() != null && item.getTransferType().getName_ru() != null)
                {
                    additionalPart += ", " + item.getTransferType().getName_ru();
                }
                if (item.getClientName() != null && item.getClientName() != "") {
                    additionalPart += ", " + item.getClientName();
                }
                String fullName = String.format("%s %s", printNameRu.trim(), characteristicName.trim());
                item.setAlternativeName_ru(fullName + additionalPart);
                //item.setAlternativeName_ru(String.format("%s %s%s", printNameRu.trim(), characteristicName.trim(), additionalPart.trim()));
            }
            if (item.getProduct().getNomenclature().getPrintName_en() != null) {
                String characteristicName = (item.getProduct().getCharacteristic() != null ? item.getProduct().getCharacteristic().getName_en() : "");
                characteristicName = characteristicName != null ? characteristicName : "";
                String printNameEn = (item.getProduct().getNomenclature().getPrintName_en() != null ? item.getProduct().getNomenclature().getPrintName_en() : "");
                String additionalPart = "";
                if (item.getHotel() != null && item.getHotel().getName_en() != null) {
                    additionalPart = ", " + item.getHotel().getName_en();
                }
                if (item.getExcursion() != null && item.getExcursion().getName_en() != null)
                {
                    additionalPart += ", " + item.getExcursion().getName_en();
                }
                if (item.getTransferType() != null && item.getTransferType().getName_en() != null)
                {
                    additionalPart += ", " + item.getTransferType().getName_en();
                }
                String fullName = String.format("%s %s", printNameEn.trim(), characteristicName.trim());
                item.setAlternativeName_en(fullName + additionalPart);
            }
        }
    }

    private void setTourismFields(){
        if (getItem().getProduct() != null
                && getItem().getProduct().getProductType() != null
                && getItem().getProduct().getTourismProductType() != null) {
            //tourismBox.setVisible(true);
            switch (getItem().getProduct().getTourismProductType()) {
                case Hotel:
                    hotelBox.setVisible(true);
                    excursionBox.setVisible(false);
                    clientNameBox.setVisible(true);
                    transferTypeBox.setVisible(false);
                    excursionLabel.setVisible(false);
                    hotelLabel.setVisible(true);
                    transferLabel.setVisible(false);
                    clientNameLabel.setVisible(true);
                    excursion.setVisible(false);
                    hotel.setVisible(true);
                    transfer.setVisible(false);
                    clientName.setVisible(true);
                    excursion.setValue(null);
                    transfer.setValue(null);
                    break;
                case Excursion:
                    hotelBox.setVisible(false);
                    excursionBox.setVisible(true);
                    clientNameBox.setVisible(false);
                    transferTypeBox.setVisible(false);
                    excursionLabel.setVisible(true);
                    hotelLabel.setVisible(false);
                    transferLabel.setVisible(false);
                    clientNameLabel.setVisible(false);
                    excursion.setVisible(true);
                    hotel.setVisible(false);
                    transfer.setVisible(false);
                    clientName.setVisible(false);
                    hotel.setValue(null);
                    transfer.setValue(null);
                    clientName.setValue(null);
                    break;
                case Transfer:
                    hotelBox.setVisible(false);
                    excursionBox.setVisible(false);
                    clientNameBox.setVisible(false);
                    transferTypeBox.setVisible(true);
                    excursionLabel.setVisible(false);
                    hotelLabel.setVisible(false);
                    transferLabel.setVisible(true);
                    clientNameLabel.setVisible(false);
                    excursion.setVisible(false);
                    hotel.setVisible(false);
                    transfer.setVisible(true);
                    clientName.setVisible(false);
                    excursion.setValue(null);
                    hotel.setValue(null);
                    clientName.setValue(null);
                    break;
                case Other:
                    hotelBox.setVisible(false);
                    excursionBox.setVisible(false);
                    clientNameBox.setVisible(false);
                    transferTypeBox.setVisible(false);
                    excursionLabel.setVisible(false);
                    hotelLabel.setVisible(false);
                    transferLabel.setVisible(false);
                    clientNameLabel.setVisible(false);
                    excursion.setVisible(false);
                    hotel.setVisible(false);
                    transfer.setVisible(false);
                    clientName.setVisible(false);
                    excursion.setValue(null);
                    hotel.setValue(null);
                    transfer.setValue(null);
                    clientName.setValue(null);
                    break;
            }
        }
    }

    private Cost loadCost() { //Поиск Цены по коду
        LoadContext loadContext = new LoadContext(Cost.class)
                .setView("edit");
        //loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.startDate <= :date and c.project.id is null) and e.product.id = :product")
        loadContext.setQueryString("select c from crm$Cost c where c.product.id = :product and c.startDate <= :date and c.project.id is null order by c.startDate desc")
                .setParameter("product", getItem().getProduct())
                .setParameter("date", java.util.Calendar.getInstance().getTime());
        return dataManager.load(loadContext);
    }

    /*
    private CharacteristicType loadCharacteristicType() { //Поиск Типа Характеристики по кодуin
        LoadContext loadContext = new LoadContext(CharacteristicType.class)
                .setView("edit");
        //loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.startDate <= :date and c.project.id is null) and e.product.id = :product")
        loadContext.setQueryString("select c from crm$CharacteristicType c where c.id = :charTypeId")
                .setParameter("charTypeId", getItem().getProduct().getCharacteristic().getCharacteristicType().getId());
        return dataManager.load(loadContext);
    }
    */

    private List<Cost> loadProjectCost() { //Поиск Цены по коду
        LoadContext loadContext = new LoadContext(Cost.class)
                .setView("edit");
        loadContext.setQueryString("select e from crm$Cost e where e.startDate = (select max(c.startDate) from crm$Cost c where c.product.id = :product and c.project.id = :id and c.startDate <= :date) and e.product.id = :product and e.project.id = :id")
                .setParameter("product", getItem().getProduct())
                .setParameter("date", java.util.Calendar.getInstance().getTime())
                .setParameter("id", getItem().getOrdDoc().getProject());
        return dataManager.loadList(loadContext);
    }

    private UUID getExhibitSpace(UUID Id) {
        UUID exhibitSpaceId = null;
        LoadContext loadContext = new LoadContext(ExtProject.class).setView("browse");
        loadContext.setQueryString("select e from crm$Project e where e.id = :Id").setParameter("Id", Id);
        ExtProject extProject = dataManager.load(loadContext);
        if(extProject != null && extProject.getExhibitSpace() != null) {
            exhibitSpaceId = extProject.getExhibitSpace().getId();
        }
        return exhibitSpaceId;
    }

    private void createResourceWindow() {
        OrderDetail orderDetail = getOrderDetail();
        if (orderDetail == null) {
            showNotification(getMessage("Сохраните услугу!"), NotificationType.HUMANIZED);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        OrdResource newEntity = metadata.create(OrdResource.class);
        newEntity.setOrderDetail(getOrderDetail());
        Window window = openEditor("crm$OrdResource.edit", newEntity, WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                resourceDs.refresh();
            }
        });
    }

    public void createResource(Component source) {
        createResourceWindow();
    }

    private OrderDetail getOrderDetail() {
        LoadContext loadContext = new LoadContext(OrderDetail.class).setView("edit");
        loadContext.setQueryString("select e from crm$OrderDetail e where e.id = :Id").setParameter("Id", getItem().getId());
        OrderDetail orderDetail = dataManager.load(loadContext);
        return orderDetail;
    }

    public void saveButton() {
        commitAndClose();
    }

    public void closeButton() {
        closeMyWindow("close");
    }

    private void closeMyWindow(String actionId) {
        resourceDs.setAllowCommit(false);
        detailDs.setAllowCommit(false);
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }

    @Override
    public void closeAndRun(String actionId, Runnable runnable) {
        closeMyWindow("close");
    }


    private void componentSecondaryVisible(){
        OrderDetail item = getItem();
        if (item.getProduct() != null) {
            taxLb.setValue(item.getProduct().getNomenclature().getTax());
            item.setTax(item.getProduct().getNomenclature().getTax());
        }

        if (item.getProduct() != null && item.getProduct().getSecondaryUnit() != null) {
            secondaryUnit.setValue(item.getProduct().getSecondaryUnit().getName_ru());
            secondaryAmount.setVisible(true);
            secondaryLabel.setVisible(true);
            secondaryUnit.setVisible(true);
        } else {
            secondaryUnit.setValue(null);
            secondaryAmount.setVisible(false);
            secondaryLabel.setVisible(false);
            secondaryUnit.setVisible(false);
        }

        if (getItem().getOrdDoc().getTemplate()) {
            discountBox.setVisible(false);
            altNameBox.setVisible(false);
            taxHBox.setVisible(false);
            totalSumHBox.setVisible(false);
        }
    }

    //Блокирование и разблокирование цены при изменении цены
    private void initProduct() {
        OrderDetail item = getItem();
        if (item.getProduct() != null) {
            setNullFields();
            editableCost();
            setAlternateNames();
            costCalculation();
        }
    }

    protected void setAmount() {
        OrderDetail item = getItem();
        if (item.getAmount() == null) {
            BigDecimal am = BigDecimal.ONE;
            if (item.getProduct().getMinQuantity() != null) {
                am = item.getProduct().getMinQuantity();
            }
            item.setAmount(am);
        }
    }

    protected void setSecondaryAmount() {
        OrderDetail item = getItem();
        if (item.getSecondaryAmount() == null || item.getProduct().getSecondaryUnit() == null) {
            item.setSecondaryAmount(BigDecimal.valueOf(1));
        }
    }

    protected BigDecimal getCost() {
        Cost costBase = loadCost();
        List<Cost> costProjectList = loadProjectCost();
        List<Cost> resultCostList = new ArrayList<>();
        resultCostList.addAll(costProjectList);
        resultCostList.add(costBase);
        BigDecimal i = BigDecimal.ZERO;
        if (resultCostList.get(0) != null) {
            OrderDetail item = getItem();
            if (item.getOrdDoc().getCurrency().getCode().equals("643")) {
                if (resultCostList.get(0).getPrimaryCost() != null) {
                    i = resultCostList.get(0).getPrimaryCost();
                }
            } else {
                if (resultCostList.get(0).getSecondaryCost() != null) {
                    i = resultCostList.get(0).getSecondaryCost();
                }
            }
        }
        return i;
    }

    private void componentListener() {
        product.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                componentSecondaryVisible();
                setTourismFields();
                initProduct();
            }
        });
        discountType.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                costCalculation();
            }
        });
        clientName.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setAlternateNames();
            }
        });
        hotel.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setAlternateNames();
            }
        });
        excursion.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setAlternateNames();
            }
        });
        transfer.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setAlternateNames();
            }
        });
        amount.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {  //Изменение количества
                if (value!=null)costCalculation();
            }
        });
        costPerPiece.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (value!=null)costCalculation();
            }
        });
        marginSum.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) { costCalculation(); }
        });
        discountSum.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) { costCalculation(); }
        });
        secondaryAmount.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {  //Изменение количества
                if (value!=null) {
                    costCalculation();
                }
            }
        });
    }

}