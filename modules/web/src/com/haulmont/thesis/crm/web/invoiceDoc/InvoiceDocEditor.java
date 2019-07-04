package com.haulmont.thesis.crm.web.invoiceDoc;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.entity.InvoiceDoc;
import com.haulmont.thesis.crm.entity.Log1C;
import com.haulmont.thesis.crm.entity.OrderDoc;
import com.haulmont.thesis.crm.entity.OrderStatus;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class InvoiceDocEditor<T extends InvoiceDoc> extends AbstractEditor<T> {

    //@Inject
    //protected AppIntegrationTools integrationTools;

    @Inject
    private DataManager dataManager, dm;

    @Inject
    private Label integrationStateLabel;

    @Inject
    protected UserSession userSession;

    @Inject
    private CollectionDatasource log1cDs;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        //initAppIntegrationComponents();
    }

    @Override
    protected void initNewItem(T item) {    //Значения для новой записи
        super.initNewItem(item);
        item.setOutboundDate(new Date());   //Значение заполняется текущей датой
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() { //Обработчик: после загрузки карточки
        if(getItem().getStatus()!=null && !getItem().getStatus().getCode().equals("new")){
            integrationStateLabel.setValue(getItem().getStatus().getName_ru());
        }
    }

    public void export1cBtn() {

        if(getItem().getStatus()!=null){
            InvoiceDoc item = getItem();
            List<OrderStatus> orderStatus = loadOrderStatus(); //Статус
            if (!orderStatus.isEmpty()) {
                item.setStatus(orderStatus.get(0));
                OrderDoc order = item.getOrderDoc();
                order.setStatus(orderStatus.get(0));
                CommitContext commitContext = new CommitContext(order);
                dm.commit(commitContext);
                integrationStateLabel.setValue(item.getStatus().getName_ru());
            }

            //отправить к обмену
            boolean isCompany = createItemLog1c(getItem().getCompany().getId(), getItem().getCompany().getMetaClass().getName());

            if (isCompany)
            {
                if (getItem().getContract() != null) {
                   createItemLog1c(getItem().getContract().getId(), getItem().getContract().getMetaClass().getName());
                }

                createItemLog1c(getItem().getId(), getItem().getMetaClass().getName());

                log1cDs.refresh();
            }

        }
    }

    private boolean createItemLog1c (UUID entityId, String entityName)
    {
        try {

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    private List<OrderStatus> loadOrderStatus() {
        LoadContext loadContext = new LoadContext(OrderStatus.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$OrderStatus e where e.code = :code")
                .setParameter("code", "to1c");
        return dataManager.loadList(loadContext);
    }

/*
    protected void initAppIntegrationComponents() {
        integrationTools.initAppIntegrationComponents(this);
    }

    public void enqueueToExport() {
        integrationTools.enqueueToExport(this);
    }*/

    
}