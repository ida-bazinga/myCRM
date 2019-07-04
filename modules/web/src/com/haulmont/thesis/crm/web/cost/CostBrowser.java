package com.haulmont.thesis.crm.web.cost;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.web.gui.components.WebFilter;
import com.haulmont.thesis.crm.entity.Cost;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.web.cost.copy.CopyCost;
import com.haulmont.thesis.web.actions.ThesisExcelAction;

import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CostBrowser extends AbstractLookup {

    protected GroupTable table;

    @Named("mainTable.create")
    protected CreateAction mainTableCreateAction;

    @Named("mainTable.edit")
    protected EditAction mainTableEditAction;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        WindowManager.OpenType openType = (WindowManager.OpenType) params.get("openType");
        mainTableCreateAction.setOpenType(openType == null ? WindowManager.OpenType.THIS_TAB : openType);
        mainTableEditAction.setOpenType(openType == null ? WindowManager.OpenType.THIS_TAB : openType);

        table = getComponent("mainTable");
        Button excel = getComponent("excelBtn");
        if (excel != null) {
            excel.setAction(new ThesisExcelAction(table, (WebFilter) this.getComponent("genericFilter")));
        }
    }

    public void copy() {
        Set<Cost> costSet = table.getSelected();
        if (costSet.size() > 0) {
            openWindowsCopyParams(costSet);
        } else {
            showNotification(getMessage("msgCopyMessageInfo"), NotificationType.HUMANIZED);
        }
    }

    protected void openWindowsCopyParams(final Set<Cost> costSet) {
        final Map<String, Object> params = new HashMap<>();
        final Window window = openWindow("crm$CostCopy", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    Date startDate = window.getContext().getParamValue("startDate");
                    ExtProject extProject = window.getContext().getParamValue("project");
                    if (startDate != null && extProject != null) {
                        params.put("startDate", startDate);
                        params.put("project", extProject);
                        params.put("costSet", costSet);
                        CopyCost copyCost = new CopyCost(params);
                        boolean isCopy =  copyCost.copy();
                        if (isCopy) {
                            showNotification(getMessage("copyInfo"), NotificationType.HUMANIZED);
                        } else {
                            showNotification(getMessage("copyInfoError"), NotificationType.HUMANIZED);
                        }
                    }
                }
            }
        });
    }

}