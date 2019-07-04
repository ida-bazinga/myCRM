/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.baseactivity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.web.App;

import java.util.Map;

public class CreateActivityMenuItemController implements Runnable{
    private Map<String, Object> params;

    public CreateActivityMenuItemController(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public void run() {
        String metaClassName = (String) params.get("metaClassName");
        WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
        final ActivityHolderWindow creator = App.getInstance().getWindowManager().openWindow(
                windowConfig.getWindowInfo("activityCreator"),
                WindowManager.OpenType.DIALOG,
                params
        );
        creator.addListener(
                new ActivityCreatorCloseListener(creator, WindowManager.OpenType.DIALOG, metaClassName, null)
        );
    }
}