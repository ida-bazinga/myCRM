/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by k.khoroshilov on 31.01.2017.
 * Modifade d.ivanov
 */
public class CreatePackageSalesDocAction extends BaseAction {

    protected IFrame frame;
    protected Table table;
//    protected ExtProject project;
    protected Set<Entity> toCommit = new HashSet<>();
    protected Set<Entity> to1c = new HashSet<>();
    protected List<AcDoc> acDocList = new ArrayList<>();

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;
    protected int countAct;
    protected int countSelectedOrd;

    protected Log logFactory = LogFactory.getLog(this.getClass());

    public CreatePackageSalesDocAction(IFrame frame, Table table) {
        super("createActPack");
        this.frame = frame;
        this.table = table;
        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.orgStructureService = AppBeans.get(OrgStructureService.class);
        this.documentCopySupport = AppBeans.get(DocumentCopySupport.class);
    }

    /*public CreatePackageSalesDocAction(IFrame frame, ExtProject project) {
        super("createActPack");
        this.frame = frame;
        this.project = project;
        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.orgStructureService = AppBeans.get(OrgStructureService.class);
        this.documentCopySupport = AppBeans.get(DocumentCopySupport.class);
    }*/

    @Override
    public String getCaption() {
        return messages.getMessage(this.getClass(), "createPackageSalesDoc.caption");
    }

    @Override
    public void actionPerform(Component component) {
        Set<OrdDoc> docs = this.table.getSelected();
        countAct = 0;
        countSelectedOrd = this.table.getSelected().size();
        if (docs != null && !docs.isEmpty()){

            AcDoc template = getTemplate();
            if (template == null){
                showWarning("createSalesAct.noTemplateAct");
                return;
            }
            template = documentCopySupport.reloadSrcDoc(template);
            createPackage(docs, template);
            doCommit();
        } else
        showWarning("noSelectedRows");
    }

    protected void createPackage(Set<OrdDoc> docs, AcDoc template){
        for(OrdDoc doc : docs){
            doc = documentCopySupport.reloadSrcDoc(doc);
            if (validate(doc)){
                AcDoc act = copyFrom(doc, template);
                toCommit.add(act);
                // TODO: 31.01.2017 Добавить код передачи в 1С для получения фактуры
                acDocList.add(act);
                countAct++;
            }else{
                logFactory.info(String.format("Акт не создан! Заказ %s не прошел проверку валидации!", doc.getNumber()));
                //для лога
            }
        }
    }

    protected void createItemsLog1c()
    {
        try {
            for (AcDoc act:acDocList) {
                to1c(act.getCompany().getId(), act.getCompany().getMetaClass().getName(), 10);
                to1c(act.getProject().getId(), act.getProject().getMetaClass().getName(), 20);
                to1c(act.getParentCard().getId(), act.getParentCard().getMetaClass().getName(), 30);
                to1c(act.getId(), act.getMetaClass().getName(), 40);
                to1c(act.getId(), "crm$VatDoc", 50);
            }
        } catch (Exception e) {
            logFactory.info(String.format("CreatePackageSalesDocAction_createItemsLog1c: %s", e.toString()));
        }
    }

    protected void to1c(UUID Id, String entityName, long mills)
    {
        Log1C logNew =  metadata.create(Log1C.class);
        logNew.setStartDate(new Date(java.util.Calendar.getInstance().getTimeInMillis() + mills)); //java.util.Calendar.getInstance().getTime());
        logNew.setEntityId(Id);
        logNew.setEntityName(entityName);
        logNew.setShortServiceOperationResults(ServiceOperationResultsEnum.notwork);
        logNew.setPriority(2);
        to1c.add(logNew);
    }

    protected void doCommit(){
        //add try catch
        CommitContext context = new CommitContext(toCommit);
        Set<Entity> entitySet = this.dataManager.commit(context);
        if (!entitySet.isEmpty()) {
            //сформировать список для 1С
            createItemsLog1c();
            CommitContext context1с = new CommitContext(to1c);
            this.dataManager.commit(context1с);
        }
        toCommit.clear();
        to1c.clear();
        showWarning(String.format("Выбрано Заказов: %s \n Создано Актов: %s \n Акты не прошли валидацию: %s", this.countSelectedOrd, this.countAct, (this.countSelectedOrd-this.countAct)));
    }

    protected AcDoc copyFrom(OrdDoc parent, AcDoc template){

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
        act.setOwner(orgStructureService.getEmployeeByUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser()));
        act.setIntegrationResolver(createIntegrationResolver(act.getId()));
        ExtProject project = (ExtProject) parent.getProject();
        if (parent.getProject() != null) {
            act.setChifAccount(loadEmployee(1, project)); //Глав. бух.
            act.setGeneralDirector(loadEmployee(2, project)); //Ген. дир.

            act.setAdditionalDetail(String.format("Участие в мероприятии %s", project.getName())); //Детализация одной строкой
            act.setDate(project.getDeinstallationDatePlan());
        }

        int i = getVersionNumber(parent);
        i = (i > 0) ? i + 1 : 1;
        if (parent.getNumber() != null) {
            String str = parent.getNumber();
            //str = str.replace("З", "Р");
            act.setNumber(String.format("%1$s/%2$d", str, i));
        }

        //// TODO: табличная часть

        ArrayList<ActDetail> details = new ArrayList<>();
        for (OrderDetail item : parent.getOrderDetails()) {
            ActDetail actDetail = metadata.create(ActDetail.class);
            actDetail.setAcDoc(act);
            actDetail.setProduct(item.getProduct());
            actDetail.setAmount(item.getAmount());
            actDetail.setSecondaryAmount(item.getSecondaryAmount());
            actDetail.setCost(item.getCost());
            actDetail.setTax(item.getTax());
            actDetail.setTaxSum(item.getTaxSum());
            actDetail.setAlternativeName_ru(item.getAlternativeName_ru());
            actDetail.setAlternativeName_en(item.getAlternativeName_en());
            actDetail.setSumWithoutNds(item.getSumWithoutNds());
            actDetail.setTotalSum(item.getTotalSum());
            actDetail.setOrdDetail(item);
            details.add(actDetail);

            toCommit.add(actDetail);
        }
        act.setAcDetails(details);

        return act;
    }

    // TODO: 30.01.2017 Get string "DefaultSalesInvoice" from CrmConfig
    protected AcDoc getTemplate() {
        String templateName = "DefaultSalesAct";
        //Preconditions.checkNotNullArgument(templateName, "Template name is not specified");

        LoadContext loadContext = new LoadContext(AcDoc.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$AcDoc e where e.template = true and e.templateName = :name")
                .setParameter("name", templateName);

        return dataManager.load(loadContext);
    }

    protected boolean validate(OrdDoc doc) {
        if(!isNullAct(doc.getId())) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Акт создан ранее по заказу!", doc.getNumber()));
            return false;
        }
        if(!isNullInvAct(doc.getId())) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Акт создан ранее по счету!", doc.getNumber()));
            return false;
        }
        Boolean del = doc.getIntegrationResolver().getDel() != null ? doc.getIntegrationResolver().getDel() : false;
        if(del) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Помече на удаление!", doc.getNumber()));
            return false;
        }
        //проверка на наличие строк у заказа
        if (!validDetails(doc)) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Отсутсвуют услуги!", doc.getNumber()));
            return false;
        }
        //проверка на null, проверка заполнения для резидентов, ИНН и КПП
        if (!validCompany(doc.getCompany())) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Не заполнены в контрагенте ИНН, КПП!", doc.getNumber()));
            return false;
        }
        //проверка на Юр Адрес
        if (!validLegalAddress(doc.getCompany())) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Не заполнен в контрагенте Юр.адрес!", doc.getNumber()));
            return false;
        }
        //проверка на договор
        if (!validContract(doc.getContract())) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Отсутсвует договор!", doc.getNumber()));
            return false;
        }
        if (!validDeinstallationDatePlan((ExtProject) doc.getProject())) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Отсутсвует дата демонтажа в проекте!", doc.getNumber()));
            return false;
        }
        //проверка на услугу аванс
        if (!validNomenclature(doc)) {
            logFactory.warn(String.format("Массовое выставление Актов. Заказ %s. Присутствует услуга аванс!", doc.getNumber()));
            return false;
        }
        return true;
    }

    //проверка заказа на наличие строк
    protected Boolean validDetails(OrdDoc doc) {
        Boolean isValidate = doc.getOrderDetails() != null && !doc.getOrderDetails().isEmpty();

        //if (!isValidate) showWarning("createSalesAct.noSalesOrderDetails");

        return isValidate;
    }

    //проверка реквизитов (для резидентов) на null, проверка заполнения ИНН и КПП
    protected Boolean validCompany(ExtCompany company) {

        if (company != null && company.getCompanyType() != null) {
            if (company.getCompanyType().equals(CompanyTypeEnum.nonResident) || company.getCompanyType().equals(CompanyTypeEnum.person)) {
                return true;
            } else {
                return StringUtils.isNotBlank(company.getInn()) && StringUtils.isNotBlank(company.getKpp());
            }
        }
        //if (!isValidate) showWarning("createSalesAct.nonValidCompany");
        return false;
    }

    //проверка на договор
    protected Boolean validContract(Contract contract) {
        Boolean isValidate = contract != null;

        //if (!isValidate) showWarning("createSalesAct.nonValidContract");

        return isValidate;
    }

    //проверка Юр Адреса
    protected Boolean validLegalAddress(ExtCompany company) {
        Boolean isValidate = false;

        if (company != null && company.getCompanyType() != null)
        {
            isValidate = company.getCompanyType().equals(CompanyTypeEnum.nonResident) || company.getExtLegalAddress() != null;
        }

        //if (!isValidate) showWarning("createSalesAct.nonValidLegalAddress");

        return isValidate;
    }

    //проверка даты демонтажа в проекте
    protected Boolean validDeinstallationDatePlan(ExtProject project) {

        if (project != null){
            if (project.getDeinstallationDatePlan() == null) {
                return false;
            }
            if(project.getDateStartFact() == null) {
                return false;
            }
            if(project.getDateFinishFact() == null) {
                return false;
            }
        }
        return true;
    }

    protected Boolean validNomenclature(OrdDoc ordDoc) {
        Nomenclature nomenclatureAdvance = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getNomenclatureAdvance();
        for (OrderDetail orderDetail:ordDoc.getOrderDetails()) {
            Nomenclature nomenclature = orderDetail.getProduct().getNomenclature();
            if (nomenclatureAdvance.equals(nomenclature)) {
                return false;
            }
        }
        return true;
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
        else {
            loadContext.setQueryString("select e from crm$ProjectTeam p left join p.employee e where p.extProject.id = :project and (p.signatory = 2 or p.signatory = 3)")
                    .setParameter("project", project);
        }
        List<ExtEmployee> employees = dataManager.loadList(loadContext);

        return employees.isEmpty() ? null : employees.get(0);
    }

    protected int getVersionNumber(OrdDoc doc) {
        LoadContext loadContext = new LoadContext(AcDoc.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$AcDoc e where e.parentCard.id = :docId")
                .setParameter("docId", doc.getId());

        List<AcDoc> acts = dataManager.loadList(loadContext);

        return acts.isEmpty() ? 0 : acts.size();
    }

    protected Boolean isNullAct(UUID Id) {
        LoadContext loadContext = new LoadContext(AcDoc.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$AcDoc e where e.parentCard.id = :Id").setParameter("Id", Id);
        List<AcDoc> acDoc = dataManager.loadList(loadContext);
        if (acDoc.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    protected Boolean isNullInvAct(UUID Id) {
        LoadContext loadContext = new LoadContext(InvDoc.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$InvDoc e where e.parentCard.id = :Id").setParameter("Id", Id);
        List<InvDoc> invDocsDoc = dataManager.loadList(loadContext);
        Boolean result = true;
        if (invDocsDoc.size() > 0) {
            for (InvDoc i:invDocsDoc) {
                if (result) {
                    result = isNullAct(i.getId());
                }
            }
            return result;
        } else {
            return true;
        }
    }

    protected IntegrationResolver createIntegrationResolver(UUID entityId)
    {
        IntegrationResolver integrationResolver = null;
        LoadContext loadContext = new LoadContext(IntegrationResolver.class).setView("1c");
        String params = "select e from crm$IntegrationResolver e where e.entityName = 'crm$AcDoc' and e.entityId = :entityId";
        loadContext.setQueryString(params).setParameter("entityId", entityId).setMaxResults(1);
        List<IntegrationResolver> integrationResolverList = dataManager.loadList(loadContext);
        if(integrationResolverList.size() > 0) {
            integrationResolver = integrationResolverList.get(0);
        }
        else {
            integrationResolver = metadata.create(IntegrationResolver.class);
            integrationResolver.setEntityId(entityId);//*
            integrationResolver.setEntityName("crm$AcDoc");//*
            integrationResolver.setStateDocSales(StatusDocSales.NEW);//*
            integrationResolver.setPosted(false); //*
            integrationResolver.setDel(false); //*
        }
        toCommit.add(integrationResolver);
        return integrationResolver;
    }

}
