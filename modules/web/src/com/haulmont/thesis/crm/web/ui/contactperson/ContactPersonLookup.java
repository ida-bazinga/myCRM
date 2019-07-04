package com.haulmont.thesis.crm.web.ui.contactperson;

import com.haulmont.cuba.gui.components.AbstractWindow;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.ExtContactPerson;

import javax.inject.Named;

public class ContactPersonLookup extends AbstractLookup {

    @Named("contactsDs")
    protected CollectionDatasource<ExtContactPerson, UUID> contactsDs;

    @Override
    public void init(Map<String, Object> params) {
        contactsDs.refresh(params);

        getDialogParams().setWidth(1000).setHeight(600).setResizable(true);
    }
}