/*
 * Copyright (c) 2018 com.haulmont.thesis.web.individualbrowse
 */
package com.haulmont.thesis.crm.web.individual;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.web.ui.individual.IndividualBrowser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author a.donskoy
 */
public class ExtIndividualBrowse extends IndividualBrowser {

    public void addEmailTargets() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedIndividuals", individualsTable.getSelected());
        Window window = openWindow("crm$SelectMailCampaignIndividualsWindow", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
            }
        });
    }

}