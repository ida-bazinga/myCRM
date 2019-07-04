package com.haulmont.thesis.crm.web.company;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.web.ui.common.actions.CardLinkOpenAction;
import com.haulmont.thesis.web.ui.company.CompanyBrowser;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ExtCompanyBrowser extends CompanyBrowser {

    @Named("companiesTable")
    protected GroupTable companiesTable;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DataManager dataManager;
    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        companiesTable.addGeneratedColumn("webAddress", new Table.ColumnGenerator<ExtCompany>() {
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
        companiesTable.addAction(createAction());
        companiesTable.addAction(new CardLinkOpenAction(companiesDs));
        companiesTable.setMultiSelect(true);
    }

    public void addCallTargets() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedItems", companiesTable.getSelected());
        Window window = openWindow("crm$SelectCampaignFrame", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
            }
        });
    }

    public void addCallCampaignTargets() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedCompanies", companiesTable.getSelected());
        Window window = openWindow("crm$SelectCallCampaignWindow", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
            }
        });
    }

    public void addEmailTargets() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedCompanies", selectedCompanies()) ;
        Window window = openWindow("crm$SelectMailCampaignWindow", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
            }
        });
    }

    protected Set selectedCompanies() {
        if (companiesTable.getSelected().size() > 0) {
            return companiesTable.getSelected();
        }

        CollectionDatasource ds = new DsBuilder(getDsContext())
                .setJavaClass(ExtCompany.class)
                .setViewName(View.LOCAL)
                .setId("filterCompaniesDs")
                .buildCollectionDatasource();
        ds.setLoadDynamicAttributes(true);
        ds.setAllowCommit(false);
        ds.setQuery(companiesTable.getDatasource().getQuery(), companiesTable.getDatasource().getQueryFilter());
        ds.refresh();

        Set<ExtCompany> hashSet = new HashSet<>();
        hashSet.addAll(ds.getItems());

        return hashSet;
    }


    public void extCompanyFind() {
        Map<String, Object> params = new HashMap<>();
        //params.put("selectedItems", companiesTable.getSelected());
        Window window = openWindow("df$Company.find", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
            }
        });
    }

    private String validCompanyDuplicate() {
        return companiesTable.getSelected().size() < 2 ? "Кол-во выбранных записей, должно быть больше 1 записи!" :
                companiesTable.getSelected().size() > 100 ? "Кол-во выбранных записей, должно быть не больше 100 записей!" : "";
    }

    public void extCompanyMDup() {
        String txtMesage = validCompanyDuplicate();
        if (!StringUtils.isEmpty(txtMesage)) {
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("searchString", companiesTable.getSelected());
        Window window = openWindow("df$Company.mergingdup", WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                companiesDs.refresh();
            }
        });
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

    private void createDuplicateCompanyAndDuplicateCompanyDetail() {
        Set<Entity> toCommit = new HashSet<>();
        Set<ExtCompany> companyList = companiesTable.getSelected();
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

    public void companyVerifiedState() {
        if (companiesTable.getSelected().size() < 1) {
            String txtMesage = "Выберите контрагента для актулизации!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }

        createCompanyVerifiedState();
        showNotification(getMessage("Выбранные Контрагенты добавлены в список актулизации."), NotificationType.HUMANIZED);
    }

    private void createCompanyVerifiedState() {
        Set<Entity> toCommit = new HashSet<>();
        String userName = userSession.getCurrentOrSubstitutedUser().getName();
        Set<ExtCompany> companyList = companiesTable.getSelected();
        for (ExtCompany company:companyList) {
            CompanyVerifiedState companyVerifiedState = metadata.create(CompanyVerifiedState.class);
            companyVerifiedState.setCorrespondent(company);
            companyVerifiedState.setByUser(userName);
            companyVerifiedState.setState(VerifiedStateEnum.NOTVERIFIED);
            toCommit.add(companyVerifiedState);
        }
        dataManager.commit(new CommitContext(toCommit));
    }

    protected Action createAction()  {
        return new CreateAction(companiesTable) {
            @Override
            public void actionPerform(Component component) {
                extCompanyFind();
            }
        };
    }

    public void createCompanyOld() {
        ExtCompany newExtCompany = metadata.create(ExtCompany.class);
        final Window editor = openEditor("df$Company.edit", newExtCompany ,WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                companiesDs.refresh();
            }
        });
    }


}