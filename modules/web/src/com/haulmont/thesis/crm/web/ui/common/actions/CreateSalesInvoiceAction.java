/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.ui.common.SaveEntityAction;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by k.khoroshilov on 22.01.2017.
 */
public class CreateSalesInvoiceAction extends SaveEntityAction {

    protected Set<Entity> toCommit = new HashSet<>();
    protected OrdDoc salesOrder;
    protected List<OrderDetail> salesOrderDetails;

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;

    public CreateSalesInvoiceAction(IFrame frame) {
        super("createInvoice", frame);
        this.frame = frame;
        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.orgStructureService = AppBeans.get(OrgStructureService.class);
        this.documentCopySupport = AppBeans.get(DocumentCopySupport.class);
    }

    @Override
    public String getCaption() {
        return messages.getMessage(this.getClass(), "createSalesInvoice.caption");
    }

    @Override
    public void actionPerform(Component component){
        if (frame instanceof Window.Editor)
            super.actionPerform(component);
        else {
            afterSaveEntity();
        }
    }

    @Override
    protected void afterSaveEntity() {
        OrdDoc doc;
        if (frame instanceof Window.Editor){
            doc = (OrdDoc) ((Window.Editor) frame).getItem();
        }else{
            doc = ((Table) frame.getComponentNN("cardsTable")).getSingleSelected();
        }
        if (doc == null){
            showWarning("createSalesInvoice.noSalesOrder");
            return;
        }

        this.salesOrder = documentCopySupport.reloadSrcDoc(doc);
        this.salesOrderDetails = this.salesOrder.getOrderDetails();

        InvDoc template = getTemplate();
        if (template == null){
            showWarning("createSalesInvoice.noTemplateInvoice");
            return;
        }
        template = documentCopySupport.reloadSrcDoc(template);

        if (validate()) copyFrom(template);
    }

    protected void copyFrom(@Nonnull InvDoc sourceDoc){
        Preconditions.checkNotNullArgument(sourceDoc, "Template document is not specified");

        InvDoc invoice = metadata.create(InvDoc.class);

        invoice.copyFrom(sourceDoc, toCommit);

        invoice.setCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        invoice.setSubstitutedCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        invoice.setTemplate(false);

        invoice.setParentCard(this.salesOrder);
        invoice.setParentCardAccess(this.salesOrder.getParentCardAccess());
        invoice.setCompany(this.salesOrder.getCompany());
        invoice.setCurrency(this.salesOrder.getCurrency());
        invoice.setProject(this.salesOrder.getProject());
        if (this.salesOrder.getProject() != null) {
            invoice.setChifAccount(loadEmployee(1, this.salesOrder.getProject())); //Глав. бух.
            invoice.setGeneralDirector(loadEmployee(2, this.salesOrder.getProject())); //Ген. дир.

            invoice.setAdditionalDetail(String.format("Участие в мероприятии %s", this.salesOrder.getProject().getName())); //Детализация одной строкой
        }
        // TODO: 29.01.2017 так наверно правильно но пока...
        //invoice.setPrintInEnglish(this.salesOrder.getCompany().getPrintInEnglish());
        invoice.setPrintInEnglish(this.salesOrder.getPrintInEnglish());
        invoice.setIntegrationResolver(null);

        int i = getVersionNumber();
        i = (i > 0) ? i + 1 : 1;
        if (this.salesOrder.getNumber() != null) {
            String str = this.salesOrder.getNumber();
            //str = str.replace("З", "С");
            invoice.setNumber(String.format("%1$s/%2$d", str, i));
        }
        invoice.setContract(this.salesOrder.getContract());

        invoice.setDepartment(this.salesOrder.getDepartment());
        invoice.setTax(this.salesOrder.getTax());
        invoice.setTaxSum(this.salesOrder.getTaxSum());
        invoice.setFullSum(this.salesOrder.getFullSum());
        invoice.setOrganization(this.salesOrder.getOrganization());
        invoice.setOwner(orgStructureService.getEmployeeByUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser()));

        //// TODO: Необходимо передавать счет организации в зависимости от валюты счета или искать его при ините счета (Чижиков П.С)

        ArrayList<InvoiceDetail> inv = new ArrayList<>();
        for (OrderDetail item : salesOrderDetails) {
            InvoiceDetail invoiceDetail = metadata.create(InvoiceDetail.class);
            invoiceDetail.setInvDoc(invoice);
            invoiceDetail.setProduct(item.getProduct());
            invoiceDetail.setAmount(item.getAmount());
            invoiceDetail.setCost(item.getCost());
            invoiceDetail.setTax(item.getTax());
            invoiceDetail.setTaxSum(item.getTaxSum());
            invoiceDetail.setAlternativeName_ru(item.getAlternativeName_ru());
            invoiceDetail.setAlternativeName_en(item.getAlternativeName_en());
            invoiceDetail.setSumWithoutNds(item.getSumWithoutNds());
            invoiceDetail.setTotalSum(item.getTotalSum());
            invoiceDetail.setOrdDetail(item);
            inv.add(invoiceDetail);
        }
        invoice.setInvoiceDetails(inv);
        openEditor(invoice);
    }

    protected void openEditor(InvDoc invoice){
        Map<String, Object> params = new HashMap<>();
        params.put("toCommit", toCommit);
        Window window =  frame.openEditor("crm$InvDoc.edit", invoice, WindowManager.OpenType.THIS_TAB, params);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                //// TODO: Возможно следует открывать реестр счетов, после закрытия карточки счета (Чижиков П.С)
            }
        });
    }

    protected InvDoc getTemplate() {
        // TODO: 30.01.2017 Get string "DefaultSalesInvoice" from CrmConfig
        String templateName = "DefaultSalesInvoice";
        Preconditions.checkNotNullArgument(templateName, "Template name is not specified");

        LoadContext loadContext = new LoadContext(InvDoc.class)
                .setView("_minimal");

        loadContext.setQueryString("select e from crm$InvDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);
        return dataManager.load(loadContext);
    }

    protected boolean validate() {

        if (!validSalesOrder()) return false;

        //проверка на наличие строк у заказа
        if (!validDetails()) return false;

        //проверка на null, проверка заполнения для резидентов, ИНН и КПП
        if (!validCompany(this.salesOrder.getCompany())) return false;

        //проверка на договор
        if (!validContract(this.salesOrder.getContract())) return false;

        //проверка на Юр Адрес
        //if (!validLegalAddress(this.salesOrder.getCompany())) return false;

        return true;
    }

    //проверка ссылки на заказ
    protected Boolean validSalesOrder() {
        Boolean isValidate = salesOrder != null;

        if (!isValidate) showWarning("createSalesInvoice.noSalesOrder");

        return isValidate;
    }

    //проверка заказа на наличие строк
    protected Boolean validDetails() {
        Boolean isValidate = this.salesOrder.getOrderDetails() != null && !this.salesOrder.getOrderDetails().isEmpty();

        if (!isValidate) showWarning("createSalesInvoice.noSalesOrderDetails");

        return isValidate;
    }

    //проверка реквизитов (для резидентов) на null, проверка заполнения ИНН и КПП
    //проверка контрагента на заполнеие, обязательных реквизитов
    protected Boolean validCompany(ExtCompany company) {
        Boolean isValidate = true;
        String txtMesage = "";
        if (company != null && company.getCompanyType() != null) {
            //isValidate = company.getCompanyType().equals(CompanyTypeEnum.nonResident) || StringUtils.isNotBlank(company.getInn()) && StringUtils.isNotBlank(company.getKpp());
            if (company.getCompanyType().equals(CompanyTypeEnum.legal) ||
                    company.getCompanyType().equals(CompanyTypeEnum.government) ||
                    company.getCompanyType().equals(CompanyTypeEnum.branch)) {
                if (!StringUtils.isNotBlank(company.getInn()) && StringUtils.isNotBlank(company.getKpp())) {
                    txtMesage = "ИНН, КПП - должны быть заполнены!";
                    isValidate = false;
                }
            }
            if (company.getCompanyType().equals(CompanyTypeEnum.legal) ||
                    company.getCompanyType().equals(CompanyTypeEnum.nonResident) ||
                    company.getCompanyType().equals(CompanyTypeEnum.branch) ||
                    company.getCompanyType().equals(CompanyTypeEnum.person) ||
                    company.getCompanyType().equals(CompanyTypeEnum.government)) {
                if (company.getExtLegalAddress() == null) {
                    txtMesage = "Юридический адрес - должн быть заполнен!";
                    isValidate = false;
                }
            }
            if (company.getCompanyType().equals(CompanyTypeEnum.branch)) {
                if (company.getParentCompany() == null) {
                    txtMesage = "Головной контрагент - должн быть заполнен!";
                    isValidate = false;
                }
            }
        }
        if (!isValidate) showWarning(txtMesage); //showWarning("createSalesInvoice.nonValidCompany");

        return isValidate;
    }

    //проверка на договор
    protected Boolean validContract(Contract contract) {
        Boolean isValidate = contract != null;

        if (!isValidate) showWarning("createSalesInvoice.nonValidContract");
        return isValidate;
    }

    //проверка Юр Адреса
    protected Boolean validLegalAddress(ExtCompany company) {
        Boolean isValidate = false;

        if (company != null && company.getCompanyType() != null)
        {
            isValidate = company.getCompanyType().equals(CompanyTypeEnum.nonResident) || company.getExtLegalAddress() != null;
        }

        if (!isValidate) showWarning("createSalesInvoice.nonValidLegalAddress");

        return isValidate;
    }

    protected void showWarning(String messageKey){
        if (frame instanceof Window) {
            final Window editor = (Window) frame;

            String txtMesage = messages.getMessage(this.getClass(), messageKey);
            editor.showNotification(txtMesage, IFrame.NotificationType.WARNING);
        }

    }

    protected ExtEmployee loadEmployee(int param, Project project) {
        LoadContext loadContext = new LoadContext(ExtEmployee.class)
                .setView("edit");

        if(param == 1){
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 1 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        else {
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 2 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        List<ExtEmployee> employees = dataManager.loadList(loadContext);

        return employees.isEmpty() ? null : employees.get(0);
    }

    protected int getVersionNumber() {
        LoadContext loadContext = new LoadContext(InvDoc.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$InvDoc e where e.parentCard.id = :salesOrder")
                .setParameter("salesOrder", this.salesOrder);

        List<InvDoc> invoices = dataManager.loadList(loadContext);

        return invoices.isEmpty() ? 0 : invoices.size();
    }
}
