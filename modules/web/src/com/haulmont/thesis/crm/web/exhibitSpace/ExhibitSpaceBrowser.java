package com.haulmont.thesis.crm.web.exhibitSpace;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.EvotorService;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;

import javax.inject.Inject;
import java.util.Map;

public class ExhibitSpaceBrowser extends AbstractLookup {

    @Inject
    protected EvotorService evotorService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void getStoreInfo(){
        try {
            showMessageDialog("Evotor store info",evotorService.getStoresInfo(),MessageType.CONFIRMATION);
        } catch (ServiceNotAvailableException e) {
            showNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

}