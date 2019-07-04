package com.haulmont.thesis.crm.web.ui.cashmachine;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.EvotorService;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class CashMachineBrowser extends AbstractLookup {

    @Named("maсhinesTable")
    protected Table mainTable;
    @Inject
    protected EvotorService evotorService;

    @Override
    public void init(Map<String, Object> params){
        super.init(params);
        mainTable.addAction(initRefreshAction());
        getDevicesFromCloud();
    }

    protected Action initRefreshAction(){
        return new BaseAction("refresh") {
            @Override
            public void actionPerform(Component component) {
                getDevicesFromCloud();
            }
        };
    }

    protected void getDevicesFromCloud(){
        try {
            evotorService.updateDevices();
            getDsContext().refresh();
        } catch (ServiceNotAvailableException e) {
            showNotification(e.getMessage(), NotificationType.WARNING);
        }
    }
}