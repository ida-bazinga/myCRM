package com.haulmont.thesis.crm.web.emailActivity;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DatasourceListener;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.entity.EmailActivity;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

public class EmailActivityEditor<T extends EmailActivity> extends AbstractEditor<T> {

    @Named("cardDs")
    protected Datasource cardDs;

    @Inject
    private DataManager dataManager;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void initNewItem(T item) {
        item.setCreateTs(java.util.Calendar.getInstance().getTime());
        item.setCampaign(null);
    }

    @Override
    protected void postInit() {

        cardDs.addListener(new DatasourceListener() {
            @Override
            public void itemChanged(Datasource ds, @Nullable Entity prevItem, @Nullable Entity item) {

            }

            @Override
            public void stateChanged(Datasource ds, Datasource.State prevState, Datasource.State state) {

            }

            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                if ("contactPerson".equals(property)) {
                        getItem().setAddress(communicationsAddress());
                }
            }
        });
    }

    private String communicationsAddress() {
        LoadContext loadContext = new LoadContext(Communication.class).setView("with-contact");
        loadContext.setQueryString("select e from crm$Communication e where e.contactPerson.id = :contactPerson\n" +
                "                 and e.commKind.communicationType = :communicationType")
                .setParameter("contactPerson", getItem().getContactPerson().getId())
                .setParameter("communicationType", CommunicationTypeEnum.email)
                .setMaxResults(1);
        List<Communication> list = dataManager.loadList(loadContext);
        if(list.size() > 0)
           return list.get(0).getAddress() != null ? list.get(0).getAddress() : list.get(0).getMainPart();
        return "";
    }

}