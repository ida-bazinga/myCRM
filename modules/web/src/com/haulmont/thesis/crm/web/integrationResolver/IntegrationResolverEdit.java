package com.haulmont.thesis.crm.web.integrationResolver;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.StatusDocSales;
import com.haulmont.workflow.core.entity.Card;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class IntegrationResolverEdit<T extends IntegrationResolver> extends AbstractEditor<T> {

    @Named("mainDs")
    protected Datasource<T> mainDs;

    protected Work1C work1C;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        mainDsListener();
    }

    @Override
    protected void postInit() {
        if (!PersistenceHelper.isNew(getItem())) {
            getComponent("sumPayment").setVisible("crm$OrdDoc".equals(getItem().getEntityName()) ||
                    "crm$InvDoc".equals(getItem().getEntityName()));
            getComponent("posted").setVisible("crm$OrdDoc".equals(getItem().getEntityName()) ||
                    "crm$InvDoc".equals(getItem().getEntityName()) ||
                    "crm$AcDoc".equals(getItem().getEntityName()));
            getComponent("entityName").setEnabled(false);
            getComponent("extSystem").setEnabled(false);
        }

        work1C = AppBeans.get(Work1C .class);
        work1C.setExtSystem(getItem().getExtSystem());
    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem())) {
            if(!isInsert()) {
                showNotification(getMessage(getMessage("addError")), NotificationType.HUMANIZED);
                return false;
            }
        }
        return true;
    }

    protected boolean isInsert() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("entityId", getItem().getEntityId());
        params.put("extSystem", getItem().getExtSystem());
        params.put("entityName", getItem().getEntityName());
        Work1C work1C = AppBeans.get(Work1C.class);
        IntegrationResolver ir = work1C.buildQuery(IntegrationResolver.class, params);
        if (ir != null) {
            return false;
        }
        return true;
    }

    protected void mainDsListener() {
        mainDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                switch (property) {
                    case "del":
                    case "stateDocSales":
                        boolean isDel = source.getDel() || StatusDocSales.DELETE.equals(source.getStateDocSales());
                        updateCardState(isDel);
                        break;
                }
            }
        });
    }

    private void updateCardState(boolean isDel) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "_local");
        params.put("id", getItem().getEntityId());
        Card card = work1C.buildQuery(Card.class, params);
        if (card != null) {
            String state = isDel ? "Помечен на удаление" : "";
            card.setState(state);
            commitIntegrationResolver(card);
        }
    }

    public void commitIntegrationResolver(Card card) {
        CommitContext commitContext = new CommitContext();
        commitContext.getCommitInstances().add(card);
        getDsContext().getDataSupplier().commit(commitContext);
        getDsContext().commit();
    }

}