package com.haulmont.thesis.crm.web.ui.softphoneSession;

import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneSessionManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class SoftphoneSessionBrowser extends AbstractLookup {

    @Named("softPhoneSessionTable")
    protected Table sessionTable;

    @Inject
    protected SoftPhoneSessionManager sessionManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        sessionTable.addAction(RemoveAction());
    }

    protected Action RemoveAction()  {
        return new RemoveAction(sessionTable) {
            @Override
            public void actionPerform(Component component) {
                SoftPhoneSession session = target.getSingleSelected();
                if (session != null)
                    optionDialog(session);
            }
        };
    }

    protected void optionDialog(final SoftPhoneSession session) {
        final String messagesPackage = AppConfig.getMessagesPack();
        showOptionDialog(
                messages.getMessage(messagesPackage, "dialogs.Confirmation"),
                messages.getMessage(messagesPackage, "dialogs.Confirmation.Remove"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY) {
                            @Override
                            public void actionPerform(Component component) {
                                sessionManager.removeSession(session.getId().toString());
                                sessionTable.refresh();
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL) {
                            @Override
                            public void actionPerform(Component component) {
                            }
                        }
                }
        );
    }
}