package com.haulmont.thesis.crm.web.company;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.crm.core.app.CreateCompanyAndAddressDaData;
import com.haulmont.thesis.crm.core.app.ValidINN;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ExtCompanyFind<T extends ExtCompany> extends AbstractWindow {

    @Named("mainDs")
    protected CollectionDatasource<ExtCompany, UUID> mainDs;
    @Inject
    protected TextField searchInn, searchName, searchPhone, searchMail, searchWeb; //searchAddress;
    @Inject
    protected CreateCompanyAndAddressDaData createCompanyAndAddressDaData;
    @Inject
    private DataManager dataManager;
    @Inject
    private Label lbInfo;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Table findTable;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected ValidINN validINN;

    protected ExtCompany newExtCompany;
    protected Boolean isSearchInn;


    @Override
    public void init(final Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidth(510).setHeight(610).setResizable(true);
    }

    private void setNewExtCompany_CompanyTypeEnum()
    {
        if (newExtCompany.getName() == null && searchName.getValue() != null) {
            newExtCompany.setName(searchName.getValue().toString());
        }
        if (newExtCompany.getFullName() == null && searchName.getValue() != null) {
            newExtCompany.setFullName(searchName.getValue().toString());
        }
        if (newExtCompany.getInn() == null && searchInn.getValue() != null) {
            newExtCompany.setInn(searchInn.getValue().toString());
        }
        if(newExtCompany.getWebAddress() == null && searchWeb.getValue() != null) {
            String web = searchWeb.getValue().toString();
            if (!web.contains("http://") || !web.contains("https://")) {
                newExtCompany.setWebAddress(String.format("http://%s",web));
            }
        }
        if (newExtCompany.getContactPersons() != null) {
            for (ContactPerson cp:newExtCompany.getContactPersons()) {
                cp.setCompany(newExtCompany);
            }
        }
        if(searchPhone.getValue() != null || searchMail.getValue() != null) {
            if (newExtCompany.getContactPersons() != null) {
                newExtCompany.getContactPersons().clear();
            }
            List<ContactPerson> contactPersonList = new ArrayList<>();
            List<Communication> communicationList = new ArrayList<>();
            if (newExtCompany.getContactPersons() != null) {
                contactPersonList = newExtCompany.getContactPersons();
            }
            String managementName = "Контактное лицо";
            ExtContactPerson contactPerson = createContactPerson(managementName);
            contactPersonList.add(contactPerson);
            if (searchPhone.getValue() != null) {
                communicationList.add(createCommunication(contactPerson, searchPhone.getValue().toString(), true));
            }
            if (searchMail.getValue() != null) {
                communicationList.add(createCommunication(contactPerson, searchMail.getValue().toString(), false));
            }
            contactPerson.setCommunications(communicationList);
            newExtCompany.setContactPersons(contactPersonList);
        }
    }

    private ExtContactPerson createContactPerson(String name)
    {
        ExtContactPerson contactPerson = metadata.create(ExtContactPerson.class);
        contactPerson.setLastName(name);
        contactPerson.setFullName(name);
        contactPerson.setName(name);
        contactPerson.setCompany(newExtCompany);
        return contactPerson;
    }

    private Communication createCommunication(ExtContactPerson contactPerson, String mainPart, boolean isPhone) {
        Communication communication = metadata.create(Communication.class);
        communication.setAddress(mainPart);
        communication.setMainPart(mainPart);
        communication.setContactPerson(contactPerson);
        String code = isPhone ? "work" : "INTERNET";
        CommKind commKind = getCommKind(code);
        communication.setCommKind(commKind);
        communication.setPref(true);
        return communication;
    }

    private CommKind getCommKind(String code) {
        LoadContext loadContext = new LoadContext(CommKind.class).setView("_local");
        loadContext.setQueryString("select e from crm$CommKind e where e.code=:code").setParameter("code", code);
        return dataManager.load(loadContext);
    }

    @Override
    public void ready() {
        final Button searchSimple = getComponentNN("searchSimple");
        searchSimple.setAction(new AbstractAction("find") {
            public void actionPerform(Component component) {

                if (!validRequired()) {
                    String txtMesage = "Не заданы дополнительные параметры поиска!";
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                    return;
                }
                isSearchInn = false;
                resultSearch();
            }

            @Override
            public String getCaption() {
                return getMessage("Поиск по дополнительным параметрам");
            }
        });

        final Button searchSimpleInn = getComponentNN("searchSimpleInn");
        searchSimpleInn.setAction(new AbstractAction("find") {
            public void actionPerform(Component component) {
                if (searchInn.getValue() != null) {
                    if (!validINN.isValidINN(searchInn.getValue().toString())) {
                        String txtMesage = "Неправильное контрольное число ИНН!";
                        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                        return;
                    }
                    isSearchInn = true;
                    resultSearch();
                }
                else {
                    String txtMesage = "Не заполнен ИНН!";
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                    return;
                }
            }
            @Override
            public String getCaption() {
                return getMessage("Поиск по ИНН");
            }
        });

        initVisibleComponent();
    }

    private Boolean validRequired () {
        newExtCompany = metadata.create(ExtCompany.class);
        Boolean result = false;
        //searchInn.getValue() != null || searchAddress.getValue() != null ||
        if (searchName.getValue() != null ||
            searchPhone.getValue() != null || searchMail.getValue() != null || searchWeb.getValue() != null)
        {
            result = true;
            /*
            if(searchAddress.getValue() != null) {
                standartAddress();
            }
            */
            if(searchInn.getValue() != null) {
                isSearchInn = true;
            }
        }
        return result;
    }

    private void resultSearch() {
        if(searchAndLoad()) {
            lbInfo.setValue("Подозрение на дубли: Да");
        }
        else {
            if (searchInn.getValue() != null) {
                lbInfo.setValue("Дубли не найдены, запущен поиск по ДаДате!");
                newExtCompany = createCompanyAndAddressDaData.getCompany(searchInn.getValue().toString());
                if (newExtCompany != null) {
                    searchName.setValue(newExtCompany.getName());
                    //searchAddress.setValue(newExtCompany.getExtLegalAddress().getName_ru());
                    isSearchInn = false;
                    if (!searchAndLoad()) {
                        lbInfo.setValue("По ИНН записи не найдены. Произведен поиск с использованием ДаДата. Подозрение на дубли: Нет");
                    }
                    else {
                        lbInfo.setValue("По ИНН записи не найдены. Произведен поиск с использованием ДаДата. Подозрение на дубли: Да");
                    }
                }
                else {
                    lbInfo.setValue("Данные в ДаДате по введеному ИИН не найдены");
                }
            }
            else {
                lbInfo.setValue("Подозрение на дубли: Нет");
            }
        }
    }

    private Boolean searchAndLoad() {
        Boolean result = false;
        List<ExtCompany> extCompanyList = getExtCompany();
        mainDs.clear();
        if(extCompanyList.size() > 0) {
            for (ExtCompany ec:extCompanyList) {
                mainDs.addItem(ec);
            }
            result = true;
        }
        generatedColumnWebAddress();
        return result;
    }

    /*
    private void standartAddress() {
        if (searchInn.getValue() != null) {
            newExtCompany = createCompanyAndAddressDaData.getCompany(searchInn.getValue().toString());
        } else if(searchAddress.getValue() != null) {
            Address address = createCompanyAndAddressDaData.getAddress(searchAddress.getValue().toString());
            if(address != null) {
                newExtCompany.setExtFactAddress(address);
                newExtCompany.setExtLegalAddress(address);
            }
        }
        if (newExtCompany.getExtLegalAddress() != null) {
            searchAddress.setValue(newExtCompany.getExtLegalAddress().getName_ru());
        }
    }
    */

    private void initVisibleComponent() {
        lbInfo.setValue("Информационная строка...");
        isSearchInn = false;
    }

    private List<ExtCompany> getExtCompany() {
        LoadContext loadContext = new LoadContext(ExtCompany.class).setView("browse");
        loadContext.setQueryString(bildCompanyQueryString()).setMaxResults(100);
        return dataManager.loadList(loadContext);
    }


    private String bildCompanyQueryString () {
        StringBuffer companyQuery = new StringBuffer();
        companyQuery.append("select e from crm$Company e where e.txtsearch like '");
        if (isSearchInn) {
            if (!companyQuery.toString().endsWith("%")) {
                companyQuery.append("%");
            }
            companyQuery.append(searchInn.getValue().toString());
        }
        else {

            if (searchName.getValue() != null) {
                if (!companyQuery.toString().endsWith("%")) {
                    companyQuery.append("%");
                }
                String searchNameStr = searchName.getValue().toString();
                searchName.setValue(searchNameStr.replaceAll("[^\\sа-яА-ЯёЁa-zA-Z0-9]", ""));
                companyQuery.append(searchName.getValue().toString());
            }
            if (searchPhone.getValue() != null) {
                if (!companyQuery.toString().endsWith("%")) {
                    companyQuery.append("%");
                }
                String phone = searchPhone.getValue().toString().replaceAll("[^0-9]", "");
                companyQuery.append(phone);
            }
            if (searchMail.getValue() != null) {
                if (!companyQuery.toString().endsWith("%")) {
                    companyQuery.append("%");
                }
                companyQuery.append(searchMail.getValue().toString());
            }
            if (searchWeb.getValue() != null) {
                if (!companyQuery.toString().endsWith("%")) {
                    companyQuery.append("%");
                }
                companyQuery.append(searchWeb.getValue().toString());
            }
            /*
            if (searchAddress.getValue() != null) {
                if (searchName.getValue() != null || searchPhone.getValue() != null || searchMail.getValue() != null || searchWeb.getValue() != null) {
                    companyQuery.append(" or");
                }
                String s = String.format(txtsearch, searchAddress.getValue().toString());
                companyQuery.append(s);
            }
            */
        }

        companyQuery.append("%'");
        return companyQuery.toString();
    }

    private void generatedColumnWebAddress() {
        findTable.addGeneratedColumn("webAddress", new Table.ColumnGenerator<ExtCompany>() {
            @Override
            public Component generateCell(ExtCompany entity) {
                String value = (entity.getWebAddress() != null) ? entity.getWebAddress() : "";
                if (value.startsWith("http://") || value.startsWith("https://")) {
                    Link field = componentsFactory.createComponent(Link.NAME);
                    field.setTarget("_blank");
                    field.setCaption(value);
                    field.setUrl(value);
                    return field;
                } else {
                    return new Table.PlainTextCell(getMessage(value));
                }
            }
        });
        findTable.setIconProvider(getIconProvider());
    }

    private Table.IconProvider<ExtCompany> getIconProvider(){
        return new Table.IconProvider<ExtCompany>() {
            @Nullable
            @Override
            public String getItemIcon(ExtCompany entity) {
                if (isTo1c(entity.getId())) {
                    return "theme:icons/ok.png";
                }
                return "";
            }
        };
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

    private void closeMyWindow(String actionId) {
        if ("close".equals(actionId)) {
            //selectDs.clear();
        }
        //selectDs.setAllowCommit(false);
        mainDs.setAllowCommit(false);
    }

    @Override
    public boolean close(String actionId) {
        closeMyWindow("close");
        return ((Window) frame).close(actionId);
    }

    @Override
    public void closeAndRun(String actionId, Runnable runnable) {
        closeMyWindow("close");
        ((Window) frame).closeAndRun(actionId, runnable);
    }

    public void createButton() {
        //closeMyWindow("select");
        //((Window) frame).close(Window.CLOSE_ACTION_ID);
        if (newExtCompany == null) {
            String txtMesage = isSearchInn ? "Проверьте контрагента по дополнительным реквизитам, нет ли его в системе!" : "Проверьте контрагента, нет ли его в системе!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        setNewExtCompany_CompanyTypeEnum();
        final Window editor = openEditor("df$Company.edit", newExtCompany, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                newExtCompany = null;
                isSearchInn = false;
            }
        });
    }

    public void closeButton() {
        closeMyWindow("close");
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }

    public void duplicateCompany() {
        String txtMesage = validCompanyDuplicate();
        if (!StringUtils.isEmpty(txtMesage)) {
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        createDuplicateCompanyAndDuplicateCompanyDetail();
        showNotification(getMessage("Выбранные Контрагенты добавлены в список дублей."), NotificationType.HUMANIZED);
    }

    private String validCompanyDuplicate() {
        if(findTable.getSelected().size() == 0) {
            return "Не выбраны контрагенты!";
        }
        if(findTable.getSelected().size() < 2) {
            return "Кол-во выбранных записей, должно быть больше 1 записи!";
        }
        if(findTable.getSelected().size() > 100) {
            return  "Кол-во выбранных записей, должно быть не больше 100 записей!";
        }
        return "";
    }

    private void createDuplicateCompanyAndDuplicateCompanyDetail() {
        Set<Entity> toCommit = new HashSet<>();
        Set<ExtCompany> companyList = findTable.getSelected();
        DuplicateCompany duplicateCompany = metadata.create(DuplicateCompany.class);
        duplicateCompany.setName(companyList.iterator().next().getName().replaceAll("[^sa-zа-яA-ZА-Я0-9]", ""));
        duplicateCompany.setCountDuplicate(companyList.size());
        duplicateCompany.setStatus(DuplicateStatus.original);
        toCommit.add(duplicateCompany);
        for (ExtCompany company:companyList) {
            DuplicateCompanyDetail duplicateCompanyDetail = metadata.create(DuplicateCompanyDetail.class);
            duplicateCompanyDetail.setCompany(company);
            duplicateCompanyDetail.setDuplicateCompany(duplicateCompany);
            toCommit.add(duplicateCompanyDetail);
        }
        dataManager.commit(new CommitContext(toCommit));
    }
}