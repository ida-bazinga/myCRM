/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.baseactivity;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.crm.entity.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class ActivityCreatorCloseListener<T extends BaseActivity> implements Window.CloseListener {
    ActivityHolderWindow creator;
    WindowManager.OpenType openType;
    String activityMetaClassName;
    CollectionDatasource dataSource;
    WindowConfig windowConfig;

    public ActivityCreatorCloseListener(ActivityHolderWindow creator, WindowManager.OpenType openType, String activityMetaClassName,
                                        CollectionDatasource dataSource){
        this.creator = creator;
        this.openType = openType;
        this.activityMetaClassName = activityMetaClassName;
        this.dataSource = dataSource;
        windowConfig = AppBeans.get(WindowConfig.NAME);
    }

    @Override
    public void windowClosed(String actionId) {
        if(Window.Editor.WINDOW_COMMIT.equals(actionId)){
            T activity = creator.getActivity();
            final Window.Editor window = App.getInstance().getWindowManager().openEditor(windowConfig.getWindowInfo(getWindowId()),
                    activity, openType, getEditorParams());

            if (dataSource != null)
                window.addListener(new Window.CloseListener() {
                    public void windowClosed(String actionId) {
                        dataSource.refresh();
                    }
                });
        }
    }

    protected String getWindowId() {
        MetaClass metaClass = AppBeans.get(Metadata.class).getClassNN(activityMetaClassName);
        return windowConfig.getEditorScreenId(metaClass);
    }

    protected Map<String, Object> getEditorParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("justCreated", true);
        return params;
    }
}
