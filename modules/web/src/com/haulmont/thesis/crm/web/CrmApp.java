package com.haulmont.thesis.crm.web;

import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.cuba.web.Connection;
import com.haulmont.cuba.web.gui.WebUIPaletteManager;
import com.haulmont.thesis.crm.web.app.mainwindow.CrmMainWindow;
import com.haulmont.thesis.crm.web.gui.components.CrmComponentPalette;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.haulmont.thesis.crm.web.softphone.core.entity.EventMessage;
import com.haulmont.thesis.web.DocflowApp;

import javax.annotation.Nonnull;
import java.util.UUID;

public class CrmApp extends DocflowApp {

    static {
        // Register gui palettes
        WebUIPaletteManager.registerPalettes(new CrmComponentPalette());
    }

    public static CrmApp getInstance() {
        return (CrmApp) App.getInstance();
    }

    public CrmApp() {
        super();
    }

    public boolean isSPIInit(@Nonnull UUID sessionId){

        UUID storedSessionId = userSessionSource.getUserSession().getAttribute(SoftphoneConstants.SESSION_ID_ATTR_NAME);

        return storedSessionId != null && storedSessionId.equals(sessionId);
    }

    @Override
    public void userSubstituted(Connection connection) {
        for (final AppUI ui : getAppUIs()) {
            ui.accessSynchronously(new Runnable() {
                @Override
                public void run() {
                    if (ui instanceof CrmAppUI) {
                        ((CrmAppUI) ui).removeAllSoftphoneListeners();
                    }

                    if(getAppWindow().getMainWindow() instanceof CrmMainWindow){
                        ((CrmMainWindow)getAppWindow().getMainWindow()).disposeSPIPanel();
                    }
                }
            });
        }
        super.userSubstituted(connection);
    }
}
