/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

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
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.ui.common.SaveEntityAction;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by k.khoroshilov on 30.01.2017.
 */
public class CreateSalesActFromInvoiceAction extends SaveEntityAction {

    protected Set<Entity> toCommit = new HashSet<>();

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;
    protected MyUtilsService myUtilsService;

    public CreateSalesActFromInvoiceAction(IFrame frame) {
        super("createAct", frame);
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
        return messages.getMessage(this.getClass(), "createSalesAct.caption");
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

    @Override
    protected void afterSaveEntity() {
        InvDoc doc;
        if (frame instanceof Window.Editor){
            doc = (InvDoc) ((Window.Editor) frame).getItem();
        }else{
            doc = ((Table) frame.getComponentNN("cardsTable")).getSingleSelected();
        }
        if (doc == null){
            showWarning("createSalesAct.noSalesInvoice");
            return;
        }

        AcDoc template = getTemplate();
        if (template == null){
            showWarning("createSalesAct.noTemplateInvoice");
            return;
        }

        doc = documentCopySupport.reloadSrcDoc(doc);
        template = documentCopySupport.reloadSrcDoc(template);

        if (validate(doc)){
            AcDoc act = copyFrom(doc, template);
            openEditor(act);
        }
    }

    protected AcDoc copyFrom(InvDoc parent, AcDoc template){

        AcDoc act = metadata.create(AcDoc.class);
        act.copyFrom(template, toCommit);

        act.setCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        act.setSubstitutedCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        act.setTemplate(false);

        act.setParentCard(parent);
        act.setParentCardAccess(parent.getParentCardAccess());
        act.setCompany(parent.getCompany());
        act.setCurrency(parent.getCurrency());
        act.setProject(parent.getProject());
        act.setContract(parent.getContract());
        act.setDepartment(parent.getDepartment());
        act.setTax(parent.getTax());
        act.setTaxSum(parent.getTaxSum());
        act.setFullSum(parent.getFullSum());
        act.setOrganization(parent.getOrganization());
        // TODO: 29.01.2017 так наверно правильно но пока...
        //invoice.setPrintInEnglish(parent.getCompany().getPrintInEnglish());
        act.setPrintInEnglish(parent.getPrintInEnglish());
        act.setOwner(parent.getOwner());  //orgStructureService.getEmployeeByUser(userSessionSource.getUserSession().getUser()));

        ExtProject project = (ExtProject) parent.getProject();
        if (parent.getProject() != null) {
            act.setChifAccount(loadEmployee(1, project)); //Глав. бух.
            act.setGeneralDirector(loadEmployee(2, project)); //Ген. дир.
            act.setGeneralDirectorAct(loadEmployee(3, project)); //Ген. дир.
            act.setAdditionalDetail(String.format("Участие в мероприятии %s", project.getName())); //Детализация одной строкой
            act.setDate(project.getDeinstallationDatePlan());
        }
        OrdDoc ordDoc = getOrdDoc(parent.getParentCard().getId()) ;
        String ordNumber = ordDoc.getNumber();
        int versionNumber = this.myUtilsService.getActCount(ordDoc.getId());
        int i = versionNumber + 1;
        act.setNumber(String.format("%1$s/%2$d", ordNumber, i));

        //// TODO: Необходимо передавать счет организации в зависимости от валюты счета или искать его при ините счета (Чижиков П.С)

        ArrayList<ActDetail> details = new ArrayList<>();
        for (InvoiceDetail item : parent.getInvoiceDetails()) {
            ActDetail actDetail = metadata.create(ActDetail.class);
            actDetail.setAcDoc(act);
            actDetail.setProduct(item.getProduct());
            actDetail.setAmount(item.getAmount());
            actDetail.setCost(item.getCost());
            actDetail.setTax(item.getTax());
            actDetail.setTaxSum(item.getTaxSum());
            actDetail.setAlternativeName_ru(item.getAlternativeName_ru());
            actDetail.setAlternativeName_en(item.getAlternativeName_en());
            actDetail.setSumWithoutNds(item.getSumWithoutNds());
            actDetail.setTotalSum(item.getTotalSum());
            details.add(actDetail);
        }
        act.setAcDetails(details);

        return act;
    }

    protected void openEditor(AcDoc invoice){
        Map<String, Object> params = new HashMap<>();
        params.put("toCommit", toCommit);
        Window window =  frame.openEditor("crm$AcDoc.edit", invoice, WindowManager.OpenType.THIS_TAB, params);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                //// TODO: Возможно следует открывать реестр счетов, после закрытия карточки счета (Чижиков П.С)
            }
        });
    }

    // TODO: 30.01.2017 Get string "DefaultSalesInvoice" from CrmConfig
    protected AcDoc getTemplate() {
        String templateName = "DefaultSalesAct";
        //Preconditions.checkNotNullArgument(templateName, "Template name is not specified");

        LoadContext loadContext = new LoadContext(AcDoc.class).setView("edit");
        loadContext.setQueryString("select e from crm$AcDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);

        return dataManager.load(loadContext);
    }

    protected boolean validate(InvDoc doc) {

        if (!validSalesInvoice(doc)) return false;

        //проверка на наличие строк у заказа
        if (!validDetails(doc)) return false;

        //проверка на null, проверка заполнения для резидентов, ИНН и КПП
        if (!validCompany(doc.getCompany())) return false;

        //проверка на договор
        if (!validContract(doc.getContract())) return false;

        //проверка на Юр Адрес
        if (!validLegalAddress(doc.getCompany())) return false;

        if (!validDeinstallationDatePlan((ExtProject) doc.getProject())) return false;

        return true;
    }

    //проверка ссылки на заказ
    protected Boolean validSalesInvoice(InvDoc doc) {
        Boolean isValidate = doc != null;

        if (!isValidate) showWarning("createSalesAct.noSalesInvoice");

        return isValidate;
    }

    //проверка заказа на наличие строк
    protected Boolean validDetails(InvDoc doc) {
        Boolean isValidate = doc.getInvoiceDetails() != null && !doc.getInvoiceDetails().isEmpty();

        if (!isValidate) showWarning("createSalesAct.noSalesInvoiceDetails");

        return isValidate;
    }

    /*
    проверка реквизитов (для резидентов) на null, проверка заполнения ИНН и КПП
    protected Boolean validCompany(ExtCompany company) {
        Boolean isValidate = false;

        if (company != null && company.getCompanyType() != null) {
            isValidate = company.getCompanyType().equals(CompanyTypeEnum.nonResident) || StringUtils.isNotBlank(company.getInn()) && StringUtils.isNotBlank(company.getKpp());
        }

        if (!isValidate) showWarning("createSalesAct.nonValidCompany");

        return isValidate;
    }
    */

    //проверка реквизитов (для резидентов) на null, проверка заполнения ИНН и КПП
    //проверка контрагента на заполнеие, обязательных реквизитов
    protected Boolean validCompany(ExtCompany company) {
        Boolean isValidate = true;
        String txtMesage = "";
        if (company != null && company.getCompanyType() != null) {
            //isValidate = company.getCompanyType().equals(CompanyTypeEnum.nonResident) || StringUtils.isNotBlank(company.getInn()) && StringUtils.isNotBlank(company.getKpp());
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
        }
        if (!isValidate) showWarning(txtMesage);
        return isValidate;
    }

    //проверка на договор
    protected Boolean validContract(Contract contract) {
        Boolean isValidate = contract != null;

        if (!isValidate) showWarning("createSalesAct.nonValidContract");
        return isValidate;
    }

    //проверка Юр Адреса
    protected Boolean validLegalAddress(ExtCompany company) {
        Boolean isValidate = false;

        if (company != null && company.getCompanyType() != null)
        {
            isValidate = company.getCompanyType().equals(CompanyTypeEnum.nonResident) || company.getExtLegalAddress() != null;
        }

        if (!isValidate) showWarning("createSalesAct.nonValidLegalAddress");

        return isValidate;
    }

    //проверка даты демонтажа в проекте
    protected Boolean validDeinstallationDatePlan(ExtProject project) {
        Boolean isValidate = false;

        if (project != null){
            isValidate = project.getDeinstallationDatePlan() != null;
        }
        if (!isValidate) showWarning("createSalesAct.emptyProjectDeinstallationDatePlan");

        return isValidate;
    }

    protected void showWarning(String messageKey){
        if (frame instanceof Window) {
            final Window window = (Window) frame;

            String txtMesage = messages.getMessage(this.getClass(), messageKey);
            window.showNotification(txtMesage, IFrame.NotificationType.WARNING);
        }

    }

    protected ExtEmployee loadEmployee(int param, Project project) {
        LoadContext loadContext = new LoadContext(ExtEmployee.class)
                .setView("edit");

        if(param == 1){
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 1 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        else if(param == 3) {
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 2 or p.signatory = 3) and (p.roleInProject.code = 200)")
                    .setParameter("project", project);
        }
        else {
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 2 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        List<ExtEmployee> employees = dataManager.loadList(loadContext);

        return employees.isEmpty() ? null : employees.get(0);
    }

    public OrdDoc getOrdDoc(UUID Id) {
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.id =:Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }
}
