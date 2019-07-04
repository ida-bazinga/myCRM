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
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.ui.common.SaveEntityAction;

import java.math.BigDecimal;

/**
 * Created by d.ivanov on 26.06.2018
 */
public class CreateSalesExpenseBillFromContractAction extends SaveEntityAction {

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;
    protected MyUtilsService myUtilsService;



    public CreateSalesExpenseBillFromContractAction(IFrame frame) {
        super("createExpenseBill", frame);
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
        return messages.getMessage(this.getClass(), "createExpenseBill.caption");
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

    protected void openEditor(InvDoc invDoc){
        Window window =  frame.openEditor("crm$InvDoc.edit",invDoc, WindowManager.OpenType.THIS_TAB);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                //// TODO:
            }
        });
    }

    // TODO:
    protected InvDoc getTemplate() {
        String templateName = "Основной";
        //Preconditions.checkNotNullArgument(templateName, "Template name is not specified");

        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$InvDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);

        return dataManager.load(loadContext);
    }

    protected Currency getCurrency(String code) {
        LoadContext loadContext = new LoadContext(Currency.class).setView("_local");
        loadContext.setQueryString("select e from crm$Currency e where e.name_en = :code")
                .setParameter("code", code);

        return dataManager.load(loadContext);
    }


    protected Tax getTax(BigDecimal rate) {
        rate = rate.divide(BigDecimal.valueOf(100)).setScale(2);
        LoadContext loadContext = new LoadContext(Tax.class).setView("_local");
        loadContext.setQueryString("select e from crm$Tax e where e.rate = :rate")
                .setParameter("rate", rate);

        return dataManager.load(loadContext);
    }


    protected InvDoc copyFrom(Contract doc, InvDoc template) {
        InvDoc invDoc = documentCopySupport.copy(template);
        invDoc.setCompany((ExtCompany)doc.getContractor());
        invDoc.setDocCategory(template.getDocCategory());
        invDoc.setParentCard(doc);
        //invDoc.setContract(doc);
        invDoc.setIntegrationResolver(null);
        if (doc.getAmount() != null) {
            invDoc.setFullSum(doc.getAmount().setScale(2));
        }
        if (doc.getVatAmount() != null) {
            invDoc.setTaxSum(doc.getVatAmount().setScale(2));
        }
        if (doc.getVatRate() != null) {
            invDoc.setTax(getTax(doc.getVatRate()));
        }
        if (doc.getCurrency() != null) {
            invDoc.setCurrency(getCurrency(doc.getCurrency().getCode()));
        }

        return invDoc;
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
            showWarning("createExpenseBill.noSalesExpenseBill");
            return;
        }

        InvDoc template = getTemplate();
        if (template == null){
            showWarning("createExpenseBill.noTemplateExpenseBill");
            return;
        }

        doc = documentCopySupport.reloadSrcDoc(doc);
        template = documentCopySupport.reloadSrcDoc(template);

        if (!PersistenceHelper.isNew(doc)){
            InvDoc invDoc = copyFrom(doc, template);
            openEditor(invDoc);
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
