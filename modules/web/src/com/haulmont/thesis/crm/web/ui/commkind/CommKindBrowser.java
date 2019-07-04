package com.haulmont.thesis.crm.web.ui.commkind;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;

import javax.inject.Named;

public class CommKindBrowser extends AbstractLookup {

    @Named("rowsTable")
    protected GroupTable rowsTable;

    @Override
    public void ready() {
        rowsTable.expandAll();
    }
}