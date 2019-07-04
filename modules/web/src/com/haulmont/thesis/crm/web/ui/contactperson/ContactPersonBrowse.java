package com.haulmont.thesis.crm.web.ui.contactperson;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class ContactPersonBrowse extends AbstractLookup {
    @Named("mainTable")
    protected Table mainTable;

    public void addEmailTargets() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedContactPersons", mainTable.getSelected());
        Window window = openWindow("crm$SelectMailCampaignIndividualsWindow", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
            }
        });
    }
}