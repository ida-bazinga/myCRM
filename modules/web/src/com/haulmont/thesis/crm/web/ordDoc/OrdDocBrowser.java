package com.haulmont.thesis.crm.web.ordDoc;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.entity.InvDoc;
import com.haulmont.thesis.crm.entity.OrdDoc;
import com.haulmont.thesis.crm.web.invDoc.surcharge.SurchargeInvoice;
import com.haulmont.thesis.crm.web.ordDoc.copy.CopyOrdDoc;
import com.haulmont.thesis.crm.web.ui.basicdoc.SalesAbstractDocBrowser;
import com.haulmont.thesis.crm.web.ui.common.actions.CreatePackageSalesDocAction;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesActAction;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesInvoiceAction;
import com.haulmont.thesis.crm.web.ui.common.actions.RemoveDocAction;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class OrdDocBrowser<T extends OrdDoc> extends SalesAbstractDocBrowser<OrdDoc> {

    @Inject
    protected Metadata metadata;
    @Named("additionalCreateBtn")
    protected PopupButton additionalCreateBtn;

    protected OrdDoc newOrdDoc;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "crm$OrdDoc";

        cardsTable = getComponentNN("cardsTable");
        if (cardsTable == null) {
            throw new IllegalStateException("Table not found");
        }
        if (cardsDs == null) {
            throw new IllegalStateException("Datasource not found");
        }

        //inTemplates = params.get("inTemplates") != null ? (boolean)params.get("inTemplates") : false;

        createButton.getAction("openWindowsCopyOrdDoc").setVisible(!inTemplates);


        if (!inTemplates) {
            CreateSalesInvoiceAction createInvoiceAction = new CreateSalesInvoiceAction(this);
            additionalCreateBtn.addAction(createInvoiceAction);
            cardsTable.addAction(createInvoiceAction);

            CreateSalesActAction createActAction = new CreateSalesActAction(this);
            additionalCreateBtn.addAction(createActAction);
            cardsTable.addAction(createActAction);
            //массовое выставление актов и счет-фактур
            additionalCreateBtn.addAction(new CreatePackageSalesDocAction(this, cardsTable));

            //удаление
            cardsTable.addAction(RemoveAction());
            cardsTable.addAction(createCopyAction());

            additionalCreateBtn.addAction(SurchargeAction());
        }
    }

    public void onRemovebtnClick() {
        optionDialog();
    }

    protected Action RemoveAction()  {
        return new RemoveAction(cardsTable) {
            @Override
            public void actionPerform(Component component) {
                optionDialog();
            }
        };
    }

    private void removeItem() {
        if (!inTemplates) {
            Set<OrdDoc> ordDocSet = cardsTable.getSelected();
            if (ordDocSet.size() > 0) {

                RemoveDocAction removeDocAction = new RemoveDocAction();
                removeDocAction.removeOrd(ordDocSet);
                showNotification(getMessage(removeDocAction.txtMesage), IFrame.NotificationType.HUMANIZED);
                cardsDs.refresh();
            }
        } else {
                showNotification("Шаблон нельзя удалить!", IFrame.NotificationType.HUMANIZED);
        }
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
                    return OrdDocBrowser.this.getStyleName(entity);
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

    @Override
    protected Action createCopyAction() {
        return new AbstractAction("openWindowsCopyOrdDoc") {
            @Override
            public void actionPerform(Component component) {
                OrdDoc ordDoc = cardsTable.getSingleSelected();
                if (ordDoc != null) {
                    openWindowsCopyParams("crm$CopyOrdDoc.edit", ordDoc);
                }
            }
            @Override
            public String getCaption() {
                return  messages.getMessage(this.getClass(), "actions.Copy");
            }
        };
    }

    protected void openWindowsCopyParams(String windowAlias, final OrdDoc ordDoc) {
        final Map<String, Object> params = new HashMap<>();
        params.put("ordDoc", ordDoc);
        final Window window = openWindow(windowAlias, WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    ExtCompany extCompany = window.getContext().getParamValue("company");
                    ExtProject extProject = window.getContext().getParamValue("project");
                    if (extCompany != null && extProject != null) {
                        params.put("company", extCompany);
                        params.put("project", extProject);
                        params.put("ordDoc", ordDoc);
                        copyOrdDocObject(params);
                    }
                }
            }
        });
    }

    protected void copyOrdDocObject(Map<String, Object> params) {
        CopyOrdDoc copyOrdDoc = new CopyOrdDoc(params); //инициализация копирования
        Map<String, Object> copy = copyOrdDoc.copy(); //копирование
        newOrdDoc = (OrdDoc)copy.get("ordDoc");
        if (copy.get("error") != null) {
            showNotification(getMessage(String.format("%s", copy.get("error"))), IFrame.NotificationType.HUMANIZED);
            return;
        }
        if (newOrdDoc != null) {
            List<String> notCreateOrdDetail = (List<String>) copy.get("notCreateOrdDetail");
            if (notCreateOrdDetail.size() > 0) {
                String message = "Список услуг не скопрированных в заказ, так как нет цены!";
                for (String value : notCreateOrdDetail) {
                    message = message + "\n" + value;
                }
                optionDialogCopy(message);
                return;
            }
            openCopyEditor();
        }
    }

    protected void optionDialogCopy(String message) {
        showOptionDialog(
                "Внимание!",
                message,
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.OK) {
                            @Override
                            public void actionPerform(Component component) {
                                openCopyEditor();
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

    protected void openCopyEditor(){
        Map<String, Object> params = new HashMap<>();
        params.put("copy", true);
        Window window = openEditor("crm$OrdDoc.edit", this.newOrdDoc, WindowManager.OpenType.THIS_TAB, params);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                    cardsDs.refresh();
                }
            }
        });
    }

    protected Action SurchargeAction()  {
        return new AbstractAction("surcharge") {
            @Override
            public void actionPerform(Component component) {
                if (cardsTable.getSingleSelected() == null) {
                    showNotification(messages.getMessage("com.haulmont.thesis.crm.web.invDoc.surcharge", "noOrd"), NotificationType.HUMANIZED);
                    return;
                }
                final Window window = openWindow("crm$InvSurcharge", WindowManager.OpenType.DIALOG,  Collections.<String, Object>singletonMap("ordDoc", cardsTable.getSingleSelected()));
                window.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                            BigDecimal sum = window.getContext().getParamValue("sum");
                            Map<String, Object> params = new HashMap<>();
                            params.put("ordDoc", cardsTable.getSingleSelected());
                            params.put("sum", sum);
                            SurchargeInvoice surchargeInvoice = new SurchargeInvoice(params);
                            InvDoc invoice = surchargeInvoice.getInvSurcharge();
                            Window window = openEditor("crm$InvDoc.edit", invoice, WindowManager.OpenType.THIS_TAB);
                        }
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("invSurcharge");
            }



        };
    }


}