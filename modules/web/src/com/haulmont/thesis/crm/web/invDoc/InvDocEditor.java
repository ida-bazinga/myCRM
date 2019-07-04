package com.haulmont.thesis.crm.web.invDoc;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.entity.ContractorAccount;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.DocCategory;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.TaxRate;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.web.ui.basicdoc.SalesAbstractDocEditor;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesActFromInvoiceAction;
import com.haulmont.thesis.web.actions.PrintReportAction;
import org.apache.commons.lang.BooleanUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class InvDocEditor<T extends InvDoc> extends SalesAbstractDocEditor<T> {

    @Inject
    protected CollectionDatasource<InvoiceDetail, UUID> detailsDs;
    @Inject
    protected CollectionDatasource<InvDocBugetDetail, UUID> invDocBugetDetailsDs;
    @Inject
    protected CollectionDatasource<ContractorAccount, UUID> companyAccountDs;
    @Inject
    private DataManager dataManager;
    @Inject
    protected Button exportButton;
    @Inject
    protected LookupField tax, currency;
    @Inject
    protected SearchPickerField budgetDocLookup, company, project;
    @Inject
    protected CheckBox isPlannedChkbx, isPaymentRub;
    @Inject
    protected GroupBoxLayout signatory, additionalDetailBox;
    @Inject
    protected TextField docNumber, fullSum, taxSum;
    @Inject
    protected Label integraStateLabel3, name_paid_label, taxMismatchWarning, invDoubleWarning, ownerLabel,
            comment_ru, parentCardLabel, docNumberLabel, companyLabel, fullSumLabel,projectLabel;
    @Inject
    protected LookupPickerField owner;
    @Inject
    protected LookupField transactionType;
    @Inject
    protected ButtonsPanel detailBtnPnl, budgetExpensesBtnPnl;
    @Inject
    protected PopupButton createSubDocButton, additionalCreateBtn;
    @Inject
    protected TabSheet detailsTabsheet;
    @Inject
    protected Table orderDetailTable, budgetExpensesTable;
    @Inject
    private BoxLayout docInfo, budgetDocBox, buyInvoice, buyTaxCurrency, addBudgetBox, hb_companyAccount, paymentBudgetOrganization;
    @Inject
    private ScrollBoxLayout actionsFrameScroll;
    @Inject
    protected TextArea paymentDestination;
    @Inject
    protected DateField date;
    @Inject
    protected PickerField contract, parentCard;
    @Named("orderDetailTable.remove")
    private RemoveAction orderDetailTableRemove;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected CrmConfig crmConfig;
    @Inject
    protected Metadata metadata;

    private int priority;
    private Date dateQuartal;
    private String validateInvDocMessage;
    private boolean isExpense;



    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        budgetExpensesTable.addAction(new CreateAction(budgetExpensesTable, WindowManager.OpenType.DIALOG){
            @Override
            public Map<String,Object> getInitialValues(){
                return Collections.<String,Object>singletonMap("invDoc", cardDs.getItem());
            }
        });
        budgetExpensesTable.addAction(new RemoveAction(budgetExpensesTable, false));
        isPlannedChkbx.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem() != null && getItem().getIsPlanned()!=null) {
                    budgetDocBox.setVisible(!getItem().getIsPlanned());
                    budgetDocLookup.setRequired(!getItem().getIsPlanned());
                }
            }
        });
        detailsDs.addListener(new CollectionDsListenerAdapter<InvoiceDetail>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<InvoiceDetail> items) {
                if(items!=null){
                    if(operation.equals(Operation.REFRESH) && items.size()==0){
                        return;
                    }
                    BigDecimal sum = BigDecimal.valueOf(0);
                    BigDecimal taxSum = BigDecimal.valueOf(0);
                    for (Object i : ds.getItems()) {
                        InvoiceDetail item = (InvoiceDetail) i;
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
        CreateSalesActFromInvoiceAction createActAction = new CreateSalesActFromInvoiceAction(this);
        additionalCreateBtn.addAction(createActAction);
    }


    @Override
    protected void initNewItem(T item) {    //Значения для новой записи
        super.initNewItem(item);
        item.setPrintInEnglish(item.getPrintInEnglish()==null ? false : item.getPrintInEnglish());
        item.setPrintSingleLine(item.getPrintSingleLine()==null ? false : item.getPrintSingleLine());
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
        printButton.addAction(new PrintReportAction("printExecutionList", this, "printDocExecutionListReportName"));
    }

    @Override
    protected void createCreateSubCardActions() {
        super.createCreateSubCardActions();
        createSubCardButton.setVisible(false);
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
            setHeaderComponentVisible("printInEnglish", false);
            setHeaderComponentVisible("printSingleLine", false);
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
    protected boolean preCommit() {
        if (isTemplate) {
            return true;
        }

        if (!super.preCommit()) {
            showNotification("Счет не сохранен!", NotificationType.HUMANIZED);
            return false;
        }

        if (isExpense) {
            return isValidExpense();
        } else {
            if (detailsDs.isModified() || cardDs.isModified()) {
                if (!isSendInvDoc1c()) {
                    showNotification(String.format("Счет не сохранен! %s", this.validateInvDocMessage), NotificationType.HUMANIZED);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void postInit() {
        company.setRequired(!isTemplate);
        owner.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                InvDoc item = getItem();
                if (item.getOwner() != null) {
                    item.setDepartment(item.getOwner().getDepartment());
                }
            }
        });

        setEditorCaption(getItem());
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1;
        dateQuartal = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getDataQuartal();
        exportButton.setEnabled(!PersistenceHelper.isNew(getItem()));
        isExpense = getItem().getDocKind().getCode().equalsIgnoreCase("ExpenseBill");
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

    public void taxCheck() {
        if (tax.getValue() != null && taxSum.getValue() != null
                && fullSum.getValue() != null && isExpense) {
            List<Tax> cat = loadTax(getItem().getTax().getName_ru());
            BigDecimal rate, calcTax, taxScaled;
            if (cat.get(0) != null && cat.get(0).getRate().compareTo(BigDecimal.ZERO) != 0) {
                rate = cat.get(0).getRate();
                calcTax = getItem().getFullSum().multiply(rate);
                calcTax = calcTax.divide(rate.add(BigDecimal.valueOf(1)), BigDecimal.ROUND_HALF_UP, 2);
                taxScaled = calcTax.setScale(2, BigDecimal.ROUND_HALF_UP);
                if (getItem().getTaxSum().compareTo(taxScaled) != 0)  {
                    taxMismatchWarning.setVisible(true);
                } else {
                    taxMismatchWarning.setVisible(false);
                }
            } else {
                taxMismatchWarning.setVisible(false);
            } // || (rate.compareTo(BigDecimal.ZERO) != 0 && getItem().getTaxSum().compareTo(getItem().getFullSum()) != 0)
        }
    }

    public void setName() { //Генератор названия заказа (Контрагент [Проект])
        String nameProject = "";
        String nameCompany = "";
        InvDoc item = getItem();
        if (item.getProject() != null) {
            nameProject = getItem().getProject().getName();
        }
        if (item.getCompany() != null) {
            nameCompany = getItem().getCompany().getName();
        }

        getItem().setTheme(String.format("%s [%s]",nameCompany, nameProject));
        getItem().setDescription(String.format("%s [%s]",nameCompany, nameProject));
    }

    private List<DocCategory> loadCategory(String code) {
        LoadContext loadContext = new LoadContext(DocCategory.class)
                .setView("_local");
        loadContext.setQueryString("select e from df$Category e where e.code like :code")
                .setParameter("code", code);
        return dataManager.loadList(loadContext);
    }

    private List<Tax> loadTax(String taxName) {
        LoadContext loadContext = new LoadContext(Tax.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$Tax e where e.name_ru like :taxName")// where e.code like :code")
                .setParameter("taxName", taxName);
        return dataManager.loadList(loadContext);
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
        if (isSendInvDoc1c()) {
            this.validateInvDocMessage = "Счет отправлен в 1С";
        }
        showNotification(getMessage(this.validateInvDocMessage), NotificationType.HUMANIZED);
        return;
    }

    private boolean isSendInvDoc1c() {
        InvDoc item = getItem();
        if (isValidateInvDoc()) {
            Map<String, Object> params = new HashMap<>();
            params.put(item.getProject().getMetaClass().getName(), item.getProject().getId());
            params.put(item.getCompany().getMetaClass().getName(), item.getCompany().getId());
            params.put("crm$OrdDoc", item.getParentCard().getId());
            params.put(item.getMetaClass().getName(), item.getId());
            exportParentCompany();
            Boolean result = work1C.regLog(params, priority);
            if (result) {
                super.integrationResolverUpdateStatus(item.getId(), StatusDocSales.MARKER1C);
                return true;
            }
        }

        return false;
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

    @Override
    public void ready(){
        super.ready();
        initEditWindow();
    }

    @Override
    public void closeAndRun(String actionId, Runnable runnable) {
        closeMyWindow();
        ((Window) frame).closeAndRun(actionId, runnable);
    }

    private void closeMyWindow() {
        if (getItem().getIntegrationResolver() != null) {
            if (getItem().getIntegrationResolver().getDel()) {
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
            if (getItem().getIntegrationResolver().getDel()) {
                orderDetailTable.removeAllActions();
                detailBtnPnl.setVisible(false);
                docInfo.setEnabled(false);
                actionsFrameScroll.setVisible(false);
            }
        }
        initLisener();
        typeDoc();
        settingFieldsTemplate();
    }

    private void buildPaymentDestination() {
        InvDoc item = getItem();
        StringBuffer paymentDest = new StringBuffer();
        paymentDest.append("Оплата по счету № ");

        if (item.getNumber() != null) {
            paymentDest.append(item.getNumber());
        }

        if (item.getDate() != null) {
            paymentDest.append(" от " + new SimpleDateFormat("dd.MM.yyyy").format(item.getDate()));
        }

        if (item.getFullSum() != null) {
            paymentDest.append(System.lineSeparator());
            paymentDest.append("Сумма " + item.getFullSum());
            paymentDest.append(System.lineSeparator());
        }

        if (item.getTaxSum() != null && item.getTax() != null) {
            if (item.getTax().getCode() == TaxRate.NO_VAT.name()) {
                paymentDest.append("Без налога (НДС)");
            } else {
                paymentDest.append(String.format("В т.ч. %s, %s", item.getTax().getName_ru(), item.getTaxSum()));
            }
        }
        paymentDestination.setValue(paymentDest.toString());
    }

    public void budgetExpensesTableCreate() {
        InvDoc item = getItem();
        if (item.getFullSum() == null) {
            showNotification(getMessage("errorFullSum"), NotificationType.HUMANIZED);
            return;
        }
        if (item.getCurrency() == null) {
            showNotification(getMessage("errorCurrency"), NotificationType.HUMANIZED);
            return;
        }

        InvDocBugetDetail invDocBugetDetail = metadata.create(InvDocBugetDetail.class);
        invDocBugetDetail.setInvDoc(item);
        invDocBugetDetail.setCurrency(item.getCurrency());
        invDocBugetDetail.setFullSum(getSumInvDocBuget());
        invDocBugetDetailsDs.addItem(invDocBugetDetail);
    }

    protected BigDecimal getSumInvDocBuget() {
        BigDecimal sum = BigDecimal.ZERO;

        for (InvDocBugetDetail i: invDocBugetDetailsDs.getItems()) {
            sum = sum.add(i.getFullSum());
        }

        sum = getItem().getFullSum().subtract(sum);

        return sum;
    }

    private boolean isValidateInvDoc() {
        InvDoc item = getItem();
        this.validateInvDocMessage = "";

        boolean isPeriod = isOpenPeriod(item.getDate());
        if (!isPeriod) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. Дата счета находиться в закрытом периоде!";
            return false;
        }

        if (item.getProject() == null) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. Не выбран проект!";
            return false;
        }

        ExtProject extProject = getProject();

        if (extProject.getDateStartFact() == null) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. В Проекте не заполна дата \"Начало проведения\"!";
            return false;
        }

        if (extProject.getDateFinishFact() == null) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. В Проекте не заполна дата \"Завершение проведения\"!";
            return false;
        }

        if (item.getContract() == null) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. Не выбран договор!";
            return false;
        }
        if (item.getContract().getNumber().length() > 20) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. Номер \"Договора\" больше 20 символов, отправка в 1С запрещена.";
            return false;
        }

        if (detailsDs.size() < 1) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. Не выбраны услуги!";
            return false;
        }

        boolean isCost = true;
        for (InvoiceDetail invDetail : detailsDs.getItems()) {
            if (invDetail.getCost().signum() != 1) {
                isCost = false;
            }
        }

        if(!isCost) {
            this.validateInvDocMessage = "Счет не отправлен в 1С. Счет содержит услуги с нулевой ценой!";
            return false;
        }

        return true;
    }

    protected void initExpenseBill() {
        parentCardLabel.setValue("Основание(договор, приказ)");
        ownerLabel.setValue("Ответственное лицо");
        comment_ru.setValue("Примечание");
        docNumberLabel.setValue("Номер счета");
        companyLabel.setValue("Получатель платежа");
        fullSumLabel.setValue("Сумма платежа");
        name_paid_label.setVisible(false);
        integraStateLabel3.setVisible(false);
        signatory.setVisible(false);
        additionalDetailBox.setVisible(false);
        buyInvoice.setVisible(true);
        buyTaxCurrency.setVisible(true);
        detailsTabsheet.getTab("budgetExpensesTab").setVisible(true);
        detailsTabsheet.getTab("ordDetailTab").setVisible(false);
        addBudgetBox.setVisible(true);
        exportButton.setVisible(false);
        additionalCreateBtn.setVisible(false);
        hb_companyAccount.setVisible(true);
        project.setVisible(false);
        projectLabel.setVisible(false);
        IFrame frame = getComponent("header");
        HBoxLayout hBoxLayout1 = frame.getComponent("captionInfoBox");
        hBoxLayout1.setVisible(false);
        if (PersistenceHelper.isNew(getItem())) {
            isPlannedChkbx.setValue(true);
            owner.setValue(orgStructureService.getEmployeeByUser(userSession.getCurrentOrSubstitutedUser()));
            List<DocCategory> cat = loadCategory("inv");
            if (cat.get(0) != null) {
                getItem().setDocCategory(cat.get(0));
            }
        }
    }

    protected boolean doubleCheck() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "edit");
        params.put("company.id", getItem().getCompany().getId());
        params.put("number", getItem().getNumber());
        params.put("docKind.id", getItem().getDocKind().getId());
        params.put("date", getItem().getDate());
        List<InvDoc> invDocList = work1C.buildQueryList(InvDoc.class, params);
        if (invDocList.size() > 0) {
            return true;
        }
        return false;
    }

    protected void settingFieldsTemplate() {
        if (getItem().getTemplate()) {
            String[] filds = {};
            required(filds, false);
        }
    }

    protected void required(String[] filds, boolean required) {
        for (Component comp : docInfo.getComponents()) {
            if (comp instanceof Field) {
                Field field = (Field) comp;
                if (filds.length > 0) {
                    for (String f:filds) {
                        if (f.equals(field.getId())) {
                            field.setRequired(required);
                        }
                    }
                } else {
                    field.setRequired(required);
                }
            }
        }
    }

    public boolean getSecurityFrameEditable() {
        return userSessionTools.isCurrentUserInProcRole(getItem(), "Initiator") || isUserCardCreator();
    }

    protected boolean isUserCardCreator() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        return user.getLogin().equals(getItem().getCreatedBy()) || user.equals(getItem().getSubstitutedCreator());
    }

    protected void typeDoc () {
        if (isExpense) {
            initExpenseBill();
            visiblePaymentRub();
            String[] fildsRequired = {"organization", "company", "companyAccount", "owner", "tax", "currency", "taxSum", "fullSum", "paymentDestination"};
            required(fildsRequired, true);
            budgetExpensesTable.setEditable(getAccessData().getGlobalEditable());
            budgetExpensesBtnPnl.setVisible(getAccessData().getGlobalEditable());
            parentCard.setEditable(getAccessData().getGlobalEditable());

            String state = getItem().getState() != null ? getItem().getState() : "";

            if (getSecurityFrameEditable() && !Arrays.asList(",Improvement,", ",Canceled,", "").contains(state)) {
                if (getItem().getState().equals(",Soglasovan,")) {
                    budgetExpensesTable.removeAllActions();
                    budgetExpensesTable.setEditable(false);
                    budgetExpensesBtnPnl.setVisible(false);
                    actionsFrame.setVisible(false);
                    startProcessButtonsFrame.setVisible(false);
                    cardAttachmentsFrame.setEditable(false);
                    getAccessData().applyAccessDataForContainerEditable(getMainContainer(), false);
                } else {
                    getAccessData().applyAccessDataForContainerEditable(buyTaxCurrency, false);
                    getAccessData().applyAccessDataForContainerEditable(buyInvoice, false);
                }
            }
        }
    }

    protected void initTransactionType(String type) {
        currency.setEditable(getAccessData().getGlobalEditable());
        paymentBudgetOrganization.setVisible(false);
        if (TypeTransactionDebiting.TRANSFERTAX.getId().equals(type) ||
                TypeTransactionDebiting.TRANSFERUNDERCONTRACT.getId().equals(type)) {
            getItem().setCurrency(crmConfig.getCurrencyRub());
            currency.setEditable(false);
            if (TypeTransactionDebiting.TRANSFERTAX.getId().equals(type)) {
                paymentBudgetOrganization.setVisible(true);
            }
        }
    }

    protected void visiblePaymentRub() {
        isPaymentRub.setVisible(false);
        if (getItem().getCurrency() != null) {
            if (!"643".equals(getItem().getCurrency().getCode())) {
                isPaymentRub.setVisible(true);
            }
        }
    }

    protected void initLisener() {
        company.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setName();
                doubleCheckInfo();
                getItem().setCompanyAccount(null);
                if (companyAccountDs.size() == 1) {
                    for (ContractorAccount contractorAccount:companyAccountDs.getItems()) {
                        getItem().setCompanyAccount((ExtContractorAccount) contractorAccount);
                    }
                }
            }
        });

        project.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setName();
            }
        });

        date.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem() != null) {
                    buildPaymentDestination();
                    doubleCheckInfo();
                }
            }
        });

        docNumber.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem() != null) {
                    buildPaymentDestination();
                    doubleCheckInfo();
                }
            }
        });

        fullSum.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem() != null) {
                    buildPaymentDestination();
                    taxCheck();
                }
            }
        });

        tax.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem() != null) {
                    buildPaymentDestination();
                    taxCheck();
                }
            }
        });

        taxSum.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem() != null) {
                    buildPaymentDestination();
                    taxCheck();
                }
            }
        });

        if (isExpense) {
            currency.addListener(new ValueListener() {
                @Override
                public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                    visiblePaymentRub();
                }
            });

            transactionType.addListener(new ValueListener() {
                @Override
                public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                    initTransactionType(value.toString());
                }
            });

            if (!PersistenceHelper.isNew(getItem()) && getItem().getTypeTransaction() != null) {
                initTransactionType(getItem().getTypeTransaction().getId());
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

    protected boolean isValidExpense() {

        if (!isValidatePaymentDate()) {
            messageInfo("Срок оплаты меньше даты создания счета, счет НЕ СОХРАНЕН.");
            return false;
        }

        if (getItem().getFullSum() != null) {
            if (!getTotalDetailsSum().stripTrailingZeros().equals(getItem().getFullSum().stripTrailingZeros())) {
                messageInfo("Не совпадают сумма счета и суммы по бюджетам, счет НЕ СОХРАНЕН.");
                return false;
            }
        }

        if (!bugetItem()) {
            messageInfo("Статья бюджета не заполена, счет НЕ СОХРАНЕН");
            return false;
        }

        if (!department()) {
            messageInfo("Подразделение ЦФО не заполено, счет НЕ СОХРАНЕН");
            return false;
        }

        return true;
    }

    protected BigDecimal getTotalDetailsSum() {
        BigDecimal totalDetailsSum = BigDecimal.valueOf(0);
        for (InvDocBugetDetail bugetDetail : invDocBugetDetailsDs.getItems()) {
            if (bugetDetail.getFullSum()!= null) {
                totalDetailsSum = totalDetailsSum.add(bugetDetail.getFullSum());
            }
        }
        return totalDetailsSum;
    }

    protected boolean bugetItem() {
        for (InvDocBugetDetail bugetDetail : invDocBugetDetailsDs.getItems()) {
            if (bugetDetail.getBugetItem() == null) {
                return false;
            }
        }
        return true;
    }

    protected boolean department() {
        for (InvDocBugetDetail bugetDetail : invDocBugetDetailsDs.getItems()) {
            if (bugetDetail.getDepartment() == null) {
                return false;
            }
        }
        return true;
    }

    protected boolean isValidatePaymentDate() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(getItem().getDate());
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(getItem().getPaymentDate());

        if (cal2.compareTo(cal1) == -1) {
            return false;
        }
        return true;
    }

    protected ExtProject getProject() {
        Map<String, Object> params = new HashMap<>();
        params.clear();
        params.put("view", "1c");
        params.put("id", getItem().getProject().getId());
        return work1C.buildQuery(ExtProject.class, params);
    }

    protected void doubleCheckInfo() {
        if (isExpense && getItem().getNumber() != null &&
                getItem().getDate() != null && getItem().getCompany() != null &&
                PersistenceHelper.isNew(getItem())) {
            if (doubleCheck()) {
                invDoubleWarning.setVisible(true);
            } else {
                invDoubleWarning.setVisible(false);
            }
        }
    }

}