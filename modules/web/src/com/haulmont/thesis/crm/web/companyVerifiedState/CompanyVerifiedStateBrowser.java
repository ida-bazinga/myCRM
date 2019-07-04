package com.haulmont.thesis.crm.web.companyVerifiedState;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.entity.Correspondent;
import com.haulmont.thesis.crm.entity.CompanyVerifiedState;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompanyVerifiedStateBrowser extends AbstractLookup {

    @Inject
    protected UserSession userSession;
    @Named("mainTable")
    protected GroupTable mainTable;
    @Named("mainDs")
    protected CollectionDatasource<CompanyVerifiedState, UUID> mainDs;

    @Override
    public void init(Map<String, Object> params) {
        userSession.getCurrentOrSubstitutedUser().getName();
        mainTable.addAction(EditAction());
    }

    protected Action EditAction()  {
        return new EditAction(mainTable) {

            @Override
            public void actionPerform(Component component) {
                openWinEdit();
            }
        };
    }

    public void onEditbtnClick(Component source) {
        openWinEdit();
    }

    private void openWinEdit() {
        if (mainTable.getSelected().size() < 1) {
            String txtMesage = "Не выбран Контрагент!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        CompanyVerifiedState companyVerifiedState = mainTable.getSingleSelected();
        Map<String, Object> params = new HashMap<>();
        Correspondent newEntity = companyVerifiedState.getCorrespondent();
        Window window = openEditor("df$Company.edit", newEntity, WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                mainDs.refresh();
            }
        });
    }

    public void verifiedState(Component source) {
        if (mainTable.getSelected().size() < 1) {
            String txtMesage = "Не выбран Контрагент!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("companyVerifiedState", mainTable.getSingleSelected());
        final Window window = openWindow("crm$CompanyVerifiedState.message", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                mainDs.refresh();
            }
        });
    }

}