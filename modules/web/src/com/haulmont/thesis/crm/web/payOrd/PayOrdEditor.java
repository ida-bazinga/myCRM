package com.haulmont.thesis.crm.web.payOrd;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.cuba.gui.components.SearchPickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.ContractorAccount;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.web.ui.basic.editor.AbstractCardEditor;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayOrdEditor<T extends PayOrd> extends AbstractCardEditor<T> {

    @Inject
    protected CollectionDatasource<InvDocBugetDetail, UUID> invDocBugetDetailsDs;
    @Inject
    protected CollectionDatasource<ContractorAccount, UUID> companyAccountDs;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    private Work1C work1C;
    @Inject
    protected CrmConfig crmConfig;
    @Named("print")
    protected PopupButton printButton;
    @Named("paymentBudgetOrganization")
    protected BoxLayout paymentBudgetOrganization;
    @Named("currency")
    protected LookupField currency;
    @Named("transactionType")
    protected LookupField transactionType;
    @Named("company")
    protected SearchPickerField company;



    private int priority;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1; //приоритет отправки в 1С
        transactionType.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                initTransactionType(value.toString());
            }
        });
        if (!PersistenceHelper.isNew(getItem()) && getItem().getTypeTransaction() != null) {
            initTransactionType(getItem().getTypeTransaction().getId());
        }
        company.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                getItem().setCompanyAccount(null);
                if (companyAccountDs.size() == 1) {
                    for (ContractorAccount contractorAccount:companyAccountDs.getItems()) {
                        getItem().setCompanyAccount((ExtContractorAccount) contractorAccount);
                    }
                }
            }
        });
    }

    protected void initTransactionType(String type) {
        currency.setEditable(true);
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

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        createPrintActions();
    }

    //пометить к обмену
    public void exportButton() {
        PayOrd item = getItem();
        Currency currencyRub = configuration.getConfigCached(CrmConfig.class).getCurrencyRub();
        Organization org = configuration.getConfigCached(DefaultEntityConfig.class).getOrganizationDefault();

        if (!item.getOrganization().equals(org)) {
            showNotification(getMessage("Выбранная Организация не соответствует требованиям. \n Платежное поручение НЕ ОТПРАВЛЕНО В 1С!"), NotificationType.HUMANIZED);
            return;
        }

        if (!item.getCurrency().equals(currencyRub)) {
            showNotification(getMessage("Выбранная Валюта не соответствует требованиям. \n Платежное поручение НЕ ОТПРАВЛЕНО В 1С!"), NotificationType.HUMANIZED);
            return;
        }

        if (!TypeTransactionDebiting.PAYSUPPLINER.equals(item.getTypeTransaction())) {
            showNotification(getMessage("Выбранный Вид операции не соответствует требованиям. \n Платежное поручение НЕ ОТПРАВЛЕНО В 1С!"), NotificationType.HUMANIZED);
            return;
        }

        work1C.setExtSystem(ExtSystem.BP1CEFI);
        if (!work1C.validate(item.getIntegrationResolver())) {
            item.setIntegrationResolver(work1C.setIntegrationResolver(item.getId(), item.getMetaClass().getName(), StatusDocSales.MARKER1C, null));
            super.commit();
        }
        boolean isSum = isSum();
        if (!isSum) {
            showNotification(getMessage("Не совпадают сумма платежного поручения и суммы по бюджетам. \n Платежное поручение НЕ ОТПРАВЛЕНО В 1С!"), NotificationType.HUMANIZED);

        } else {
            boolean isValidateAll = super.validateAll();
            if (isValidateAll) {
                Map<String, Object> params = new HashMap<>();
                params.put(item.getCompany().getMetaClass().getName(), item.getCompany().getId());
                params.put(item.getMetaClass().getName(), item.getId());

                Boolean result = work1C.regLog(params, priority);
                if (result) {
                    String txtMesage = "Платежное поручение отправлено в 1С";
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                }
            }
        }
    }

    @Override
    protected void setMultiUploadEnabled() {
        multiUploadField.setVisible(false);
    }

    public void budgetExpensesTableCreate() {
        PayOrd item = getItem();
        if (item.getCurrency() != null && item.getParentCard() != null) {
            InvDocBugetDetail invDocBugetDetail = metadata.create(InvDocBugetDetail.class);
            invDocBugetDetail.setInvDoc((InvDoc)item.getParentCard());
            invDocBugetDetail.setCurrency(item.getCurrency());
            invDocBugetDetail.setFullSum(BigDecimal.ZERO);
            invDocBugetDetailsDs.addItem(invDocBugetDetail);
        }

    }

    @Override
    protected boolean preCommit() {
        boolean isSum = isSum();
        if (!isSum) {
            showNotification(getMessage("Не совпадают сумма платежного поручения и суммы по бюджетам. \n Платежное поручение НЕ СОХРАНЕНО!"), NotificationType.HUMANIZED);
            return false;
        }
        return true;
    }

    private boolean isSum() {
        PayOrd item = getItem();
        if (item.getFullSum() != null && invDocBugetDetailsDs.getItems() != null && invDocBugetDetailsDs.getItems().size()>0) {
            BigDecimal totalDetailsSum = BigDecimal.valueOf(0);
            for (InvDocBugetDetail bugetDetail : invDocBugetDetailsDs.getItems()) {
                if (bugetDetail.getFullSum()!= null) {
                    totalDetailsSum = totalDetailsSum.add(bugetDetail.getFullSum());
                }
            }
            if (!totalDetailsSum.stripTrailingZeros().equals(item.getFullSum().stripTrailingZeros())) {
                return false;
            }
        }
        return true;
    }

    protected void createPrintActions() {
        if (printButton != null) {
            printButton.setVisible(CollectionUtils.isNotEmpty(printButton.getActions()));
        }
    }



}