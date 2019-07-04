package com.haulmont.thesis.crm.web.cost;

import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.Cost;
import com.haulmont.thesis.crm.entity.CostTypeEnum;
import com.haulmont.thesis.crm.entity.Currency;
import com.haulmont.thesis.web.ui.tools.AppIntegrationTools;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class CostEditor<T extends Cost> extends AbstractEditor<T> {

    @Inject
    protected AppIntegrationTools integrationTools;

    @Inject
    private DataManager dataManager;

    @Inject
    private UniqueNumbersService un;

    @Inject
    protected LookupPickerField project;

    @Inject
    protected LookupField costType;

    @Inject
    protected TextField unit, secondaryUnit;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        initAppIntegrationComponents();
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        project.setRequired(false);
        project.setEnabled(false);
        costType.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem().getCostType()!=null && getItem().getCostType()==CostTypeEnum.project) {
                    project.setRequired(true);
                    project.setEnabled(true);
                }
                else {
                    project.setRequired(false);
                    project.setEnabled(false);
                }
            }
        });
        initDialogParams();
    }

    protected void initDialogParams() {
        getDialogParams().setWidth(1000);
        getDialogParams().setHeight(800);
        getDialogParams().setResizable(true);
    }

    @Override
    protected void postInit() {
        if (getItem() != null) {
            Cost item = getItem();
            if (item.getProduct() != null && item.getProduct().getNomenclature().getUnit().getName_ru() != null) {
                unit.setValue(item.getProduct().getNomenclature().getUnit().getName_ru());
            }
            if (item.getProduct() != null && item.getProduct().getCharacteristic() != null && item.getProduct().getCharacteristic().getCharacteristicType() != null
                    && item.getProduct().getCharacteristic().getCharacteristicType().getSecondUnit() != null) {
                secondaryUnit.setValue(item.getProduct().getCharacteristic().getCharacteristicType().getSecondUnit().getName_ru());
            }
            List<Currency> cur = loadCurrency("840");    //Валюта
            if (!cur.isEmpty()) item.setTerniaryCurrency(cur.get(0));
        }

    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem())){
            Long l = un.getNextNumber("Cost");
            getItem().setCode(String.format("%05d",l));
        }
        return true;
    }

    @Override
    protected void initNewItem(T item) {    //Значения для новой записи
        super.initNewItem(item);
        List<Currency> cur = loadCurrency("643");    //Валюта
        if (!cur.isEmpty()) item.setPrimaryCurrency(cur.get(0));
        cur = loadCurrency("978");    //Валюта
        if (!cur.isEmpty()) item.setSecondaryCurrency(cur.get(0));
        cur = loadCurrency("840");    //Валюта
        if (!cur.isEmpty()) item.setTerniaryCurrency(cur.get(0));
        if (item.getCostType() == null) {
            item.setCostType(CostTypeEnum.project);
        }
    }

    private List<Currency> loadCurrency(String code) { //Поиск валюты по коду
        LoadContext loadContext = new LoadContext(Currency.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$Currency e where e.code = :code")
                .setParameter("code", code);
        return dataManager.loadList(loadContext);
    }

    protected void initAppIntegrationComponents() {
        integrationTools.initAppIntegrationComponents(this);
    }

    public void enqueueToExport() {
        integrationTools.enqueueToExport(this);
    }
}