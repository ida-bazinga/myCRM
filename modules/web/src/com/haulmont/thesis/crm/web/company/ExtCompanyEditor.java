package com.haulmont.thesis.crm.web.company;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.core.app.FormatTools;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.CorrespondentAttachment;
import com.haulmont.thesis.crm.core.app.CreateCompanyAndAddressDaData;
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.core.app.ValidINN;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.web.ui.account.ExtAccountsFrame;
import com.haulmont.thesis.gui.app.DocumentCopySupport;
import com.haulmont.thesis.web.ui.common.CardTabSheetHelper;
import com.haulmont.thesis.web.ui.correspondencehistory.CorrespondenceHistoryFrame;
import com.haulmont.thesis.web.ui.entitylogframe.EntityLogFrame;
import com.haulmont.workflow.core.entity.Card;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ExtCompanyEditor<T extends ExtCompany> extends AbstractEditor<T> {

    private List<String> initializedTabs = new ArrayList<>();
    protected boolean editable = true;

    @Named("tasksTable.create")
    private CreateAction tasksCreateAction;
    @Named("companyDs")
    protected Datasource<T> companyDs;
    @Named("attachmentsDs")
    protected CollectionDatasource<CorrespondentAttachment, UUID> attachmentsDs;
    @Named("activitiesDs")
    protected CollectionDatasource<Activity, UUID> activitiesDs;
    @Named("linesOfBusinessDs")
    protected CollectionDatasource<LineOfBusinessCompaniesFlags, UUID> linesOfBusinessDs;
    @Named("integraStateLabel1")
    private Label integraStateLabel;
    @Named("tabsheet")
    protected TabSheet tabsheet;
    @Named("legalFormBox")
    protected BoxLayout legalFormBox; //Организационно-правовая форма
    @Named("contactsDs")
    protected CollectionDatasource<ContactPerson, UUID> contactsDs;
    @Named("cardDS")
    protected  CollectionDatasource cardDS;
    @Named("communicationsDs")
    protected  CollectionDatasource communicationsDs;
    @Named("lineOfBusinessCompaniesFlagsRemoveBtn")
    protected Button lineOfBusinessCompaniesFlagsRemoveBtn;
    @Named("lineOfBusinessCompaniesFlagsTable")
    protected Table lineOfBusinessCompaniesFlagsTable;
    @Named("popupButtonAction")
    protected PopupButton popupButtonAction;
    @Named("popupButtonAction1С")
    protected PopupButton popupButtonAction1С;
    @Named("companyType")
    protected LookupField companyType;

    @Inject
    protected Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private Work1C work1C;
    @Inject
    protected FormatTools formatTools;
    @Inject
    protected CardTabSheetHelper cardTabSheetHelper;
    @Inject
    protected ValidINN validINN;
    @Inject
    protected CreateCompanyAndAddressDaData createCompanyAndAddressDaData;
    @Inject
    protected MyUtilsService myUtilsService;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected DocumentCopySupport documentCopySupport;


    protected GroupTable docsGroupTable;
    protected int priority;
    protected boolean isEdit;




    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidth(1200).setHeight(900).setResizable(true);
        initLazyTabs();
        cardTabSheetHelper.addCounterOnTab(attachmentsDs, tabsheet, "attachmentsTab", null);
        lineOfBusinessCompaniesFlagsRemoveBtn.setAction(RemoveAction());
    }

    private void initNewControls()
    {
        ExtCompany item = getItem();
        BoxLayout parentCompanyBox = getComponentNN("parentCompanyBox");
        BoxLayout innogrnBox = getComponentNN("innogrnBox");
        BoxLayout kppokpoBox = getComponentNN("kppokpoBox");
        BoxLayout nonResidentBox = getComponentNN("nonResidentBox");
        BoxLayout residentBox = getComponentNN("residentBox");
        TextField inn = getComponent("inn");
        TextField kpp = getComponent("kpp");
        TextField fullName = getComponent("fullName");
        PickerField legalAddressField = getComponent("legalAddressField");
        kppokpoBox.setVisible(!item.getCompanyType().equals(CompanyTypeEnum.person));
        innogrnBox.setVisible(!item.getCompanyType().equals(CompanyTypeEnum.nonResident));
        kppokpoBox.setVisible(!item.getCompanyType().equals(CompanyTypeEnum.nonResident));
        nonResidentBox.setVisible(item.getCompanyType().equals(CompanyTypeEnum.nonResident));
        residentBox.setVisible(!item.getCompanyType().equals(CompanyTypeEnum.nonResident));
        parentCompanyBox.setVisible(item.getCompanyType().equals(CompanyTypeEnum.branch));

        inn.setEditable(!isEdit);
        kpp.setEditable(!isEdit);
        legalAddressField.setEnabled(!isEdit);
        fullName.setEditable(!isEdit);
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setPrintInEnglish(false);
    }

    @Override
    protected void postInit() {
        Map<String, Object> values = new HashMap<>();
        values.put("extCompany", companyDs.getItem());
        tasksCreateAction.setInitialValues(values);
        setEditorCaption(getItem());
        legalFormBox.setVisible(!isNonResident());
        Map<String, Object> export1C = getStatusExport1C();
        boolean isExport1C = Boolean.valueOf(export1C.get("state").toString());
        integraStateLabel.setValue(export1C.get("stateInfo"));
        loadCommunicationsDs(); //загрузка средств связи
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1;
        popupButtonActionInit(isExport1C);
        //роль на редактирования контрагента
        isEdit = userSessionSource.getUserSession().getRoles().contains("1СCompanyEditor") ? false : isExport1C;
        companyType.setEnabled(!isEdit);
    }

    protected void popupButtonActionInit(boolean isExport1C) {
        if (!PersistenceHelper.isNew(getItem())) {
            popupButtonAction1С.addAction(new BaseAction("popupExportEFI") {
                @Override
                public void actionPerform(Component component) {
                    export(ExtSystem.BP1CEFI);
                }

                @Override
                public String getCaption() {
                    return messages.getMessage(getClass(), "exportEFI");
                }
            });
            popupButtonAction1С.addAction(new BaseAction("popupExportNeva") {
                @Override
                public void actionPerform(Component component) {
                    export (ExtSystem.BP1CNEVA);
                }

                @Override
                public String getCaption() {
                    return messages.getMessage(getClass(), "exportNeva");
                }
            });
            popupButtonAction.addAction(new BaseAction("popupCreateOrd") {
                @Override
                public void actionPerform(Component component) {
                    createOrdDoc();
                }

                @Override
                public String getCaption() {
                    return messages.getMessage(getClass(), "createOrdDoc");
                }
            });

            popupButtonAction.addAction(new BaseAction("popupCreateExpenseBill") {
                @Override
                public void actionPerform(Component component) {
                    createExpenseBill();
                }

                @Override
                public String getCaption() {
                    return messages.getMessage(getClass(), "createExpenseBill");
                }
            });
        }

        if (isExport1C) {
            popupButtonAction.addAction(new BaseAction("popupSendRequest") {
                @Override
                public void actionPerform(Component component) {
                    sendRequest();
                }

                @Override
                public String getCaption() {
                    return messages.getMessage(getClass(), "sendRequest");
                }
            });
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        myUtilsService.updateCompanyTxtSearch(getItem().getId());
        commit_HistoryCompanyCells(toCommit_HistoryCompanyCells());
        return super.postCommit(committed, close);
    }

    protected void loadCommunicationsDs() {
        ExtCompany item = getItem();
        if (PersistenceHelper.isNew(item) && item.getContactPersons() != null) {
            List<ContactPerson> extContactPersonList = item.getContactPersons();
            for (ContactPerson contactPerson : extContactPersonList) {
                ExtContactPerson extContactPerson = (ExtContactPerson) contactPerson;
                contactsDs.addItem(extContactPerson);
                if (extContactPerson.getCommunications() != null) {
                    for (Communication communication : extContactPerson.getCommunications()) {
                        communicationsDs.addItem(communication);
                    }
                }
            }
        }
    }


    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        ExtCompany item = getItem();
        if (!StringUtils.isEmpty(item.getInn()))
        {
            if (!validINN.isValidINN(item.getInn())) {
                String txtMesage = "Неправильное контрольное число ИНН!";
                errors.add(getMessage(txtMesage));
            }
        }
    }

    protected void setEditorCaption(T entity) {
        StringBuilder caption = new StringBuilder();
        if (PersistenceHelper.isNew(entity)) {
            caption.append(getMessage("companyEditor.caption"));
        } else {
            caption.append(formatTools.formatForScreenHistory(entity));

        }
        getWindowManager().setWindowCaption(this, caption.toString(), null);
    }

    private boolean isNonResident() {
        return (getItem().getCompanyType() != null && getItem().getCompanyType().equals(CompanyTypeEnum.nonResident));
    }

    private void initLazyTabs() {
        tabsheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                String tabName = newTab.getName();
                if ("accountsTab".equals(tabName) && !initializedTabs.contains(tabName)) {
                    ExtAccountsFrame accountsFrame = getComponentNN("accountsFrame");
                    accountsFrame.setPropertyName("contractor");
                    accountsFrame.setParentDs(companyDs);
                    accountsFrame.init();
                    initializedTabs.add(tabName);
                    TextField inn = getComponentNN("inn");
                    inn.setEditable(StringUtils.isEmpty(getItem().getInn()));
                    TextField kpp = getComponentNN("kpp");
                    kpp.setEditable(PersistenceHelper.isNew(getItem()));

                    LinkButton openHistoryKpp = getComponent("openHistoryKpp");
                    openHistoryKpp.setAction(new AbstractAction("openHistoryKpp") {
                        @Override
                        public void actionPerform(Component component) {
                            openHistoryWindow(CompanyCellsEnum.kpp);
                        }
                    });
                    LinkButton openHistoryTitle = getComponent("openHistoryFullName");
                    openHistoryTitle.setAction(new AbstractAction("openHistoryFullName") {
                        @Override
                        public void actionPerform(Component component) {
                            openHistoryWindow(CompanyCellsEnum.fullName);
                        }
                    });
                    LinkButton openHistoryAddressFirstDoc = getComponent("openHistoryAddressFirstDoc");
                    openHistoryAddressFirstDoc.setAction(new AbstractAction("openHistoryAddressFirstDoc") {
                        @Override
                        public void actionPerform(Component component) {
                            openHistoryWindow(CompanyCellsEnum.addressFirstDoc);
                        }
                    });

                } else if ("correspondenceHistoryTab".equals(newTab.getName())) {
                    CorrespondenceHistoryFrame historyFrame = getComponentNN("correspondenceHistoryFrame");
                    historyFrame.init();
                    historyFrame.refreshDs();
                } else if ("contractorLogTab".equals(newTab.getName())) {
                    EntityLogFrame contractorLogFrame = getComponentNN("contractorLogFrame");
                    contractorLogFrame.init(Collections.<String, Object>singletonMap(WindowParams.ITEM.toString(), companyDs.getItem()));
                }
                if ("accountsTab".equals(tabName) && getItem().getCompanyType() != null) {
                    initNewControls();
                }
                if("docsTab".equals(tabName)) {
                    Map<String, Object> params = new HashMap<>();
                    List<UUID> listCardId = new ArrayList<>();
                    for (Contract contract: getContractList()) {
                        listCardId.add(contract.getId());
                    }
                    for (OrdDoc ordDoc: getOrdDocList()) {
                        listCardId.add(ordDoc.getId());
                    }
                    for (InvDoc invDoc: getInvDocList()) {
                        listCardId.add(invDoc.getId());
                    }
                    for(AcDoc acDoc: getAcDocList()) {
                        listCardId.add(acDoc.getId());
                    }
                    params.put("searchString", listCardId);
                    cardDS.refresh(params);
                    docsGroupTable = getComponentNN("docsGroupTable");
                    docsGroupTable.addAction(docsTableEditAction());
                    createColumnDocGroupTable();
                }
            }
        });
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        getDialogParams().reset();
    }

    public void companyByInn() {
        ExtCompany item = getItem();
        if (StringUtils.isEmpty(item.getInn())) {
            String txtMesage = "ИНН не заполнен!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        if (!validINN.isValidINN(item.getInn())) {
            String txtMesage = "Неправильное контрольное число ИНН!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        createCompanyToDadata();
    }

    private void createCompanyToDadata() {
        ExtCompany item = getItem();
        try {
            ExtCompany extCompany = createCompanyAndAddressDaData.getCompany(item.getInn());
            if (work1C.validate(extCompany)) {
                item.setLegalForm(work1C.validate(extCompany.getLegalForm()) ? extCompany.getLegalForm() : item.getLegalForm());
                if (!work1C.validate(item.getKpp())) {
                    item.setKpp(extCompany.getKpp());
                }
                if (!work1C.validate(item.getName())) {
                    item.setName(extCompany.getName());
                }
                if (!work1C.validate(item.getFullName())) {
                    item.setFullName(extCompany.getFullName());
                }
                if (!work1C.validate(item.getExtLegalAddress())) {
                    item.setExtLegalAddress(extCompany.getExtLegalAddress());
                }
                Label infoDadata = getComponent("infoDadata");
                infoDadata.setDescription("Данные из ЕГРЮЛ");
                infoDadata.setValue(String.format("КПП: %s | Наименование(сокр.): %s | Полное наименование с ОПФ: %s | Адрес ЕГРЮЛ: %s", extCompany.getKpp(), extCompany.getName(), extCompany.getFullName(), extCompany.getExtLegalAddress().getAddressFirstDoc()));
                item.setOgrn(extCompany.getOgrn());
                item.setOkpo(!work1C.validate(extCompany.getOkpo()) ? extCompany.getOkpo() : item.getOkpo());
                item.setInn(extCompany.getInn());
                item.setActivityStatus(extCompany.getActivityStatus());
                item.setActualizationDate(extCompany.getActualizationDate());
                item.setExtFactAddress(work1C.validate(item.getExtFactAddress()) ? item.getExtFactAddress() : extCompany.getExtFactAddress());
                if (work1C.validate(item.getOkvds(), extCompany.getOkvds())) {
                    if (item.getOkvds().size() > 0) {
                        if (!item.getOkvds().contains(extCompany.getOkvds().get(0))) {
                            item.getOkvds().add(extCompany.getOkvds().get(0));
                        }
                    } else {
                        item.setOkvds(extCompany.getOkvds());
                    }
                }
                if(work1C.validate(extCompany.getContactPersons())) {
                    ContactPerson contactPerson = extCompany.getContactPersons().get(0);
                    boolean isAddContactPerson = true;
                    for (ContactPerson cp:contactsDs.getItems()) {
                        if (contactPerson.getLastName().equals(cp.getLastName())) {
                            isAddContactPerson = false;
                        }
                    }
                    if (isAddContactPerson) {
                        contactPerson.setCompany(item);
                        contactsDs.addItem(contactPerson);
                    }
                }
            }
        } catch (Exception e) {
            showNotification(getMessage(e.getMessage()), NotificationType.HUMANIZED);
            return;
        }
    }

    protected void export(ExtSystem extSystem) {
        String txtMesageCompany = validCompany();
        if (!StringUtils.isEmpty(txtMesageCompany)) {
            showNotification(getMessage(txtMesageCompany), NotificationType.HUMANIZED);
            return;
        }
        work1C.setExtSystem(extSystem);
        exportParentCompany();

        Map<String, Object> params = new HashMap<>();
        params.put(getItem().getMetaClass().getName(), getItem().getId());
        work1C.regLog(params, priority);
        String txtMesage = "Контрагент отправлен в 1С";
        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
    }

    private String validCompany()
    {
        if (getItem().getCompanyType().equals(CompanyTypeEnum.legal) ||
                getItem().getCompanyType().equals(CompanyTypeEnum.government) ||
                getItem().getCompanyType().equals(CompanyTypeEnum.branch))
        {
            if(!work1C.validate(getItem().getInn(), getItem().getKpp())) {
                return "ИНН, КПП - должны быть заполнены!";
            }
        }

        if (getItem().getInn() != null && !getItem().getInn().isEmpty()) {
            if (getItem().getCompanyType().equals(CompanyTypeEnum.legal)) {
                if (getItem().getInn().length() != 10) {
                    return "ИНН должен быть 10 цифр!";
                }
            }
            if (getItem().getCompanyType().equals(CompanyTypeEnum.person)) {
                if (getItem().getInn().length() != 12) {
                    return "ИНН должен быть 12 цифр!";
                }
            }
            if (!validINN.isValidINN(getItem().getInn())) {
                return "Неправильное контрольное число ИНН!";
            }
        }

        if (getItem().getCompanyType().equals(CompanyTypeEnum.legal) ||
                getItem().getCompanyType().equals(CompanyTypeEnum.nonResident) ||
                getItem().getCompanyType().equals(CompanyTypeEnum.branch) ||
                getItem().getCompanyType().equals(CompanyTypeEnum.person) ||
                getItem().getCompanyType().equals(CompanyTypeEnum.government))
        {
            if(!work1C.validate(getItem().getExtLegalAddress())) {
                return "Юридический адрес - должн быть заполнен!";
            }
        }
        if (getItem().getCompanyType().equals(CompanyTypeEnum.branch)) {
            if(!work1C.validate(getItem().getParentCompany())) {
                return "Головной контрагент - должн быть заполнен!";
            }
        }

        return "";
    }

    private void exportParentCompany() {
        if (getItem().getCompanyType().equals(CompanyTypeEnum.branch)) {
            Map<String, Object> params = new HashMap<>();
            params.put(getItem().getMetaClass().getName(), getItem().getParentCompany().getId());
            work1C.regLog(params, priority);
        }
    }

    public void createNewLine() {
        openLookup("crm$LineOfBusiness.browse", new Lookup.Handler() {
            public void handleLookup(Collection items) {
                ExtCompany item = getItem();
                if (items != null && items.size() > 0) {
                    for (Object obj:items) {
                        LineOfBusiness lineOfBusiness = (LineOfBusiness)obj;
                        if(!isLineOfBusinessCompaniesFlags(lineOfBusiness.getId())) {
                            LineOfBusinessCompaniesFlags newLineOfBusinessCompanies = metadata.create(LineOfBusinessCompaniesFlags.class);
                            newLineOfBusinessCompanies.setCompany(item);
                            newLineOfBusinessCompanies.setLineOfBusiness(lineOfBusiness);
                            linesOfBusinessDs.addItem(newLineOfBusinessCompanies);
                        }
                    }
                }
            }
        }, WindowManager.OpenType.NEW_TAB);
    }

    private boolean isLineOfBusinessCompaniesFlags(UUID Id) {
        boolean isResult = false;
        for (LineOfBusinessCompaniesFlags lineOfBusinessCompaniesFlags:linesOfBusinessDs.getItems()) {
            if (lineOfBusinessCompaniesFlags.getLineOfBusiness() != null) {
                if (lineOfBusinessCompaniesFlags.getLineOfBusiness().getId().compareTo(Id) == 0) {
                    isResult = true;
                }
            }
        }
        return isResult;
    }

    public void onCreate(final int numBut) {
        Address newItem = metadata.create(Address.class);
        final Window editor = openEditor("crm$Address.edit", newItem, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                Address address = (Address) editor.getDsContext().get("mainDs").getItem();
                if (work1C.validate(address)) {
                    if (numBut == 1) {
                        getItem().setExtFactAddress(address);
                    }
                    if (numBut == 3) {
                        getItem().setExtPostalAddress(address);
                    }
                    if (work1C.validate(address.getAddressFirstDoc())) {
                        if (numBut == 2) {
                            getItem().setExtLegalAddress(address);
                        }
                    }
                }
            }
        });
    }

    public void onCreateFact()
    {
        onCreate(1);
    }

    public void onCreateLegal()
    {
        onCreate(2);
    }

    public void onCreatePostal()
    {
        onCreate(3);
    }

    private List<Contract> getContractList() {
        LoadContext loadContext = new LoadContext(Contract.class).setView("_local");
        loadContext.setQueryString("select e from df$Contract e where e.contractor.id=:entityId").setParameter("entityId", getItem().getId());
        return dataManager.loadList(loadContext);
    }

    private List<OrdDoc> getOrdDocList() {
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.company.id=:entityId").setParameter("entityId", getItem().getId());
        return dataManager.loadList(loadContext);
    }

    private List<InvDoc> getInvDocList() {
        LoadContext loadContext = new LoadContext(InvDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$InvDoc e where e.company.id=:entityId").setParameter("entityId", getItem().getId());
        return dataManager.loadList(loadContext);
    }

    private List<AcDoc> getAcDocList() {
        LoadContext loadContext = new LoadContext(AcDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$AcDoc e where e.company.id=:entityId").setParameter("entityId", getItem().getId());
        return dataManager.loadList(loadContext);
    }

    private Contract getContract(UUID Id) {
        LoadContext loadContext = new LoadContext(Contract.class).setView("_local");
        loadContext.setQueryString("select e from df$Contract e where e.id=:Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    private OrdDoc getOrdDoc(UUID Id) {
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.id=:Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    private InvDoc getInvDoc(UUID Id) {
        LoadContext loadContext = new LoadContext(InvDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$InvDoc e where e.id=:Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    private AcDoc getAcDoc(UUID Id) {
        LoadContext loadContext = new LoadContext(AcDoc.class).setView("_local");
        loadContext.setQueryString("select e from crm$AcDoc e where e.id=:Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    public void openEditWindow() {
        Card card = docsGroupTable.getSingleSelected();
        if(card == null) {
            String txtMesage = "Не выбран документ!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        if (card.getCategory() == null) {
            String txtMesage = "Не распознан тип документа!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        String entityName = card.getCategory().getEntityType();
        String windowAlias = String.format("%s.edit", entityName);

        final Window editor = openEditor(windowAlias, getNewItem(card, entityName) , WindowManager.OpenType.THIS_TAB);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                cardDS.refresh();
            }
        });
    }

    public Entity getNewItem(Card card, String entityName) {

        UUID Id = card.getId();
        if("crm$OrdDoc".equals(entityName)){
            return getOrdDoc(Id);
        }
        if("crm$InvDoc".equals(entityName)){
            return  getInvDoc(Id);
        }
        if("crm$AcDoc".equals(entityName)) {
            return getAcDoc(Id);
        }
        if("df$Contract".equals(entityName)){
            return getContract(Id);
        }
        return null;
    }

    protected Action docsTableEditAction()  {
        return new EditAction(docsGroupTable) {
            @Override
            public void actionPerform(Component component) {
                openEditWindow();
            }
        };
    }

    public void createColumnDocGroupTable() {
        String cellName = "status";
        docsGroupTable.addGeneratedColumn(cellName, new Table.ColumnGenerator<Card>() {
            @Override
            public Component generateCell(final Card entity) {
                Label label = componentsFactory.createComponent(Label.NAME);
                String statusName = work1C.getStausIntegrationResolver(entity.getId(), entity.getCategory().getEntityType());
                label.setValue(statusName);
                return label;
            }
        });
        docsGroupTable.getColumn(cellName).setCaption("Статус");
        docsGroupTable.getColumn(cellName).setWidth(80);
    }

    protected Action RemoveAction()  {
        return new RemoveAction(lineOfBusinessCompaniesFlagsTable) {
            @Override
            protected void afterRemove(Set selected) {
                companyDs.getItem().getLinesOfBusinessFlags().removeAll(selected);
            }
        };
    }

    protected void openHistoryWindow(final CompanyCellsEnum companyCellsEnum) {
        Map<String, Object> windowParams = new HashMap<>();
        final ExtCompany item = getItem();
        windowParams.put("company", item);
        windowParams.put("companyCells", companyCellsEnum);
        Window window = openWindow("crm$HistoryCompanyCells.browse", WindowManager.OpenType.THIS_TAB, windowParams);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                boolean isCommit = false;
                HistoryCompanyCells historyCompanyCells = getHistoryCompanyCellsCurrent(companyCellsEnum);
                if (work1C.validate(historyCompanyCells)) {
                    String historyCompanyCells_item = historyCompanyCells.getItem();

                    if (companyCellsEnum.equals(CompanyCellsEnum.kpp)) {
                        String kpp = work1C.validate(item.getKpp()) ? item.getKpp() : "";
                        if (!kpp.equals(historyCompanyCells_item)) {
                            item.setKpp(historyCompanyCells_item);
                            isCommit = true;
                        }
                    }

                    if (companyCellsEnum.equals(CompanyCellsEnum.fullName)) {
                        String fullName = work1C.validate(item.getFullName()) ? item.getFullName() : "";
                        if (!fullName.equals(historyCompanyCells_item)) {
                            item.setFullName(historyCompanyCells_item);
                            isCommit = true;
                        }
                    }

                    if (companyCellsEnum.equals(CompanyCellsEnum.addressFirstDoc)) {
                        String addressFirstDoc = work1C.validate(item.getExtLegalAddress()) ?
                                work1C.validate(item.getExtLegalAddress().getAddressFirstDoc()) ?
                                        item.getExtLegalAddress().getAddressFirstDoc() : "" : "";

                        if (!addressFirstDoc.equals(historyCompanyCells_item)) {
                            item.setExtLegalAddress(historyCompanyCells.getAddress());
                            isCommit = true;
                        }
                    }

                    if (isCommit) {
                        commit();
                    }

                }
            }
        });
    }

    protected HistoryCompanyCells getHistoryCompanyCellsCurrent(CompanyCellsEnum companyCellsEnum) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "historyCompanyCells");
        params.put("company.id", getItem());
        params.put("companyCells", companyCellsEnum);
        params.put("sort", "order by e.startDate desc");
        return work1C.buildQuery(HistoryCompanyCells.class, params);
    }

    protected Set<Entity> toCommit_HistoryCompanyCells() {
        Set<Entity> toCommit = new HashSet<>();

        boolean isEdit = false;
        HistoryCompanyCells historyCompanyCells = getHistoryCompanyCellsCurrent(CompanyCellsEnum.fullName);
        if (work1C.validate(getItem().getFullName())) {
            if (historyCompanyCells == null) {
                historyCompanyCells = createHistoryCompanyCells(CompanyCellsEnum.fullName, getItem().getFullName(), null);
                isEdit = true;
            } else if(!historyCompanyCells.getItem().equals(getItem().getFullName())) {
                historyCompanyCells.setItem(getItem().getFullName());
                isEdit = true;
            }
            if (isEdit) {
                toCommit.add(historyCompanyCells);
            }
        }

        isEdit = false;
        historyCompanyCells = getHistoryCompanyCellsCurrent(CompanyCellsEnum.addressFirstDoc);
        if (work1C.validate(getItem().getExtLegalAddress())) {
            if (work1C.validate(getItem().getExtLegalAddress().getAddressFirstDoc())) {
                if (historyCompanyCells == null) {
                    historyCompanyCells = createHistoryCompanyCells(CompanyCellsEnum.addressFirstDoc,
                            getItem().getExtLegalAddress().getAddressFirstDoc(), getItem().getExtLegalAddress());
                    isEdit = true;
                } else if (!historyCompanyCells.getAddress().equals(getItem().getExtLegalAddress()) ||
                        !historyCompanyCells.getItem().equals(getItem().getExtLegalAddress().getAddressFirstDoc())) {
                    historyCompanyCells.setAddress(getItem().getExtLegalAddress());
                    historyCompanyCells.setItem(getItem().getExtLegalAddress().getAddressFirstDoc());
                    isEdit = true;
                }
                if (isEdit) {
                    toCommit.add(historyCompanyCells);
                }
            }
        }

        isEdit = false;
        historyCompanyCells = getHistoryCompanyCellsCurrent(CompanyCellsEnum.kpp);
        if (work1C.validate(getItem().getKpp())) {
            if (historyCompanyCells == null) {
                historyCompanyCells = createHistoryCompanyCells(CompanyCellsEnum.kpp, getItem().getKpp(), null);
                isEdit = true;
            } else if(!historyCompanyCells.getItem().equals(getItem().getKpp())) {
                historyCompanyCells.setItem(getItem().getKpp());
                isEdit = true;
            }
            if (isEdit) {
                toCommit.add(historyCompanyCells);
            }
        }

        return toCommit;
    }

    protected HistoryCompanyCells createHistoryCompanyCells(CompanyCellsEnum companyCellsEnum, String item, Address address){
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        Calendar calendar = Calendar.getInstance(tz);
        calendar.set(2001,0,1);
        HistoryCompanyCells historyCompanyCells = metadata.create(HistoryCompanyCells.class);
        historyCompanyCells.setCompany(getItem());
        historyCompanyCells.setCompanyCells(companyCellsEnum);
        historyCompanyCells.setItem(item);
        historyCompanyCells.setAddress(address);
        historyCompanyCells.setStartDate(calendar.getTime());
        return historyCompanyCells;
    }

    private void commit_HistoryCompanyCells(Set<Entity> toCommit) {
        if(toCommit.size() > 0) {
            dataManager.commit(new CommitContext(toCommit));
        }
    }

    public void sendRequest() {
        Map<String, Object> windowParams = new HashMap<>();
        windowParams.put("company", getItem());
        Window window = openWindow("crm$SendRequestCompany", WindowManager.OpenType.DIALOG, windowParams);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {

            }
        });
    }

    protected void createOrdDoc() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "reassignmentedit");
        params.put("template", true);
        params.put("templateName", "DefaultSalesOrd");
        OrdDoc template = work1C.buildQuery(OrdDoc.class, params);
        OrdDoc ordDoc = documentCopySupport.copy(template);

        ordDoc.setCompany(getItem());
        ordDoc.setDocCategory(template.getDocCategory());
        ordDoc.setIntegrationResolver(null);
        Window window = openEditor("crm$OrdDoc.edit",ordDoc, WindowManager.OpenType.THIS_TAB);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {

            }
        });
    }

    public void createExpenseBill() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "reassignmentedit");
        params.put("template", true);
        params.put("templateName", "Основной");
        InvDoc template = work1C.buildQuery(InvDoc.class, params);

        InvDoc invDoc = documentCopySupport.copy(template);
        invDoc.setCompany(getItem());
        invDoc.setDocCategory(template.getDocCategory());
        invDoc.setIntegrationResolver(null);
        Window window = openEditor("crm$InvDoc.edit",invDoc, WindowManager.OpenType.THIS_TAB);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {

            }
        });
    }

    protected Map<String, Object> getStatusExport1C() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("entityName", getItem().getMetaClass().getName());
        params.put("entityId", getItem().getId());
        List<IntegrationResolver> integrationResolverList = work1C.buildQueryList(IntegrationResolver.class, params);
        params.clear();

        if (integrationResolverList.size() == 0) {
            params.put("state", false);
            params.put("stateInfo", work1C.getMassegePack(StatusDocSales.NEW));
        }

        if (integrationResolverList.size() == 1) {
            params.put("state", true);
            params.put("stateInfo", getMessagesStateInfo(integrationResolverList.get(0)));
        }

        if (integrationResolverList.size() == 2) {
            params.put("state", true);
            String statusInfo = "";

            for (IntegrationResolver  ir: integrationResolverList) {
                statusInfo = statusInfo + "/" + getMessagesStateInfo(ir);
            }

            params.put("stateInfo", statusInfo);
        }

        return params;
    }

    protected String getMessagesStateInfo(IntegrationResolver ir) {
        return String.format("%s: %s", work1C.getMassegePack(ir.getExtSystem()),  work1C.getMassegePack(ir.getStateDocSales()));
    }


}
