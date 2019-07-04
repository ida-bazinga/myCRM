/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.ui.common.SaveEntityAction;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by k.khoroshilov on 30.01.2017.
 * Modification Ivanov.D.A on 20.03.2019
 */
public class CreateSalesActAction extends SaveEntityAction {

    protected Set<Entity> toCommit = new HashSet<>();

    protected Metadata metadata;
    protected DataManager dataManager;
    protected UserSessionSource userSessionSource;
    protected OrgStructureService orgStructureService;
    protected DocumentCopySupport documentCopySupport;
    protected MyUtilsService myUtilsService;
    protected AcDoc acDoc;


    public CreateSalesActAction(IFrame frame) {
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
        OrdDoc doc;
        toCommit.clear();
        if (frame instanceof Window.Editor){
            doc = (OrdDoc) ((Window.Editor) frame).getItem();
        }else{
            doc = ((Table) frame.getComponentNN("cardsTable")).getSingleSelected();
        }
        if (doc == null){
            showWarning("createSalesAct.noSalesOrder");
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

            //AcDoc act = copyFrom(doc, template);
            //openEditor(act);

            List<Tax> taxs = getTaxs(doc);
            newCopyFrom(doc, template, taxs);
            if(taxs.size() > 1) {
                doCommit();
            }
            else {
                //openEditor(this.act);
                validateOpenEditor(doc);
            }
        }
    }

    protected void openEditor() {
        Map<String, Object> params = new HashMap<>();
        params.put("toCommit", toCommit);
        Window window =  frame.openEditor("crm$AcDoc.edit", this.acDoc,  WindowManager.OpenType.THIS_TAB, params);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                //// TODO: Возможно следует открывать реестр счетов, после закрытия карточки счета (Чижиков П.С)
            }
        });
    }

    protected void validateOpenEditor(OrdDoc doc) {
        int count = myUtilsService.getActCount(doc.getId());
        if (count > 0) {
            frame.showOptionDialog("Внимание!",
                    String.format("По заказу %s, уже есть Акт! %s Создать новый Акт?", doc.getNumber(), System.lineSeparator()),
                    null,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                public void actionPerform(Component component) {
                                    openEditor();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {
                                public void actionPerform(Component component) {

                                }
                            }
                    }
            );
        } else {
            openEditor();
        }
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

        if (!validSalesOrder(doc)) return false;
        //проверка на наличие строк у заказа
        if (!validDetails(doc)) return false;
        //проверка на null, проверка заполнения для резидентов, ИНН и КПП
        if (!validCompany(doc.getCompany())) return false;
        //проверка на договор
        if (!validContract(doc.getContract())) return false;
        if (!validDeinstallationDatePlan((ExtProject) doc.getProject())) return false;
        if (!validNomenclature(doc)) return false;

        return true;
    }

    //проверка ссылки на заказ
    protected Boolean validSalesOrder(OrdDoc doc) {
        Boolean isValidate = doc != null;
        if (!isValidate) showWarning("createSalesAct.noSalesOrder");
        return isValidate;
    }

    //проверка заказа на наличие строк
    protected Boolean validDetails(OrdDoc doc) {
        Boolean isValidate = doc.getOrderDetails() != null && !doc.getOrderDetails().isEmpty();
        if (!isValidate) showWarning("createSalesAct.noSalesOrderDetails");
        return isValidate;
    }

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
        if (!isValidate) showWarning(txtMesage);  //if (!isValidate) showWarning("createSalesAct.nonValidCompany");

        return isValidate;
    }

    //проверка на договор
    protected Boolean validContract(Contract contract) {
        Boolean isValidate = contract != null;
        if (!isValidate) showWarning("createSalesAct.nonValidContract");
        return isValidate;
    }

    //проверка даты демонтажа в проекте
    protected Boolean validDeinstallationDatePlan(ExtProject project) {
        Boolean isValidate = false;
        if (project != null) {
            isValidate = project.getDeinstallationDatePlan() != null;
        }
        if (!isValidate) showWarning("createSalesAct.emptyProjectDeinstallationDatePlan");
        return isValidate;
    }

    //проверка на услугу аванс
    protected Boolean validNomenclature(OrdDoc ordDoc) {
        Nomenclature nomenclatureAdvance = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getNomenclatureAdvance();
        for (OrderDetail orderDetail:ordDoc.getOrderDetails()) {
            Nomenclature nomenclature = orderDetail.getProduct().getNomenclature();
            if (nomenclatureAdvance.equals(nomenclature)) {
                showWarning("createSalesAct.nonValidNomenclatureAdvance");
                return false;
            }
        }
        return true;
    }

    protected void showWarning(String messageKey) {
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

    protected List<Tax> getTaxs(OrdDoc doc) {
        List<Tax> taxs = new ArrayList<>();
        for (OrderDetail orderDetail:doc.getOrderDetails()) {
            if (!taxs.contains(orderDetail.getTax())) {
                taxs.add(orderDetail.getTax());
            }
        }
        return taxs;
    }

    protected void newCopyFrom(OrdDoc parent, AcDoc template, List<Tax> taxs) {

        int count = this.myUtilsService.getActCount(parent.getId());

        for (Tax tax:taxs) {
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
            act.setTax(tax);
            act.setOrganization(parent.getOrganization());
            act.setPrintInEnglish(parent.getPrintInEnglish());
            act.setOwner(orgStructureService.getEmployeeByUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser()));
            ExtProject project = (ExtProject) parent.getProject();
            if (parent.getProject() != null) {
                act.setChifAccount(loadEmployee(1, project)); //Глав. бух. для счет-фактур
                act.setGeneralDirector(loadEmployee(2, project)); //Ген. дир.
                act.setGeneralDirectorAct(loadEmployee(3, project)); //Ген. дир. для актов
                act.setAdditionalDetail(String.format("Участие в мероприятии %s", project.getName())); //Детализация одной строкой
                act.setDate(project.getDeinstallationDatePlan());
            }

            act.setNumber(String.format("%1$s/%2$d", parent.getNumber(), ++count));
            act = getOrderDetailList (parent, act, tax);
            act.setIntegrationResolver(taxs.size() > 1 ? createIntegrationResolver(act.getId()) : null);
            toCommit.add(act);
            if (taxs.size() == 1) {
                this.acDoc = act;
            }
        }
    }

    protected AcDoc getOrderDetailList(OrdDoc doc, AcDoc act, Tax tax) {
        BigDecimal taxSum = BigDecimal.valueOf(0);
        BigDecimal sum = BigDecimal.valueOf(0);
        ArrayList<ActDetail> details = new ArrayList<>();
        for (OrderDetail item:doc.getOrderDetails()) {
            if(item.getTax() == tax) {
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
                sum = item.getTotalSum() != null ? sum.add(item.getTotalSum()) : sum;
                taxSum = item.getTaxSum()!=null ? taxSum.add(item.getTaxSum()) : taxSum;
            }
        }
        act.setTaxSum(taxSum);
        act.setFullSum(sum);
        act.setAcDetails(details);
        return act;
    }

    private void doCommit() {
        Set<Entity> isCommit = dataManager.commit(new CommitContext(toCommit));
        if(!isCommit.isEmpty()) {
            showWarning("Акты успешно созданы.");
            return;
        }
    }

    protected IntegrationResolver createIntegrationResolver(UUID entityId) {
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
            integrationResolver.setEntityId(entityId);
            integrationResolver.setEntityName("crm$AcDoc");
            integrationResolver.setStateDocSales(StatusDocSales.NEW);
            integrationResolver.setPosted(false);
            integrationResolver.setDel(false);
        }
        toCommit.add(integrationResolver);
        return integrationResolver;
    }
}
