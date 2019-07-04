package com.haulmont.thesis.crm.web.staff;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractWindow;
import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.BoxLayout;

import javax.inject.Inject;

public class StaffEditor extends AbstractEditor {

    //@Inject
    //private BoxLayout supplierBox;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        if (!PersistenceHelper.isNew(getItem())) {
            //supplierBox.setVisible(false);
        }
    }
}