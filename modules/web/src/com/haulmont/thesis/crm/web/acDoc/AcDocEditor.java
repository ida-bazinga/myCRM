package com.haulmont.thesis.crm.web.acDoc;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.crm.core.app.ValidINN;
import com.haulmont.thesis.crm.core.app.bp.IStatusService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.web.ui.basicdoc.SalesAbstractDocEditor;
import com.haulmont.workflow.core.app.WfUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class AcDocEditor<T extends AcDoc> extends SalesAbstractDocEditor<T> {

    @Inject
    protected IStatusService iStatusService;
    @Inject
    protected Button exportButton;
    @Named("company")
    private SearchPickerField company;
    @Inject
    private DataManager dataManager;
    @Inject
    private Label vatnumber_label;
    @Named("detailBtnPnl")
    private ButtonsPanel detailBtnPnl;
    @Named("orderDetailTable.remove")
    private RemoveAction orderDetailTableRemove;
    @Named("owner")
    private LookupPickerField owner;
    @Named("detailsDs")
    protected CollectionDatasource<ActDetail, UUID> detailsDs;
    @Named("companiesDs")
    protected CollectionDatasource<ActDetail, UUID> companiesDs;
    @Inject
    protected Table orderDetailTable;
    @Inject
    private VBoxLayout docInfo;
    @Inject
    private ScrollBoxLayout actionsFrameScroll;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    private Timer refreshVatnumberTimer;
    @Inject
    protected ValidINN validINN;

    private int priority;
    private Date dateQuartal;




    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void initNewItem(T item) {    //Значения для новой записи
        super.initNewItem(item);
        item.setPrintInEnglish(false);
        item.setPrintSingleLine(false);
    }

    @Override
    protected String getHiddenTabsConfig() {
        return "correspondenceHistoryTab,docLogTab,office,cardLinksTab,securityTab,versionsTab";
    }

    @Override
    public void setItem(Entity item) {
        if (PersistenceHelper.isNew(item)) {
            printActionsAlreadyInited = true;
        }
        super.setItem(item);
        //printButton.addAction(new PrintReportAction("printExecutionList", this, "printDocExecutionListReportName"));
    }

    @Override
    protected Component createState() {
        if (WfUtils.isCardInState(getItem(), "New") || StringUtils.isEmpty(getItem().getState())) {
            Label label = componentsFactory.createComponent(Label.NAME);
            label.setValue(StringUtils.isEmpty(getItem().getState()) ? "" : getItem().getLocState());
            return label;
        } else {
            return super.createState();
        }
    }

    @Override
    protected void fillHiddenTabs() {
        hiddenTabs.put("office", getMessage("office"));
        hiddenTabs.put("attachmentsTab", getMessage("attachmentsTab"));
        hiddenTabs.put("docTreeTab", getMessage("docTreeTab"));
        hiddenTabs.put("cardCommentTab", getMessage("cardCommentTab"));
        super.fillHiddenTabs();
    }

    @Override
    protected void initDocHeader(Doc doc){
        super.initDocHeader(doc);
        if (doc.getTemplate()) {
            setHeaderComponentVisible("procStateBox", false);

            setHeaderComponentVisible("companyLabel", false);
            setHeaderComponentVisible("company", false);
            setHeaderComponentVisible("contractLabel", false);
            setHeaderComponentVisible("contract", false);
            setHeaderComponentVisible("parentCardLabel", false);
            setHeaderComponentVisible("parentCard", false);
            setHeaderComponentVisible("printInEnglishLabel", false);
            setHeaderComponentVisible("printInEnglish", false);
            setHeaderComponentVisible("printSingleLineLabel", false);
            setHeaderComponentVisible("printSingleLine", false);
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            if (getItem().getIntegrationResolver().getExtId() != null) {
                goTo1C();
            }
        }
        return super.postCommit(committed, close);
    }

    @Override
    protected void postInit() { //Обработчик: после загрузки карточки
        exportButton.setEnabled(!PersistenceHelper.isNew(getItem()));
        company.setRequired(!getItem().getTemplate());
        owner.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                AcDoc item = getItem();
                if (item.getOwner() != null) {
                    item.setDepartment(item.getOwner().getDepartment());
                }
            }
        });

        detailsDs.addListener(new CollectionDsListenerAdapter<ActDetail>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<ActDetail> items) {
                if(items!=null){
                    if(operation.equals(Operation.REFRESH) && items.size()==0){
                        return;
                    }
                    BigDecimal sum = BigDecimal.valueOf(0);
                    BigDecimal taxSum = BigDecimal.valueOf(0);
                    for (Object i : ds.getItems()) {
                        ActDetail item = (ActDetail) i;
                        if(item.getTotalSum()!=null){
                            sum = sum.add(item.getTotalSum());
                        }
                        if(item.getTaxSum()!=null){
                            taxSum = taxSum.add(item.getTaxSum());
                        }
                    }
                    getItem().setFullSum(sum);
                    getItem().setTaxSum(taxSum);
                }
            }

        });

        setEditorCaption(getItem());
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1;
        dateQuartal = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getDataQuartal();

        //initCompanyColumnDetailTable();
    }


    protected void initCompanyColumnDetailTable() {
        final String column = "company";
        orderDetailTable.addGeneratedColumn(column, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                SearchPickerField component = componentsFactory.createComponent(SearchPickerField.NAME);
                component.setDatasource(orderDetailTable.getItemDatasource(entity), column);
                component.setOptionsDatasource(companiesDs);
                component.setWidth("100%");
                component.addClearAction();
                component.addLookupAction();
                component.addOpenAction();
                return component;
            }
        });
        orderDetailTable.getColumn(column).setCaption(getMessage(column));
    }

    protected void setEditorCaption(T doc) {
        StringBuilder caption = new StringBuilder();

        if (PersistenceHelper.isNew(doc)) {
            if (BooleanUtils.isTrue(doc.getTemplate())) {
                caption.append(getMessage("Caption.templateEdit"));
            } else {
                caption.append(getMessage("editorCaption"));
            }
        } else {
            caption.append(formatTools.formatForScreenHistory(doc));
        }

        getWindowManager().setWindowCaption(this, caption.toString(), null);
    }


    protected String validMassageExportButton() {
        if (getItem() != null) {
            //Запрос статусов из 1С
            AcDoc item = updateStatusAct1c();
            //проверка статусов 1С
            if (work1C.validate(item.getIntegrationResolver())) {
                boolean isPosted = work1C.validate(item.getIntegrationResolver().getPosted()) && item.getIntegrationResolver().getPosted();
                if (isPosted) {
                    return "Проведенный Акт нельзя отправлять в 1С!";
                }
            }
            if (item.getDateTime() == null) {
                return "Не заполнена дата акта!";
            }
            if(!isValidData()) {
                return "Дата Акта не может быть меньше, Даты Заказа!";
            }
            boolean isPeriod = isOpenPeriod(item.getDate());
            if (!isPeriod) {
                return "Дата Акта находиться в закрытом периоде!";
            }
            if (item.getContract().getNumber().length() > 20) {
                return "Номер \"Договора\" больше 20 символов. Отправка в 1С запрещена.";
            }

            if (!work1C.validate(item.getProject(), item.getCompany(), item.getContract())) {
                return "Не выбраны: Проект, Компания, Договор!";
            }

            LoadContext loadContext = new LoadContext(ActDetail.class);
            String sql = String.format("select e from %s e where e.acDoc.id = :Id", "crm$ActDetail");
            loadContext.setQueryString(sql).setParameter("Id", item.getId()).setMaxResults(1);
            if (!(dataManager.loadList(loadContext).size() > 0)) {
                return "Не выбраны Услуги!";
            }

            Map<String, Object> params = new HashMap<>();
            params.put("view", "1c");
            params.put("id", item.getProject().getId());
            ExtProject row_p = work1C.buildQuery(ExtProject.class, params);
            if(!work1C.validate(row_p.getDateStartFact(), row_p.getDateFinishFact())) {
                return "Не заполнены даты в Проекте!";
            }

            return "";
        }
        return "Объект = NULL";
    }

    //пометить к обмену
    public void exportButton() {
        if (cardDs.isModified() || detailsDs.isModified()) {
            showOptionDialog(dialogHeader, getMessage("massageInfo1c"), IFrame.MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES, true) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (editor.commit()) {
                                        editor.reopen();
                                        export();
                                    }
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    }
            );
        } else {
            export();
        }
    }

    public void export() {
        String message = validMassageExportButton();
        if (!message.isEmpty()) {
            showNotification(getMessage(message), NotificationType.HUMANIZED);
            return;
        }
        Boolean result = goTo1C();
        if (result) {
            super.integrationResolverUpdateStatus( getItem().getId(), StatusDocSales.MARKER1C);
            String txtMesage = "Акт отправлен в 1С";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            refreshVatnumberTimer.start();
            return;
        }
    }

    protected boolean goTo1C() {
        AcDoc item = getItem();
        UUID ordId = null;
        UUID invId = null;
        //Обход ошибки обновления карточки -->
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", item.getId());
        AcDoc rowAcDoc = work1C.buildQuery(AcDoc.class, params);
        if ("crm$OrdDoc".equals(rowAcDoc.getParentCard().getCategory().getEntityType())) {
            ordId = rowAcDoc.getParentCard().getId();
        } else {
            invId = rowAcDoc.getParentCard().getId();
            ordId = rowAcDoc.getParentCard().getParentCard().getId();
        }
        String s_ordId = ordId != null ? ordId.toString() : null;
        String s_invId = invId != null ? invId.toString() : null;

        params.clear();
        params.put(item.getProject().getMetaClass().getName(), item.getProject().getId());
        params.put(item.getCompany().getMetaClass().getName(), item.getCompany().getId());
        params.put("crm$OrdDoc", s_ordId);
        params.put("crm$InvDoc", s_invId);
        params.put(item.getMetaClass().getName(), item.getId());
        params.put("crm$VatDoc", item.getId());

        exportParentCompany();
        return work1C.regLog(params, priority);
    }

    private boolean isOpenPeriod(Date date) {
        try {
            if(date.compareTo(dateQuartal)==1) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    /*
    private String validCompany(ExtCompany company)
    {
        if (!work1C.validate(company.getCompanyType())) {
            return "Не заполнено поле \"Вид\" у контрагента!";
        }
        if (company.getCompanyType().equals(CompanyTypeEnum.legal) ||
                company.getCompanyType().equals(CompanyTypeEnum.government) ||
                company.getCompanyType().equals(CompanyTypeEnum.branch))
        {
            if(!work1C.validate(company.getInn(), company.getKpp())) {
                return "ИНН и КПП должны быть заполнены!";
            }
        }
        if (company.getCompanyType().equals(CompanyTypeEnum.legal) ||
                company.getCompanyType().equals(CompanyTypeEnum.nonResident) ||
                company.getCompanyType().equals(CompanyTypeEnum.branch) ||
                company.getCompanyType().equals(CompanyTypeEnum.person) ||
                company.getCompanyType().equals(CompanyTypeEnum.government))
        {
            boolean isAddress = work1C.validate(company.getExtLegalAddress());
            if(!isAddress) {
                return "Юридический адрес - должен быть заполнен!";
            }
            if(isAddress) {
                if(!work1C.validate(company.getExtLegalAddress().getAddressFirstDoc())) {
                    return "Адрес для первичных документов - должен быть заполнен!";
                }
            }
        }
        if (company.getCompanyType().equals(CompanyTypeEnum.branch))
        {
            if(!work1C.validate(company.getParentCompany())) {
               return "Головной контрагент должен быть заполнен!";
            }
            else {
                //проверяем, есть ли головной контрагент в мостике
                IntegrationResolver row = work1C.dataManagerLoad(IntegrationResolver.class, "crm$IntegrationResolver", "1c", "entityId", company.getParentCompany().getId());
                if(!work1C.validate(row)) {
                    String[] args = {null, company.getParentCompany().getId().toString()};
                    work1C.regLog(args, priority);
                }
            }
        }
        return "";
    }
    */

    private boolean isValidData() {
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("edit");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.id = :Id")
                .setParameter("Id", getItem().getParentCard().getId());
        List<OrdDoc> ordList = dataManager.loadList(loadContext);
        if(ordList.size() > 0) {
            for (OrdDoc ordDoc:ordList) {
                if(ordDoc.getDate().compareTo(getItem().getDate()) != 1) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void ready(){
        super.ready();
        initEditWindow();
        vatnumber_label.setValue(!work1C.validate(getItem().getVatnumber()) ? "Нет" : getItem().getVatnumber());
    }

    @Override
    public void closeAndRun(String actionId, Runnable runnable) {
        closeMyWindow();
        ((Window) frame).closeAndRun(actionId, runnable);
    }

    private void closeMyWindow() {
        if(getItem().getIntegrationResolver() != null) {
            if (!isTemplate && getItem().getIntegrationResolver().getDel()) {
                cardDs.setAllowCommit(false);
                detailsDs.setAllowCommit(false);
            }
        }
    }

    private void initEditWindow() {
        if (!isTemplate && getItem().getIntegrationResolver() != null) {
            boolean posted = getItem().getIntegrationResolver().getPosted();
            detailBtnPnl.setVisible(!posted);
            orderDetailTableRemove.setEnabled(!posted);
            getComponent("docNumber").setEnabled(!posted);
            getComponent("date").setEnabled(!posted);
            getComponent("additionalDetail").setEnabled(!posted);

            if (getItem().getIntegrationResolver().getDel()) {
                orderDetailTable.removeAllActions();
                detailBtnPnl.setVisible(false);
                docInfo.setEnabled(false);
                actionsFrameScroll.setVisible(false);
            }
        }
    }

    private AcDoc updateStatusAct1c() {
        AcDoc acDoc = getItem();
        if (!PersistenceHelper.isNew(acDoc)) {
            if (work1C.validate(acDoc.getIntegrationResolver().getExtId())) {
                boolean isPosted = work1C.validate(acDoc.getIntegrationResolver().getPosted()) && acDoc.getIntegrationResolver().getPosted();
                if (!isPosted) {
                    if (iStatusService.getStatusOne(acDoc.getIntegrationResolver())) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("view", "1c");
                        params.put("id", acDoc.getIntegrationResolver().getId());
                        IntegrationResolver integrationResolver = work1C.buildQuery(IntegrationResolver.class, params);
                        acDoc.setIntegrationResolver(integrationResolver);
                    }
                }
            }
        }
        return acDoc;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (getItem().getAcDetails() != null) {
            for (ActDetail detail : getItem().getAcDetails()) {
                if (detail.getIsAgency() != null) {
                    if (detail.getIsAgency()) {
                        if (detail.getCompany() == null) {
                            errors.add(String.format("Услуга: %s - агентская услуга. Заполнеие поля \"Контрагента\" обязательно! ", detail.getAlternativeName_ru()));
                        }
                        if (detail.getContract() == null) {
                            errors.add(String.format("Услуга: %s - агентская услуга. Заполнеие поля \"Договор\" обязательно! ", detail.getAlternativeName_ru()));
                        }
                    }
                }
            }
        }
    }

    public void refreshVatnumperTimer(Timer timer) {
        if (!PersistenceHelper.isNew(getItem()) && !work1C.validate(getItem().getVatnumber())) {
            Map<String, Object> params = new HashMap<>();
            params.put("view", "edit");
            params.put("id", getItem().getId());
            AcDoc acDoc = work1C.buildQuery(AcDoc.class, params);
            if (work1C.validate(acDoc.getVatnumber())) {
                vatnumber_label.setValue(acDoc.getVatnumber());
                refreshVatnumberTimer.stop();
            }
        }
    }

    private void exportParentCompany() {
        if (getItem().getCompany().getCompanyType().equals(CompanyTypeEnum.branch)) {
            Map<String, Object> params = new HashMap<>();
            params.put(getItem().getCompany().getMetaClass().getName(), getItem().getCompany().getParentCompany().getId());
            work1C.regLog(params, priority);
        }
    }

}