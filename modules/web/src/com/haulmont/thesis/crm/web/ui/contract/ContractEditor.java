/*
 * Copyright (c) 2010 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Konstantin Krivopustov
 * Created: 22.03.2010 15:44:30
 *
 * $Id: ContractEditor.java 21157 2015-08-10 11:25:39Z timokhov $
 */
package com.haulmont.thesis.crm.web.ui.contract;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.reports.entity.Report;
import com.haulmont.thesis.core.config.AppIntegrationConfig;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.*;
import com.haulmont.thesis.core.enums.OfficeFileState;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.OrdDoc;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesExpenseBillFromContractAction;
import com.haulmont.thesis.crm.web.ui.common.actions.CreateSalesOrdFromContractAction;
import com.haulmont.thesis.web.actions.LookupActionEx;
import com.haulmont.thesis.web.ui.simpledoc.SimpleDocEditor;
import com.haulmont.thesis.web.ui.tools.AppIntegrationTools;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContractEditor<T extends Contract> extends SimpleDocEditor<T> {
    protected Label integrationStateSeparator;
    protected Label integrationStateLabel;

    @Inject
    protected AppIntegrationTools integrationTools;
    @Inject
    protected Button enqueueToExportBtn;
    @Inject
    protected DateField liabilityStart;
    @Inject
    protected DateField liabilityEnd;
    @Inject
    protected LookupPickerField organization;
    @Inject
    private DataManager dataManager;
    @Named("contractor")
    protected PickerField contractorField;
    @Named("docNumber")
    protected TextField docNumber;
    @Named("createSubCardButton")
    protected PopupButton createSubCardButton;

    @Inject
    protected TextArea comment;
    @Inject
    protected CrmConfig crmConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initContractorField();

        createVatCalculationListener();
    }

    @Override
    protected void postInit() {
        super.postInit();
        boolean isDoc = isOrdDoc();
        contractorField.setEditable(!isDoc);
        boolean isAdmin = userSessionTools.isCurrentUserAdministrator();
        docNumber.setEditable(isAdmin || !isDoc);
    }

    @Override
    protected void applyAccessData() {
        super.applyAccessData();

        getComponentNN("header.integrationStateBox").setVisible(getAccessData().getIntegrationStateVisible());
    }

    protected void createVatCalculationListener() {
        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                if ("amount".equals(property) || "vatRate".equals(property) || "vatInclusive".equals(property)) {
                    Contract contract = getItem();
                    BigDecimal vatAmount = null;
                    BigDecimal vatRate = contract.getVatRate();
                    if (vatRate != null && vatRate.compareTo(BigDecimal.valueOf(100)) == 1) {
                        vatRate = BigDecimal.valueOf(100);
                        contract.setVatRate(vatRate);
                    }
                    BigDecimal amount = contract.getAmount();
                    if (amount != null && vatRate != null) {
                        vatAmount = amount.multiply(vatRate).divide(BigDecimal.valueOf(100).add
                                (BooleanUtils.isTrue(contract.getVatInclusive()) ? vatRate : BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_DOWN);
                    }
                    contract.setVatAmount(vatAmount);
                } else if ("contractor".equals(property)) {
                    getItem().setContactPerson(null);
                }
            }
        });
    }

    @Override
    protected void addPrintDocActions() {
        super.addPrintDocActions();
        if (getItem().getDocKind().getReports().isEmpty()) {
            Report report = printManager.loadReport(AppContext.getProperty("thesis.printContractReportName.defaultReportName"));
            if (report != null) {
                addPrintDocAction(report);
            }
        }
    }

    private void initContractorField() {
        //LookupPickerField contractorField = getComponentNN("contractor");
        //PickerField contractorField = getComponentNN("contractor");
        //contractorField.getOptionsDatasource().refresh();
        contractorField.removeAllActions();
       // ContractorLookupAction contractorLookupAction = new ContractorLookupAction(contractorField);
        PickerField.LookupAction contractorLookupAction = new PickerField.LookupAction(contractorField);
        contractorLookupAction.setLookupScreen("df$Correspondent.lookup");

        HashMap<String, Object> contractorFieldLookupParams = new HashMap<>();
        contractorFieldLookupParams.put("visibleTabs", "companiesTab, individualsTab");
        contractorFieldLookupParams.put("activeTab", "companiesTab");
        contractorFieldLookupParams.put("caption", "contractorSelect");
        contractorFieldLookupParams.put("importFromCsvVisible", false);
        contractorLookupAction.setLookupScreenParams(contractorFieldLookupParams);

        contractorField.addAction(contractorLookupAction);
        contractorField.addOpenAction();
    }

    @Override
    protected String getHiddenTabsConfig() {
        return StringUtils.defaultString(AppContext.getProperty("docflow.invisibleContractTab"));
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);

        if (item.getCurrency() == null) {
            Currency currency = getDefaultCurrency();
            item.setCurrency(currency);
        }
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        initAppIntegrationComponents();
        createSubCardButton.addAction(new CreateSalesOrdFromContractAction(this));
        createSubCardButton.addAction(new CreateSalesExpenseBillFromContractAction(this));
        createSubCardButton.setVisible(true);
        if (comment != null && !comment.isEditable()) {
            setFocusComponent(null);
        }
    }

    protected Currency getDefaultCurrency() {
        return configuration.getConfigCached(DefaultEntityConfig.class).getCurrencyDefault();
    }

    public class ContractorLookupAction extends LookupActionEx {

        public ContractorLookupAction(PickerField pickerField) {
            super(pickerField);
        }

        @Override
        public void actionPerform(Component component) {
            lookupScreenParams.put("correspondent", pickerField.getValue());
            super.actionPerform(component);
        }
    }

    protected void initAppIntegrationComponents() {
        integrationStateSeparator = getComponent("header.integrationStateLabelSeparator");
        integrationStateLabel = getComponent("header.integrationStateLabel");
        Doc doc = getItem();
        if (!doc.getTemplate() && doc.getVersionOf() == null) {
            integrationTools.initAppIntegrationComponents(this, integrationStateLabel, this.<Button>getComponent("enqueueToExportBtn"));
            OfficeFileState officeFileState = getOfficeFileState();
            if (officeFileState != null && officeFileState.getId() >= OfficeFileState.ARCHIVED.getId()) {
                enqueueToExportBtn.setVisible(false);
            }
        } else {
            enqueueToExportBtn.setVisible(false);
        }
        if (integrationStateSeparator != null) {
            integrationStateSeparator.setVisible(integrationStateLabel.isVisible());
        }

        AppIntegrationConfig appIntegrationConfig = configuration.getConfigCached(AppIntegrationConfig.class);
        if (appIntegrationConfig.getEnabled()) {
            if (organization != null) {
                organization.setRequired(true);
                organization.setRequiredMessage(messages.getMessage(ContractEditor.class, "organizationRequired"));
            }
            if (contractorField != null) {
                contractorField.setRequired(true);
                contractorField.setRequiredMessage(messages.getMessage(ContractEditor.class, "contractorRequired"));
            }
        }
    }

    public void enqueueToExport() {
        StringBuilder builder = new StringBuilder();
        Contractor contractor = getItem().getContractor();
        if (contractor == null) {
            builder.append(getMessage("contractorValidError.msg"));
        }

        if (builder.length() > 0) {
            showNotification(builder.toString().trim(), NotificationType.WARNING);
        } else {
            integrationTools.enqueueToExport(this, integrationStateLabel);
        }

        if (integrationStateSeparator != null)
            integrationStateSeparator.setVisible(integrationStateLabel.isVisible());
    }

    protected boolean checkLiabilityDates() {
        if (liabilityStart.getValue() != null && liabilityEnd.getValue() != null) {
            Date start = liabilityStart.getValue();
            Date end = liabilityEnd.getValue();
            if (end.before(start)) {
                showNotification(getMessage("liabilityDatesErrorMsg"), NotificationType.WARNING);
                return false;
            } else return true;
        } else return true;
    }

    @Override
    public boolean commit() {
        return checkLiabilityDates() && super.commit();
    }

    private boolean isOrdDoc() {
        Contract item = getItem();
        if (isOrganization()) {
            Contract offer = crmConfig.getContractOfferEfi();
            if (offer.getId().equals(item.getId())) {
                return true;
            }
            LoadContext loadContext = new LoadContext(OrdDoc.class).setView("edit");
            loadContext.setQueryString("select e from crm$OrdDoc e join e.integrationResolver i where i.extId is not null and e.contract.id = :docId")
                    .setParameter("docId", item.getId());

            if (dataManager.loadList(loadContext).size()>0) {
                return true;
            }
        }
        return false;
    }

    private boolean isOrganization() {
        Contract item = getItem();
        Organization org = configuration.getConfigCached(DefaultEntityConfig.class).getOrganizationDefault();
        boolean isOrg = item.getOrganization() != null ? item.getOrganization().equals(org) ? true : false : false;
        return isOrg;
    }

}