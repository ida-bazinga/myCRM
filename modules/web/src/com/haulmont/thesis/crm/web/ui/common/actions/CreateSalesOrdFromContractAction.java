/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.OrdDoc;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.ui.common.SaveEntityAction;

/**
 * Created by d.ivanov on 26.06.2018
 */
public class CreateSalesOrdFromContractAction extends SaveEntityAction {

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;
    protected MyUtilsService myUtilsService;



    public CreateSalesOrdFromContractAction(IFrame frame) {
        super("createOrd", frame);
        this.frame = frame;
        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.orgStructureService = AppBeans.get(OrgStructureService.class);
        this.documentCopySupport = AppBeans.get(DocumentCopySupport.class);
        this.myUtilsService = AppBeans.get(MyUtilsService.class);
    }

    @Override
    public String getCaption() {
        return messages.getMessage(this.getClass(), "createSalesOrd.caption");
    }

    @Override
    public void actionPerform(Component component){
        if (frame instanceof Window.Editor)
            super.actionPerform(component);
        else {
            component.getClass();
            afterSaveEntity();
        }
    }

    protected void openEditor(OrdDoc ordDoc){
        Window window = frame.openEditor("crm$OrdDoc.edit",ordDoc, WindowManager.OpenType.THIS_TAB);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                //// TODO:
            }
        });
    }

    // TODO:
    protected OrdDoc getTemplate() {
        String templateName = "DefaultSalesOrd";
        //Preconditions.checkNotNullArgument(templateName, "Template name is not specified");

        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);

        return dataManager.load(loadContext);
    }



    protected OrdDoc copyFrom(Contract doc, OrdDoc template) {
        OrdDoc ordDoc = documentCopySupport.copy(template);
        ordDoc.setCompany((ExtCompany)doc.getContractor());
        ordDoc.setDocCategory(template.getDocCategory());
        ordDoc.setParentCard(doc);
        ordDoc.setContract(doc);
        ordDoc.setIntegrationResolver(null);
        return ordDoc;
    }

    @Override
    protected void afterSaveEntity() {
        Contract doc;
        if (frame instanceof Window.Editor){
            doc = (Contract) ((Window.Editor) frame).getItem();
        }else{
            doc = ((Table) frame.getComponentNN("cardsTable")).getSingleSelected();
        }
        if (doc == null){
            showWarning("createSalesOrd.noSalesOrd");
            return;
        }

        OrdDoc template = getTemplate();
        if (template == null){
            showWarning("createSalesOrd.noTemplateOrd");
            return;
        }

        doc = documentCopySupport.reloadSrcDoc(doc);
        template = documentCopySupport.reloadSrcDoc(template);

        if (!PersistenceHelper.isNew(doc)){
            OrdDoc ordDoc = copyFrom(doc, template);
            openEditor(ordDoc);
        }
    }


    protected void showWarning(String messageKey){
        if (frame instanceof Window) {
            final Window window = (Window) frame;
            String txtMesage = messages.getMessage(this.getClass(), messageKey);
            window.showNotification(txtMesage, IFrame.NotificationType.WARNING);
        }
    }


}
