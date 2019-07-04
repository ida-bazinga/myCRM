package com.haulmont.thesis.crm.web.ui.activityresult;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.crm.entity.ActivityRes;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityResBrowser extends AbstractLookup {

    protected static final String WINDOW_ID = "activityResultCampaignKinds";

    protected Map<String, Object> paramsMap = new HashMap<>();

    @Named("rowsDs")
    protected CollectionDatasource<ActivityRes, UUID> mainDs;
    @Named("rowsTable")
    protected GroupTable mainTable;

    @Inject
    protected Metadata metadata;

    public void ready() {
        mainTable.expandAll();
    }

    public void OpenKindsEditor() {
        openDialogEditor();
    }

    protected void openDialogEditor() {
        ActivityRes result = mainTable.getSingleSelected();
        if (result != null) {
            App.getInstance().getWindowManager().getDialogParams().setWidth(400);
            final Window window = openEditor(WINDOW_ID, result, WindowManager.OpenType.DIALOG, paramsMap);
            window.addListener(new Window.CloseListener() {
                @Override
                public void windowClosed(String actionId) {
                    if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                        ActivityRes item = (ActivityRes) ((Editor) window).getItem();
                        if (mainDs != null && item != null)
                            mainDs.updateItem(item);
                    }
                }
            });
        } else {
            showNotification(getMessage("notSelectedActivityResult"), NotificationType.HUMANIZED);
        }
    }
}