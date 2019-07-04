/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.campaign;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.web.App;

import java.util.Map;

public class CreateCampaignMenuItemController implements Runnable{
    private Map<String, Object> params;

    public CreateCampaignMenuItemController(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public void run() {
        String metaClassName = (String) params.get("metaClassName");
        WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
        final CampaignCreatorWindow creator = App.getInstance().getWindowManager().openWindow(
                windowConfig.getWindowInfo("campaignCreator"),
                WindowManager.OpenType.DIALOG,
                params
        );
        creator.addListener(
                new CampaignCreatorCloseListener(creator, WindowManager.OpenType.NEW_TAB, metaClassName, null)
        );
    }
}
