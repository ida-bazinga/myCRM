package com.haulmont.thesis.crm.web.ui.account;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.core.entity.Contractor;
import com.haulmont.thesis.core.entity.ContractorAccount;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.ExtContractorAccount;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtAccountsFrame extends AbstractFrame {
    @Inject
    protected Table accountsTable;
    @Inject
    private DataManager dataManager;
    @Inject
    protected Metadata metadata;
    protected String propertyName;
    protected Datasource parentDs;



    protected ExtContractorAccount extContractorAccount;



    public void init() {
        accountsTable.addAction(CreateAction());
        accountsTable.addAction(EditAction());
        accountsTable.addAction(new RemoveAction(accountsTable, false));
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setParentDs(Datasource parentDs) {
        this.parentDs = parentDs;
    }

    protected Action EditAction()  {
        return new EditAction(accountsTable) {
            @Override
            public void actionPerform(Component component) {
                openWinForm(true);
            }
        };
    }

    protected Action CreateAction()  {
        return new CreateAction(accountsTable) {
            @Override
            public void actionPerform(Component component) {
                openWinForm(false);
            }
        };
    }

    protected void openWinForm(final boolean isEdit) {

        if (PersistenceHelper.isNew(parentDs.getItem())) {
            showNotification("Сохраните контрагента, перед добавлением счета!", NotificationType.HUMANIZED);
            return;
        }
        boolean isValid = isValid(isEdit);
        if (isValid) {
            final Window window = openEditor("crm$ExtContractorAccount.edit", extContractorAccount, WindowManager.OpenType.DIALOG);
            window.addListener(new Window.CloseListener() {
                @Override
                public void windowClosed(String actionId) {
                    getExtContractorAccount();
                }
            });
        }
    }

    protected boolean isValid(boolean isEdit) {
        extContractorAccount = metadata.create(ExtContractorAccount.class);
        extContractorAccount.setContractor((Contractor)parentDs.getItem());
        if(isEdit) {
            extContractorAccount = accountsTable.getSingleSelected();
            if (extContractorAccount == null) {
                return false;
            }
        }
        return true;
    }

    private boolean getExtContractorAccount() {
        ExtCompany extCompany = (ExtCompany) parentDs.getItem();
        UUID companyId = extCompany.getId();
        List<ContractorAccount> accounts = new ArrayList<>();
        LoadContext loadContext = new LoadContext(ExtContractorAccount.class).setView("contractor-edit");
        loadContext.setQueryString("select e from crm$ExtContractorAccount e where e.contractor.id=:companyId").setParameter("companyId", companyId);
        List<ExtContractorAccount> extContractorAccountList = dataManager.loadList(loadContext);
        if (extContractorAccountList.size() > 0) {
            for (ExtContractorAccount extContractorAccount:extContractorAccountList) {
                accounts.add(extContractorAccount);
            }
            if (extCompany.getAccounts()!= null) {
                extCompany.getAccounts().clear();
            }
            extCompany.setAccounts(accounts);
        }
        return true;
    }

}