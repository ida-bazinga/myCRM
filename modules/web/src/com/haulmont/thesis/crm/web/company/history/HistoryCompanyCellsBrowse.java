package com.haulmont.thesis.crm.web.company.history;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.crm.entity.CompanyCellsEnum;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.HistoryCompanyCells;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class HistoryCompanyCellsBrowse extends AbstractWindow {

    @Named("mainDs")
    protected CollectionDatasource mainDs;
    @Inject
    protected Metadata metadata;


    protected CompanyCellsEnum companyCellsEnum;
   private ExtCompany company;

    @Override
    public void init(Map<String, Object> params) {
        mainDs.refresh(params);
        this.company = (ExtCompany) params.get("company");
        this.companyCellsEnum = (CompanyCellsEnum) params.get("companyCells");
    }


    public void create() {
        App.getInstance().getWindowManager().getDialogParams().setWidth(300);
        Map<String, Object> params = new HashMap<>();
        HistoryCompanyCells entity = metadata.create(HistoryCompanyCells.class);
        entity.setCompany(this.company);
        entity.setCompanyCells(this.companyCellsEnum);
        entity.setStartDate(java.util.Calendar.getInstance().getTime());
        Window window = openEditor("crm$HistoryCompanyCells.edit", entity, WindowManager.OpenType.DIALOG, params);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                mainDs.refresh();
            }
        });
    }

}