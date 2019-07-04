package com.haulmont.thesis.crm.web.company;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.core.entity.*;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ExtCompanyMergingdup extends AbstractWindow {
    @Named("mainDs")
    protected CollectionDatasource<ExtCompany, UUID> mainDs;
    @Inject
    protected Table mainTable;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Messages messages;
    @Inject
    private TextField name, fullName, inn, ogrn, kpp, okpo, webAddress;
    @Inject
    private TextArea alternativeName, comment;
    @Inject
    private PickerField extFactAddress, extLegalAddress, extPostalAddress, parentCompany;
    @Inject
    private LookupField companyType, legalForm, companyScale;

    protected ExtCompany extCompanyMerging;
    protected Set<Entity> toCommit = new HashSet<>();
    protected Set<Entity> toRemove = new HashSet<>();
    protected Set<Entity> notRemove = new HashSet<>();
    protected NonDuplicateCompanies nonDuplicateCompanies;
    protected DuplicateCompany duplicateCompany;

    private String[] cells = {"countDoc", "companyType", "legalForm", "name", "fullName",
             "inn", "ogrn", "kpp", "okpo", "extLegalAddress",  "extFactAddress", "extPostalAddress",
             "webAddress", "parentCompany", "companyScale"};

    private String[] cellsRuName = {"Ко-во док.", "Вид", "Орг.-прав.форма", "Наименование(сокращ.)", "Название для документов",
            "ИНН", "ОГРН", "КПП", "ОКПО", "Юр.адрес", "Факт.адрес", "Почтовый адрес",
            "Веб адрес", "Головной контрагент", "Масштаб компании"};






    @Override
    public void init(final Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidth(1024).setHeight(768).setResizable(true);
        this.duplicateCompany = (DuplicateCompany) params.get("duplicateCompany");
        mainDs.refresh(params);
    }

    @Override
    public void ready() {
        legalForm.setOptionsDatasource(getFormOfIncorporationDs());
        companyScale.setOptionsDatasource(getCompanyScaleDs());
        companyType.setOptionsMap(getCompanyType());
        createCell();
        setStaticText();
        initEntity();
        mainTable.setIconProvider(getIconProvider());
        styleProvider();
    }

    private void createCell() {
        int i=0;
        for (final String cellName:cells) {

            mainTable.addGeneratedColumn(cellName, new Table.ColumnGenerator<ExtCompany>() {
                @Override
                public Component generateCell(final ExtCompany entity) {

                    if("countDoc".equals(cellName)) {
                        Label label = componentsFactory.createComponent(Label.NAME);
                        label.setValue(getCountDoc(entity.getId()));
                        return label;
                    }
                    else {
                        LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                        final String valueStr = getValue(cellName, entity);
                        linkButton.setCaption(valueStr);
                        linkButton.setAction(new AbstractAction("") {
                            @Override
                            public void actionPerform(Component component) {
                                setValue(cellName, entity);
                            }
                        });
                        return linkButton;
                    }
                }
            });
            mainTable.setColumnCaption(cellName, cellsRuName[i++]);
        }
    }

    private String getValue(String cellName, ExtCompany extCompany) {
        String txtNull = "Нет данных!";
        if ("companyType".equals(cellName)) {
            if(CompanyTypeEnum.legal.equals(extCompany.getCompanyType())) {
                return messages.getMessage(CompanyTypeEnum.legal);
            }
            if(CompanyTypeEnum.person.equals(extCompany.getCompanyType())) {
                return messages.getMessage(CompanyTypeEnum.person);
            }
            if(CompanyTypeEnum.nonResident.equals(extCompany.getCompanyType())) {
                return messages.getMessage(CompanyTypeEnum.nonResident);
            }
            if(CompanyTypeEnum.government.equals(extCompany.getCompanyType())) {
                return messages.getMessage(CompanyTypeEnum.government);
            }
            if(CompanyTypeEnum.branch.equals(extCompany.getCompanyType())) {
                return messages.getMessage(CompanyTypeEnum.branch);
            }
        }
        if ("legalForm".equals(cellName)) {
            return extCompany.getLegalForm() != null ? extCompany.getLegalForm().getShortName() : txtNull;
        }
        if ("name".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getName()) ? extCompany.getName() : txtNull;
        }
        if ("fullName".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getFullName()) ? extCompany.getFullName() : txtNull;
        }
        if ("inn".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getInn()) ? extCompany.getInn() : txtNull;
        }
        if ("ogrn".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getOgrn()) ? extCompany.getOgrn() : txtNull;
        }
        if ("kpp".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getKpp()) ? extCompany.getKpp() : txtNull;
        }
        if ("okpo".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getOkpo()) ? extCompany.getOkpo() : txtNull;
        }
        if ("extFactAddress".equals(cellName)) {
            return extCompany.getExtFactAddress() != null ? extCompany.getExtFactAddress().getName_ru() : txtNull;
        }
        if ("extLegalAddress".equals(cellName)) {
            return extCompany.getExtLegalAddress() != null ? extCompany.getExtLegalAddress().getName_ru() : txtNull;
        }
        if ("extPostalAddress".equals(cellName)) {
            return extCompany.getExtPostalAddress() != null ? extCompany.getExtPostalAddress().getName_ru() : txtNull;
        }
        if ("webAddress".equals(cellName)) {
            return !StringUtils.isEmpty(extCompany.getWebAddress()) ? extCompany.getWebAddress() : txtNull;
        }
        if ("parentCompany".equals(cellName)) {
            return extCompany.getParentCompany() != null ? extCompany.getParentCompany().getName() : txtNull;
        }
        if("companyScale".equals(cellName)) {
            return extCompany.getCompanyScale() != null ? extCompany.getCompanyScale().getName_ru() : txtNull;
        }
        return txtNull;
    }

    private void setValue(String cellName, ExtCompany extCompany) {
        if ("companyType".equals(cellName)) {
            companyType.setValue(extCompany.getCompanyType());
        }
        if ("legalForm".equals(cellName)) {
            legalForm.setValue(extCompany.getLegalForm());
        }
        if("name".equals(cellName)) {
            name.setValue(extCompany.getName());
        }
        if("fullName".equals(cellName)) {
            fullName.setValue(extCompany.getFullName());
        }
        if ("inn".equals(cellName)) {
            inn.setValue(extCompany.getInn());
        }
        if ("ogrn".equals(cellName)) {
            ogrn.setValue(extCompany.getOgrn());
        }
        if ("kpp".equals(cellName)) {
            kpp.setValue(extCompany.getKpp());
        }
        if ("okpo".equals(cellName)) {
            okpo.setValue(extCompany.getOkpo());
        }
        if ("extFactAddress".equals(cellName)) {
            extFactAddress.setValue(extCompany.getExtFactAddress());
        }
        if ("extLegalAddress".equals(cellName)) {
            extLegalAddress.setValue(extCompany.getExtLegalAddress());
        }
        if ("extPostalAddress".equals(cellName)) {
            extPostalAddress.setValue(extCompany.getExtPostalAddress());
        }
        if ("webAddress".equals(cellName)) {
            webAddress.setValue(extCompany.getWebAddress());
        }
        if ("parentCompany".equals(cellName)) {
            parentCompany.setValue(extCompany.getParentCompany());
        }
        if("companyScale".equals(cellName)) {
            companyScale.setValue(extCompany.getCompanyScale());
        }
    }


    private Table.IconProvider<ExtCompany> getIconProvider(){
        return new Table.IconProvider<ExtCompany>() {
            @Nullable
            @Override
            public String getItemIcon(ExtCompany entity) {

                if (isNonDuplicate(entity.getId())) {
                    return "theme:icons/item-remove.png";
                }
                if (isTo1c(entity.getId())) {
                    return "theme:icons/ok.png";
                }
                return "";
            }
        };
    }

    private void styleProvider() {
        mainTable.addStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, @Nullable String property) {
                if (entity.getId().equals(extCompanyMerging.getId())) {
                    return "thesis-task-overdue";
                }
                if (isTo1c(((UUID) entity.getId())) || isNonDuplicate((UUID)entity.getId())) {
                    return "thesis-task-finished";
                }
                return null;
            }
        });
    }

    private boolean isTo1c(UUID entityId) {
        LoadContext loadContext = new LoadContext(IntegrationResolver.class).setView("1c");
        loadContext.setQueryString("select e from crm$IntegrationResolver e where e.entityName='crm$Company' and e.entityId=:entityId").setParameter("entityId", entityId);
        IntegrationResolver ir = dataManager.load(loadContext);
        if(ir != null) {
            if (!StringUtils.isEmpty(ir.getExtId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNonDuplicate(UUID entityId) {
        LoadContext loadContext = new LoadContext(NonDuplicateCompanies.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$NonDuplicateCompanies e where e.firstCompany=:firstCompany and e.secondCompany=:secondCompany")
                .setParameter("firstCompany", extCompanyMerging.getId()).setParameter("secondCompany", entityId);
        nonDuplicateCompanies = dataManager.load(loadContext);
        if(nonDuplicateCompanies != null) {
            return true;
        }
        return false;
    }

    public void onCreate(final int numBut) {
        Address newItem = metadata.create(Address.class);
        final Window editor = openEditor("crm$Address.edit", newItem , WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                Address address = (Address) editor.getDsContext().get("mainDs").getItem();
                if(numBut == 1) {
                    extFactAddress.setValue(address);
                }
                if(numBut == 2) {
                    extLegalAddress.setValue(address);
                }
                if(numBut == 3) {
                    extPostalAddress.setValue(address);
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

    private int getCountDoc(UUID Id) {
        LoadContext loadContext = new LoadContext(Contract.class).setView("browse");
        loadContext.setQueryString("select e from df$Contract e where e.contractor.id=:Id").setParameter("Id", Id);
        return dataManager.loadList(loadContext).size();
    }

    private CollectionDatasource<FormOfIncorporation, UUID> getFormOfIncorporationDs() {
        CollectionDatasource<FormOfIncorporation, UUID> ds = new DsBuilder()
                .setViewName("_minimal")
                .setMetaClass(metadata.getSession().getClass(FormOfIncorporation.class)).buildCollectionDatasource();
        ds.setQuery("select e from crm$FormOfIncorporation e");
        ds.refresh();
        return ds;
    }

    private CollectionDatasource<CompanyScale, UUID> getCompanyScaleDs() {
        CollectionDatasource<CompanyScale, UUID> ds = new DsBuilder()
                .setViewName("_minimal")
                .setMetaClass(metadata.getSession().getClass(CompanyScale.class)).buildCollectionDatasource();
        ds.setQuery("select e from crm$CompanyScale e");
        ds.refresh();
        return ds;
    }

    private Map<String, Object> getCompanyType() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(messages.getMessage(CompanyTypeEnum.legal), CompanyTypeEnum.legal);
        map.put(messages.getMessage(CompanyTypeEnum.person), CompanyTypeEnum.person);
        map.put(messages.getMessage(CompanyTypeEnum.nonResident), CompanyTypeEnum.nonResident);
        map.put(messages.getMessage(CompanyTypeEnum.government), CompanyTypeEnum.government);
        map.put(messages.getMessage(CompanyTypeEnum.branch), CompanyTypeEnum.branch);
        return map;
    }

    private void setStaticText() {
        String altName = "";
        String notes = "";
        for (ExtCompany company: mainDs.getItems()) {
            if (!StringUtils.isEmpty(company.getAlternativeName())) {
                altName = altName + "|" + company.getAlternativeName();
            }
            if (!StringUtils.isEmpty(company.getNotes())) {
                notes = notes + "|" + company.getNotes();
            }
        }
        alternativeName.setValue(altName);
        comment.setValue(notes);
    }

    public void onSelectParentCompany() {
        openLookup("df$Company.browse", new Lookup.Handler() {
            public void handleLookup(Collection items) {
                if (items != null && items.size() > 0) {
                    parentCompany.setValue(items.iterator().next());
                }
            }
        }, WindowManager.OpenType.NEW_TAB);
    }

    @Override
    public boolean close(String actionId) {
        closeMyWindow("close");
        return ((Window) frame).close(actionId);
    }

    private void closeMyWindow(String actionId) {
        mainDs.setAllowCommit(false);
    }

    @Override
    public void closeAndRun(String actionId, Runnable runnable) {
        closeMyWindow("close");
        ((Window) frame).closeAndRun(actionId, runnable);
    }

    public void closeButton() {
        closeMyWindow("close");
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }

    public void mergingButton() {
        String txtMesage = "Объединение дублей прошло успешно!";
        if(mainDs.size() > 1) {
            if (!merging()) {
                txtMesage = "Объединить записи не удалось!";
            }
        }
        else {
            txtMesage = "Объединенить 1 запись нельзя!" ;
        }
        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
    }

    private boolean merging() {
        if(PersistenceHelper.isNew(extCompanyMerging)) {
            return false;
        }
        setExtCompanyMerging();
        linkDocUpdate();
        newToRemove();
        doCommit();
        mainDs.refresh();
        updateDuplicateCompany(DuplicateStatus.merge);
        return true;
    }

    public void onNonDuplicate() {

        if(isValidNonDuplicate()) {
            ExtCompany extCompanyRemove = mainTable.getSingleSelected();

            if (isNonDuplicate(extCompanyRemove.getId())) {
                dataManager.remove(nonDuplicateCompanies);
            }
            else {
                NonDuplicateCompanies nonDuplicateCompanie = metadata.create(NonDuplicateCompanies.class);
                nonDuplicateCompanie.setFirstCompany(extCompanyMerging.getId());
                nonDuplicateCompanie.setSecondCompany(extCompanyRemove.getId());
                dataManager.commit(nonDuplicateCompanie);
            }
            mainDs.refresh();
        }
        else {
            //String txtMesage = "1. Контрагент должен быть выбран! \n 2. Контрагент, не должен быть основным! \n 3. Контрагент, должен быть не отправлен в 1С!";
            String txtMesage = "1. Контрагент должен быть выбран! \n 2. Контрагент, не должен быть основным!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        }
    }

    protected boolean isValidNonDuplicate() {
        if (mainTable.getSingleSelected() != null) {
            ExtCompany extCompanyRemove = mainTable.getSingleSelected();
            if(extCompanyRemove.getId().equals(extCompanyMerging.getId())) {
                return false;
            }
            /*
            if (isTo1c(extCompanyRemove.getId()))
            {
                return false;
            }
            */
            return true;
        }
        return false;
    }

    private void setExtCompanyMerging() {
        extCompanyMerging.setCompanyType((CompanyTypeEnum) companyType.getValue());
        extCompanyMerging.setLegalForm((FormOfIncorporation)legalForm.getValue());
        extCompanyMerging.setName(defaultString(fullName.getValue()));
        extCompanyMerging.setName(defaultString(name.getValue()));
        extCompanyMerging.setFullName(defaultString(fullName.getValue()));
        extCompanyMerging.setFullName(defaultString(fullName.getValue()));
        extCompanyMerging.setInn(defaultString(inn.getValue()));
        extCompanyMerging.setOgrn(defaultString(ogrn.getValue()));
        extCompanyMerging.setKpp(defaultString(kpp.getValue()));
        extCompanyMerging.setOkpo(defaultString(okpo.getValue()));
        extCompanyMerging.setExtFactAddress((Address)extFactAddress.getValue());
        extCompanyMerging.setExtLegalAddress((Address)extLegalAddress.getValue());
        extCompanyMerging.setExtPostalAddress((Address)extPostalAddress.getValue());
        extCompanyMerging.setWebAddress(defaultString(webAddress.getValue()));
        extCompanyMerging.setParentCompany((ExtCompany)parentCompany.getValue());
        extCompanyMerging.setCompanyScale((CompanyScale)companyScale.getValue());
        extCompanyMerging.setAlternativeName(defaultString(alternativeName.getValue()));
        extCompanyMerging.setNotes(defaultString(comment.getValue()));
    }

    private void linkDocUpdate() {
        List<UUID> listCompanyId = new ArrayList<>();
        for (ExtCompany company: mainDs.getItems()) {
            boolean isTo1c = isTo1c(company.getId());
            boolean isNonDuplicate = isNonDuplicate(company.getId());
            boolean isExtCompanyMerging = company.getId().equals(extCompanyMerging.getId());

            if (!isTo1c && !isNonDuplicate && !isExtCompanyMerging) {
                listCompanyId.add(company.getId());
                for (ContactPerson person:company.getContactPersons()) {
                    person.setCompany(extCompanyMerging);
                    toCommit.add(person);
                }
                if (extCompanyMerging.getOkvds().size() > 0) {
                    company.getOkvds().removeAll(extCompanyMerging.getOkvds());
                    if(company.getOkvds().size() > 0) {
                        extCompanyMerging.getOkvds().addAll(company.getOkvds());
                    }
                }
                else {
                    extCompanyMerging.setOkvds(company.getOkvds());
                }
                if(extCompanyMerging.getInformationSources().size() > 0) {
                    company.getInformationSources().removeAll(extCompanyMerging.getInformationSources());
                    if(company.getInformationSources().size() > 0) {
                        extCompanyMerging.getInformationSources().addAll(company.getInformationSources());
                    }
                }
                else {
                    extCompanyMerging.setInformationSources(company.getInformationSources());
                }
            }
            else {
                if (!isExtCompanyMerging && !isNonDuplicate && isTo1c) {
                    company.setName(String.format("%s (не использовать)", company.getName()));
                    toCommit.add(company);
                }
                notRemove.add(company);
            }
        }
        if (listCompanyId.size() > 0) {
            linkDocUpdateDop(listCompanyId);
            toCommit.add(extCompanyMerging);
        }
    }

    private void linkDocUpdateDop(List<UUID> listCompanyId) {
        mergingDocOfficeData(listCompanyId);
        mergingDocOfficeDataAddressee(listCompanyId);
        mergingCallCampaignTarget(listCompanyId);
        mergingCallCampaignTrgt(listCompanyId);
        mergingEmailCampaignTarget(listCompanyId);
        mergingContract(listCompanyId);
        mergingOrdDoc(listCompanyId);
        mergingInvDoc(listCompanyId);
        mergingAcDoc(listCompanyId);
        mergingProject(listCompanyId);
        mergingProduct(listCompanyId);
        mergingPayment(listCompanyId);
        mergingMeetingParticipant(listCompanyId);
        mergingContractorAccount(listCompanyId);
        mergingBaseActivity(listCompanyId);
        mergingExtCompany(listCompanyId);
        mergingExhibitSpace(listCompanyId);
        mergingGuesthouse(listCompanyId);
        mergingProvider(listCompanyId);
        mergingStaff(listCompanyId);
        mergingLineOfBusinessCompany(listCompanyId);
        mergingTask(listCompanyId);
        mergingActivity(listCompanyId);
        mergingActivityOld(listCompanyId);
        mergingCorrespondentAttachment(listCompanyId);
        mergingDuplicateCompanyDetail(listCompanyId);
        mergingPayOrd(listCompanyId);
    }

    private void doCommit() {
        //dataManager.commit(new CommitContext(toCommit, toRemove));
        Set<Entity> isCommit = dataManager.commit(new CommitContext(toCommit));
        if(!isCommit.isEmpty()) {
            dataManager.commit(new CommitContext(new HashSet<>(), toRemove));
        }
    }

    private void mergingDocOfficeData(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(DocOfficeData.class).setView("doc-sender-browse");
        loadContext.setQueryString("select e from df$DocOfficeData e where e.sender.id in (:listId)").setParameter("listId", listId);
        List<DocOfficeData> docOfficeDataList = dataManager.loadList(loadContext);
        if(docOfficeDataList.size() > 0) {
            for (DocOfficeData docOfficeData:docOfficeDataList) {

                docOfficeData.setSender(extCompanyMerging);
                toCommit.add(docOfficeData);
            }
        }
    }

    private void mergingDocOfficeDataAddressee(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(DocOfficeDataAddressee.class).setView("_minimal");
        loadContext.setQueryString("select e from df$DocOfficeDataAddressee e where e.addressee.id in (:listId)").setParameter("listId", listId);
        List<DocOfficeDataAddressee> docOfficeDataAddresseeList = dataManager.loadList(loadContext);
        if(docOfficeDataAddresseeList.size() > 0) {
            for (DocOfficeDataAddressee docOfficeDataAddressee:docOfficeDataAddresseeList) {

                docOfficeDataAddressee.setAddressee(extCompanyMerging);
                toCommit.add(docOfficeDataAddressee);
            }
        }
    }

    private void mergingCallCampaignTarget(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("_minimal");
        loadContext.setQueryString("select e from crm$CallCampaignTarget e where e.company.id in (:listId)").setParameter("listId", listId);
        List<CallCampaignTarget> callCampaignTargetList = dataManager.loadList(loadContext);
        if(callCampaignTargetList.size() > 0) {
            for (CallCampaignTarget callCampaignTarget:callCampaignTargetList) {

                callCampaignTarget.setCompany(extCompanyMerging);
                toCommit.add(callCampaignTarget);
            }
        }
    }

    private void mergingCallCampaignTrgt(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(CallCampaignTrgt.class).setView("edit");
        loadContext.setQueryString("select e from crm$CallCampaignTrgt e where e.company.id in (:listId)").setParameter("listId", listId);
        List<CallCampaignTrgt> callCampaignTrgtList = dataManager.loadList(loadContext);
        if(callCampaignTrgtList.size() > 0) {
            for (CallCampaignTrgt callCampaignTrgt:callCampaignTrgtList) {

                callCampaignTrgt.setCompany(extCompanyMerging);
                toCommit.add(callCampaignTrgt);
            }
        }
    }

    private void mergingEmailCampaignTarget(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(EmailCampaignTarget.class).setView("edit");
        loadContext.setQueryString("select e from crm$EmailCampaignTarget e where e.company.id in (:listId)").setParameter("listId", listId);
        List<EmailCampaignTarget> emailCampaignTargetList = dataManager.loadList(loadContext);
        if(emailCampaignTargetList.size() > 0) {
            for (EmailCampaignTarget emailCampaignTarget:emailCampaignTargetList) {

                emailCampaignTarget.setCompany(extCompanyMerging);
                toCommit.add(emailCampaignTarget);
            }
        }
    }

    private void mergingContract(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Contract.class).setView("browse");
        loadContext.setQueryString("select e from df$Contract e where e.contractor.id in (:listId)").setParameter("listId", listId);
        List<Contract> contractList = dataManager.loadList(loadContext);
        if(contractList.size() > 0) {
            for (Contract contract:contractList) {

                contract.setContractor(extCompanyMerging);
                toCommit.add(contract);
            }
        }
    }

    private void mergingOrdDoc(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("browse");
        loadContext.setQueryString("select e from crm$OrdDoc e where e.company.id in (:listId)").setParameter("listId", listId);
        List<OrdDoc> ordDocList = dataManager.loadList(loadContext);
        if(ordDocList.size() > 0) {
            for (OrdDoc ordDoc:ordDocList) {

                ordDoc.setCompany(extCompanyMerging);
                toCommit.add(ordDoc);
            }
        }
    }

    private void mergingInvDoc(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(InvDoc.class).setView("browse");
        loadContext.setQueryString("select e from crm$InvDoc e where e.company.id in (:listId)").setParameter("listId", listId);
        List<InvDoc> invDocList = dataManager.loadList(loadContext);
        if(invDocList.size() > 0) {
            for (InvDoc invDoc:invDocList) {

                invDoc.setCompany(extCompanyMerging);
                toCommit.add(invDoc);
            }
        }
    }

    private void mergingAcDoc(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(AcDoc.class).setView("browse");
        loadContext.setQueryString("select e from crm$AcDoc e where e.company.id in (:listId)").setParameter("listId", listId);
        List<AcDoc> acDocList = dataManager.loadList(loadContext);
        if(acDocList.size() > 0) {
            for (AcDoc acDoc:acDocList) {

                acDoc.setCompany(extCompanyMerging);
                toCommit.add(acDoc);
            }
        }
    }

    private void mergingProject(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(ExtProject.class).setView("browse");
        loadContext.setQueryString("select e from crm$Project e where e.organizer.id in (:listId)").setParameter("listId", listId);
        List<ExtProject> extProjectList = dataManager.loadList(loadContext);
        if(extProjectList.size() > 0) {
            for (ExtProject extProject:extProjectList) {

                extProject.setOrganizer(extCompanyMerging);
                toCommit.add(extProject);
            }
        }
    }

    private void mergingProduct(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Product.class).setView("browse");
        loadContext.setQueryString("select e from crm$Product e where e.eventOrganizer.id in (:listId)").setParameter("listId", listId);
        List<Product> productList = dataManager.loadList(loadContext);
        if(productList.size() > 0) {
            for (Product product:productList) {

                product.setEventOrganizer(extCompanyMerging);
                toCommit.add(product);
            }
        }
    }

    private void mergingPayment(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Payment.class).setView("1c");
        loadContext.setQueryString("select e from crm$Payment e where e.company.id in (:listId)").setParameter("listId", listId);
        List<Payment> paymentList = dataManager.loadList(loadContext);
        if(paymentList.size() > 0) {
            for (Payment payment:paymentList) {

                payment.setCompany(extCompanyMerging);
                toCommit.add(payment);
            }
        }
    }

    private void mergingMeetingParticipant(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(MeetingParticipant.class).setView("browse");
        loadContext.setQueryString("select e from df$MeetingParticipant e where e.contractor.id in (:listId)").setParameter("listId", listId);
        List<MeetingParticipant> meetingParticipantList = dataManager.loadList(loadContext);
        if(meetingParticipantList.size() > 0) {
            for (MeetingParticipant meetingParticipant:meetingParticipantList) {

                meetingParticipant.setContractor(extCompanyMerging);
                toCommit.add(meetingParticipant);
            }
        }
    }

    private void mergingContractorAccount(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(ContractorAccount.class).setView("contractor");
        loadContext.setQueryString("select e from df$ContractorAccount e where e.contractor.id in (:listId)").setParameter("listId", listId);
        List<ContractorAccount> contractorAccountList = dataManager.loadList(loadContext);
        if(contractorAccountList.size() > 0) {
            for (ContractorAccount contractorAccount:contractorAccountList) {

                contractorAccount.setContractor(extCompanyMerging);
                toCommit.add(contractorAccount);
            }
        }
    }

    private void mergingBaseActivity(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(BaseActivity.class).setView("browse");
        loadContext.setQueryString("select e from crm$BaseActivity e where e.company.id in (:listId)").setParameter("listId", listId);
        List<BaseActivity> baseActivityList = dataManager.loadList(loadContext);
        if(baseActivityList.size() > 0) {
            for (BaseActivity baseActivity:baseActivityList) {
                baseActivity.setCompany(extCompanyMerging);
                toCommit.add(baseActivity);
            }
        }
    }

    private void mergingExtCompany(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(ExtCompany.class).setView("1c");
        loadContext.setQueryString("select e from df$Company e where e.parentCompany.id in (:listId)").setParameter("listId", listId);
        List<ExtCompany> extCompanyList = dataManager.loadList(loadContext);
        if(extCompanyList.size() > 0) {
            for (ExtCompany extCompany:extCompanyList) {
                extCompany.setParentCompany(extCompanyMerging);
                toCommit.add(extCompany);
            }
        }
    }

    private void mergingExhibitSpace(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(ExhibitSpace.class).setView("edit");
        loadContext.setQueryString("select e from crm$ExhibitSpace e where e.operator.id in (:listId)").setParameter("listId", listId);
        List<ExhibitSpace> exhibitSpaceList = dataManager.loadList(loadContext);
        if(exhibitSpaceList.size() > 0) {
            for (ExhibitSpace exhibitSpace:exhibitSpaceList) {
                exhibitSpace.setOperator(extCompanyMerging);
                toCommit.add(exhibitSpace);
            }
        }
    }

    private void mergingGuesthouse(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Guesthouse.class).setView("edit");
        loadContext.setQueryString("select e from crm$Guesthouse e where e.company.id in (:listId)").setParameter("listId", listId);
        List<Guesthouse> guesthouseList = dataManager.loadList(loadContext);
        if(guesthouseList.size() > 0) {
            for (Guesthouse guesthouse:guesthouseList) {
                guesthouse.setCompany(extCompanyMerging);
                toCommit.add(guesthouse);
            }
        }
    }

    private void mergingProvider(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Provider.class).setView("edit");
        loadContext.setQueryString("select e from crm$Provider e where e.company.id in (:listId)").setParameter("listId", listId);
        List<Provider> providerList = dataManager.loadList(loadContext);
        if(providerList.size() > 0) {
            for (Provider provider:providerList) {
                provider.setCompany(extCompanyMerging);
                toCommit.add(provider);
            }
        }
    }

    private void mergingStaff(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Staff.class).setView("browse");
        loadContext.setQueryString("select e from crm$Staff e where e.contractor.id in (:listId)").setParameter("listId", listId);
        List<Staff> staffList = dataManager.loadList(loadContext);
        if(staffList.size() > 0) {
            for (Staff staff:staffList) {
                staff.setContractor(extCompanyMerging);
                toCommit.add(staff);
            }
        }
    }

    private void mergingLineOfBusinessCompany(List<UUID> listId) {
        List<UUID> listCompanyId = new ArrayList<>();
        listCompanyId.add(extCompanyMerging.getId());
        List<LineOfBusinessCompaniesFlags> listCompanyMergingLineOfBusiness = getLineOfBusinessCompaniesFlags(listCompanyId);
        if(listCompanyMergingLineOfBusiness.size() < 1) {
            listCompanyMergingLineOfBusiness = new ArrayList<>();
        }
        List<LineOfBusinessCompaniesFlags> lineOfBusinessList = getLineOfBusinessCompaniesFlags(listId);
        if(lineOfBusinessList.size() > 0) {
            for (LineOfBusinessCompaniesFlags lineOfBusiness:lineOfBusinessList) {
                listCompanyMergingLineOfBusiness = lineOfBusiness(listCompanyMergingLineOfBusiness, lineOfBusiness);
            }
        }
        lineOfBusinessList.removeAll(listCompanyMergingLineOfBusiness);
        toRemove.addAll(lineOfBusinessList);
        toCommit.addAll(listCompanyMergingLineOfBusiness);
    }

    private List<LineOfBusinessCompaniesFlags> getLineOfBusinessCompaniesFlags(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(LineOfBusinessCompaniesFlags.class).setView("edit");
        loadContext.setQueryString("select e from crm$LineOfBusinessCompaniesFlags e where e.company.id in (:listId)").setParameter("listId", listId);
        List<LineOfBusinessCompaniesFlags> lineOfBusinessList = dataManager.loadList(loadContext);
        return lineOfBusinessList;
    }

    private List<LineOfBusinessCompaniesFlags> lineOfBusiness(List<LineOfBusinessCompaniesFlags> listCompanyMergingLineOfBusiness, LineOfBusinessCompaniesFlags lineOfBusiness)
    {
        boolean isFind = false;
        for (LineOfBusinessCompaniesFlags item:listCompanyMergingLineOfBusiness) {
            if (item.getLineOfBusiness().equals(lineOfBusiness.getLineOfBusiness()) && !isFind) {
                item.setWholesale(lineOfBusiness.getWholesale() != null ? lineOfBusiness.getWholesale() ? lineOfBusiness.getWholesale() : item.getWholesale() : item.getWholesale());
                item.setRetail(lineOfBusiness.getRetail() != null ? lineOfBusiness.getRetail() ? lineOfBusiness.getRetail() : item.getRetail() : item.getRetail());
                item.setInternetStore(lineOfBusiness.getInternetStore() != null ? lineOfBusiness.getInternetStore() ? lineOfBusiness.getInternetStore() : item.getInternetStore() : item.getInternetStore());
                item.setManufacturer(lineOfBusiness.getManufacturer() != null ? lineOfBusiness.getManufacturer() ? lineOfBusiness.getManufacturer() : item.getManufacturer() : item.getManufacturer());
                item.setDealer(lineOfBusiness.getDealer() != null ? lineOfBusiness.getDealer() ? lineOfBusiness.getDealer() : item.getDealer() : item.getDealer());
                item.setDistributor(lineOfBusiness.getDistributor() != null ? lineOfBusiness.getDistributor() ? lineOfBusiness.getDistributor() : item.getDistributor() : item.getDistributor());
                item.setChainStore(lineOfBusiness.getChainStore() != null ? lineOfBusiness.getChainStore() ? lineOfBusiness.getChainStore() : item.getChainStore() : item.getChainStore());
                item.setServices(lineOfBusiness.getServices() != null ? lineOfBusiness.getServices() ? lineOfBusiness.getServices() : item.getServices() : item.getServices());
                isFind = true;
            }
        }
        if(!isFind) {
            lineOfBusiness.setCompany(extCompanyMerging);
            listCompanyMergingLineOfBusiness.add(lineOfBusiness);
        }
        return listCompanyMergingLineOfBusiness;
    }

    private void mergingTask(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(ExtTask.class).setView("edit-crm");
        loadContext.setQueryString("select e from crm$Task e where e.extCompany.id in (:listId)").setParameter("listId", listId);
        List<ExtTask> taskList = dataManager.loadList(loadContext);
        if(taskList.size() > 0) {
            for (ExtTask task:taskList) {
                task.setExtCompany(extCompanyMerging);
                toCommit.add(task);
            }
        }
    }

    private void mergingActivity(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(BaseActivity.class).setView("browse");
        loadContext.setQueryString("select e from crm$BaseActivity e where e.company.id in (:listId)").setParameter("listId", listId);
        List<BaseActivity> activityList = dataManager.loadList(loadContext);
        if(activityList.size() > 0) {
            for (BaseActivity activity:activityList) {
                activity.setCompany(extCompanyMerging);
                toCommit.add(activity);
            }
        }
    }

    private void mergingActivityOld(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(Activity.class).setView("browse");
        loadContext.setQueryString("select e from crm$Activity e where e.company.id in (:listId)").setParameter("listId", listId);
        List<Activity> activityList = dataManager.loadList(loadContext);
        if(activityList.size() > 0) {
            for (Activity activity:activityList) {
                activity.setCompany(extCompanyMerging);
                toCommit.add(activity);
            }
        }
    }

    private void mergingCorrespondentAttachment(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(CorrespondentAttachment.class).setView("correspondent_attachment_edit");
        loadContext.setQueryString("select e from df$CorrespondentAttachment e where e.correspondent.id in (:listId)").setParameter("listId", listId);
        List<CorrespondentAttachment> correspondentAttachmentList = dataManager.loadList(loadContext);
        if(correspondentAttachmentList.size() > 0) {
            for (CorrespondentAttachment correspondentAttachment:correspondentAttachmentList) {
                correspondentAttachment.setCorrespondent(extCompanyMerging);
                toCommit.add(correspondentAttachment);
            }
        }
    }

    private void mergingDuplicateCompanyDetail(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(DuplicateCompanyDetail.class).setView("browse");
        loadContext.setQueryString("select e from crm$DuplicateCompanyDetail e where e.company.id in (:listId)").setParameter("listId", listId);
        List<DuplicateCompanyDetail> duplicateCompanyDetailList = dataManager.loadList(loadContext);
        if(duplicateCompanyDetailList.size() > 0) {
            toRemove.addAll(duplicateCompanyDetailList);
        }
    }

    private void updateDuplicateCompany(DuplicateStatus duplicateStatus) {
        if (this.duplicateCompany != null) {
            this.duplicateCompany.setCountDuplicate(mainDs.size());
            this.duplicateCompany.setStatus(duplicateStatus);
            dataManager.commit(this.duplicateCompany);
        }
    }

    public void nonDuplicateButton() {
        List<NonDuplicateCompanies> nonDuplicateCompaniesList = new ArrayList<>();
        for (ExtCompany company: mainDs.getItems()) {
            if(!company.equals(extCompanyMerging)) {
                if (!isNonDuplicate(company.getId())) {
                    NonDuplicateCompanies nonDuplicateCompanie = metadata.create(NonDuplicateCompanies.class);
                    nonDuplicateCompanie.setFirstCompany(extCompanyMerging.getId());
                    nonDuplicateCompanie.setSecondCompany(company.getId());
                    nonDuplicateCompaniesList.add(nonDuplicateCompanie);
                }
            }
        }
        dataManager.commit(new CommitContext(nonDuplicateCompaniesList));
        updateDuplicateCompany(DuplicateStatus.notduplicate);
        mainDs.refresh();
    }

    private String defaultString(Object obj) {
        return obj == null || StringUtils.EMPTY.equals(obj) ? StringUtils.EMPTY : obj.toString();
    }

    private void newToRemove() {
        toRemove.addAll(mainDs.getItems());
        toRemove.removeAll(notRemove);
    }

    private void initEntity() {
        for (ExtCompany company: mainDs.getItems()) {
            if (isTo1c(company.getId())) {
                if(extCompanyMerging == null) {
                    extCompanyMerging = company;
                }
            }
        }
        if(extCompanyMerging == null) {
            extCompanyMerging = mainDs.getItems().iterator().next();
        }
        loadInfoCompany(extCompanyMerging);
    }

    private void loadInfoCompany(ExtCompany entity) {
        companyType.setValue(entity.getCompanyType());
        legalForm.setValue(entity.getLegalForm());
        name.setValue(entity.getName());
        fullName.setValue(entity.getFullName());
        inn.setValue(entity.getInn());
        ogrn.setValue(entity.getOgrn());
        kpp.setValue(entity.getKpp());
        okpo.setValue(entity.getOkpo());
        extFactAddress.setValue(entity.getExtFactAddress());
        extLegalAddress.setValue(entity.getExtLegalAddress());
        extPostalAddress.setValue(entity.getExtPostalAddress());
        webAddress.setValue(entity.getWebAddress());
        parentCompany.setValue(entity.getParentCompany());
        companyScale.setValue(entity.getCompanyScale());
    }

    private void mergingPayOrd(List<UUID> listId) {
        LoadContext loadContext = new LoadContext(OrdDoc.class).setView("edit");
        loadContext.setQueryString("select e from crm$PayOrd e where e.company.id in (:listId)").setParameter("listId", listId);
        List<PayOrd> payOrdList = dataManager.loadList(loadContext);
        if(payOrdList.size() > 0) {
            for (PayOrd payOrd:payOrdList) {
                payOrd.setCompany(extCompanyMerging);
                toCommit.add(payOrd);
            }
        }
    }

}