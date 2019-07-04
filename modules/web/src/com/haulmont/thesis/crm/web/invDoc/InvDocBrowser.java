package com.haulmont.thesis.crm.web.invDoc;

import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.thesis.crm.entity.InvDoc;
import com.haulmont.thesis.crm.web.ui.basicdoc.SalesAbstractDocBrowser;
import com.haulmont.thesis.crm.web.ui.common.actions.RemoveDocAction;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class InvDocBrowser<T extends InvDoc> extends SalesAbstractDocBrowser<InvDoc> {

    /*
    @Named("additionalCreateBtn")
    protected PopupButton additionalCreateBtn;
    */


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "crm$InvDoc";
        createButton.getAction("copy").setVisible(!inTemplates);
        /*
        CreateSalesActFromInvoiceAction createActAction = new CreateSalesActFromInvoiceAction(this);
        additionalCreateBtn.addAction(createActAction);
        cardsTable.addAction(createActAction);
        */
        //удаление
        if(!inTemplates) {
            cardsTable.addAction(RemoveAction());
        }
    }

    public void onRemovebtnClick() {
        optionDialog();
    }

    private void removeItem() {
        if (!inTemplates) {
            Set<InvDoc> invDocSet = cardsTable.getSelected();
            if (invDocSet.size() > 0) {
                RemoveDocAction removeDocAction = new RemoveDocAction();
                removeDocAction.removeInv(invDocSet);
                showNotification(getMessage(removeDocAction.txtMesage), IFrame.NotificationType.HUMANIZED);
                cardsDs.refresh();
            }
        } else  {
            showNotification("Шаблон нельзя удалить!", IFrame.NotificationType.HUMANIZED);
        }
    }

    protected Action RemoveAction()  {
        return new RemoveAction(cardsTable) {
            @Override
            public void actionPerform(Component component) {
                optionDialog();
            }
        };
    }

    protected void optionDialog() {
        final String messagesPackage = AppConfig.getMessagesPack();
        showOptionDialog(
                messages.getMessage(messagesPackage, "dialogs.Confirmation"),
                messages.getMessage(messagesPackage, "dialogs.Confirmation.Remove"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY) {
                            @Override
                            public void actionPerform(Component component) {
                                removeItem();
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL) {
                            @Override
                            public void actionPerform(Component component) {
                                // move focus to owner
                                //selected.requestFocus();
                            }
                        }
                }
        );
    }

    @Override
    protected void initTableColoring() {
        cardsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null && !inTemplates)
                    return InvDocBrowser.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(T entity) {
        if (entity.getIntegrationResolver() != null) {
            boolean isDel = entity.getIntegrationResolver().getDel() != null ? entity.getIntegrationResolver().getDel() : false;
            if (isDel) {
                return "thesis-task-finished";
            }
        }
        return null;
    }

}