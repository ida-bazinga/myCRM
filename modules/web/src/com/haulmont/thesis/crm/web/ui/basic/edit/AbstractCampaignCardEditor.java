/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.basic.edit;

import com.google.common.collect.Maps;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.ViewRepository;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.reports.entity.Report;
import com.haulmont.thesis.core.app.NumerationService;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.app.ThesisConstants;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Department;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.service.CampaignService;
import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.entity.CampaignKind;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.crm.gui.processaction.campaign.CampaignAccessData;
import com.haulmont.thesis.gui.components.ThesisTabSheet;
import com.haulmont.thesis.gui.processaction.DefaultCardAccessData;
import com.haulmont.thesis.web.actions.PrintReportAction;
import com.haulmont.thesis.web.app.ThesisPrintManager;
import com.haulmont.thesis.web.ui.basic.editor.AbstractCardEditor;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.Card;
import com.haulmont.workflow.core.entity.Proc;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AbstractCampaignCardEditor<T extends BaseCampaign> extends AbstractCardEditor<T> {

    protected boolean justCreated;
    protected DefaultEntityConfig defaultEntityConfig;
    protected Organization initialOrganization;
    protected Department initialDepartment;

    protected boolean printActionsAlreadyInited = false;

    @Named("organizationsDs")
    protected CollectionDatasource<Organization, UUID> organizationDs;

    @Named("print")
    protected PopupButton printButton;
    @Named("department")
    protected LookupField departmentField;
    @Named("runtimePropertiesFrameMain")
    protected RuntimePropertiesFrame runtimePropertiesFrameMain;
    @Named("runtimePropertiesFrameTab")
    protected RuntimePropertiesFrame runtimePropertiesFrameTab;

    @Inject
    protected ViewRepository viewRepository;
    @Inject
    protected CampaignService campaignService;
    @Inject
    protected OrgStructureService orgStructureService;
    @Inject
    protected NumerationService numerationService;
    @Inject
    protected ThesisPrintManager printManager;

    @Override
    protected DefaultCardAccessData createAccessData() {
        return AppBeans.getPrototype(CampaignAccessData.NAME, getContext().getParams());
    }

    protected CampaignAccessData getAccessData() {
        return (CampaignAccessData) accessData;
    }

    protected Container getStateHolder() {
        return getComponent("stateHolder");
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        justCreated = BooleanUtils.isTrue((Boolean) params.get("justCreated"));
        defaultEntityConfig = configuration.getConfigCached(DefaultEntityConfig.class);

        initDsListeners();
        initDepartmentFieldLookupAction();
    }

    protected void initDsListeners(){
    }

    protected void clearDepartment(Object value) {
        if (departmentField != null && departmentField.getValue() == null && value != null)
            departmentField.setValue(((ExtEmployee) value).getDepartment());
    }

    protected void initDepartmentFieldLookupAction() {
        if (departmentField instanceof LookupPickerField) {
            ((LookupPickerField) departmentField).addAction(new PickerField.LookupAction((PickerField) departmentField) {
                @Override
                public Map<String, Object> getLookupScreenParams() {
                    Map<String, Object> lookupScreenParams = super.getLookupScreenParams();
                    if (lookupScreenParams == null)
                        lookupScreenParams = Maps.newHashMap();

                    lookupScreenParams.put("organization", getItem().getOrganization());
                    lookupScreenParams.put("visibleOrganizationField", false);
                    return lookupScreenParams;
                }
            });
        }
    }

    @Override
    protected boolean isNewItem(T item) {
        return PersistenceHelper.isNew(item) || justCreated;
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        initOrganizationField(item);
        initDefaultActors();
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        setMultiUploadEnabled();
    }

    @Override
    protected void postInit() {
        super.postInit();
        final T campaign = getItem();

        initialOrganization = campaign.getOrganization();
        initialDepartment = campaign.getDepartment();

        disableAbilityStartProcess();

        initPrintActions(campaign.getKind());

        initCardRolesFrame(campaign);

        if (campaign.getCopyFrom() != null) {
            //noinspection unchecked
            initCategoryAttrsFromCard((T) campaign.getCopyFrom());
        }

        setEditorCaption(getItem());
        setNumber();
    }

    @Override
    protected Component createState() {
        Label label = componentsFactory.createComponent(Label.NAME);
        label.setValue(StringUtils.isEmpty(getItem().getState()) ? "" : getItem().getLocState());
        label.setStyleName("thesis-bold");
        return label;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);

        T campaign = getItem();
        Card parentCard = campaign.getParentCard();
        if (parentCard != null)
            if (cardService.isDescendant(campaign, parentCard, viewRepository.getView(Card.class, "_minimal"), "parentCard"))
                errors.add(getMessage("cycle.parentCard"));
    }

    @Override
    protected void applyAccessData() {
        if (runtimePropertiesFrameMain != null) {
            runtimePropertiesFrameMain.setCategoryFieldVisible(false);
            runtimePropertiesFrameMain.setCategoryFieldEditable(getAccessData().getGlobalEditable());
            if (getAccessData().getRuntimePropertiesInMainTab()) {
                runtimePropertiesFrameMain.setVisible(false);
            }
        }

        if (runtimePropertiesFrameTab != null) {
            runtimePropertiesFrameTab.setCategoryFieldVisible(false);
            runtimePropertiesFrameTab.setCategoryFieldEditable(getAccessData().getGlobalEditable());
            if (getAccessData().getRuntimePropertiesInSeparateTab()) {
                runtimePropertiesFrameTab.setVisible(false);
            }
        }

        TabSheet.Tab runtimePropertiesTab = tabsheet.getTab("runtimePropertiesTab");
        if (runtimePropertiesTab != null) {
            if (getAccessData().getRuntimePropertiesInSeparateTab()) {
                runtimePropertiesTab.setVisible(true);
                runtimePropertiesTab.setCaption(getItem().getKind().getAdditionalFieldsTabName());
            } else {
                runtimePropertiesTab.setVisible(false);
            }
        }
        super.applyAccessData();
    }

    @Override
    protected void fillHiddenTabs() {
        hiddenTabs.put("securityTab", messages.getMessage(getClass(), "securityTab.name"));
        hiddenTabs.put("processTab", messages.getMessage(getClass(), "processTab.name"));
        hiddenTabs.put("cardLogTab", messages.getMessage(getClass(), "cardLogTab.name"));
        hiddenTabs.put("openHistoryTab", messages.getMessage(getClass(), "openHistoryTab.name"));

        hiddenTabs = getTabsToHide(hiddenTabs);
    }

    @Override
    protected String getHiddenTabsConfig() {
        return "processTab,cardTreeTab,cardLogTab,openHistoryTab,securityTab";
    }

    @Override
    protected void createDsContextListener() {
        super.createDsContextListener();

        getDsContext().addListener(new DsContext.CommitListener() {
            @Override
            public void beforeCommit(CommitContext context) {
                //setNumberOnCommit();
            }

            @Override
            public void afterCommit(CommitContext context, Set<Entity> result) {
            }
        });
    }

    @Override
    protected boolean useSignature() {
        return false;
    }

    @Override
    protected void initHiddenTabs() {
        if (!hiddenTabs.isEmpty())
            return;

        fillHiddenTabs();
        adjustForKind();
        adjustForDefaultCampaign();

        ((ThesisTabSheet) tabsheet).setAdditionalInfoDescription(getMessage("additionalInfo"));
        ((ThesisTabSheet) tabsheet).setAdditionalInfoIcon("icons/plus-btn.png");
        cardTabSheetHelper.hideTabs(this, hiddenTabs, (ThesisTabSheet) tabsheet);
    }

    protected void adjustForKind() {
        for (FieldInfo fieldInfo : getItem().getKind().getFields()) {
            switch (fieldInfo.getName()){
                case "propertyContentTab":
                    if (!fieldInfo.getVisible()) {
                        removeTabIfExist(tabsheet, "contentTab");
                    }
                    break;
                case "propertyTargetsTab":
                    if (!fieldInfo.getVisible()) {
                        removeTabIfExist(tabsheet, "targetsTab");
                    }
                    break;
                case "propertyOperatorsTab":
                    if (!fieldInfo.getVisible()) {
                        removeTabIfExist(tabsheet, "operatorsTab");
                    }
                    break;
                case "propertyActivityResultTab":
                    if (!fieldInfo.getVisible()) {
                        removeTabIfExist(tabsheet, "activityResultTab");
                    }
                    break;
            }
        }
    }

    protected void adjustForDefaultCampaign(){}

    //setNumberOnCommit()
    protected void setNumber() {
        T campaign = getItem();
        if (isNewItem(campaign) && StringUtils.isBlank(campaign.getNumber())) {
            String num = numerationService.getNextNumber(campaign.getKind().getNumerator().getLocName().toString());
            if (num != null)
                campaign.setNumber(num);
        }
    }

    protected void initOrganizationField(T campaign) {
        if (campaign.getOrganization() == null) {
            Organization org = defaultEntityConfig.getOrganizationDefault();
            campaign.setOrganization(org != null ?
                    org : orgStructureService.getOrganizationByUser(userSession.getCurrentOrSubstitutedUser()));
        }
        if (campaign.getOrganization() != null) organizationDs.refresh();
    }

    protected void setEditorCaption(T campaign) {
        StringBuilder caption = new StringBuilder();

        if (isNewItem(campaign)) {
            caption.append(getMessage("Caption.campaignEdit"));
        } else {
            caption.append(formatTools.formatForScreenHistory(campaign));
        }
        getWindowManager().setWindowCaption(this, caption.toString(), null);
    }

    protected void initPrintActions(@Nonnull CampaignKind kind) {
        if (!printActionsAlreadyInited && kind.getReports() != null) {
            for (final Report report : kind.getReports()) {
                addPrintAction(report);
            }
            printActionsAlreadyInited = true;
        }
    }

    protected void addPrintAction(@Nonnull final Report report) {
        String id = WfUtils.Translit.toTranslit(report.getName().toLowerCase().trim()).replace(" ", "_");
        if (printButton.getAction(id) != null) return;

        Runnable print = new Runnable() {
            @Override
            public void run() {
                printManager.printReport(getItem(), report.getCode());
            }
        };
        printButton.addAction(new PrintReportAction(id, this, print) {
            @Override
            public String getCaption() {
                String printDocActionCaption = getPrintDocActionCaption(report);
                if (printDocActionCaption != null)
                    return printDocActionCaption;

                return super.getCaption();
            }
        });
    }

    protected String getPrintDocActionCaption(Report report) {
        if (report.getName().equals(messages.getMessage(this.getClass(), "printDocExecutionListReportName")))
            return messages.getMessage(this.getClass(), "printExecutionList");
        return StringUtils.defaultIfBlank(report.getLocName(), getItem().getKind().getName());
    }

    protected void disableAbilityStartProcess() {
        if (!isUserHasAbilityToStartProcess()) {
            setComponentVisibility(cardRolesFrame, "removeProc", false);
            setComponentVisibility(cardRolesFrame, "removeRole", false);
            setComponentVisibility(cardRolesFrame, "remove", false);
            cardRolesFrame.setEditable(false);

            if (cardRolesFrame != null) {
                Table rolesTable = cardRolesFrame.getComponentNN("rolesTable");
                Action removeAction = rolesTable.getAction("remove");
                if (removeAction != null) removeAction.setVisible(false);
            }
        }
    }

    //todo добавить свою роль
    protected boolean isUserHasAbilityToStartProcess() {
        return userSessionTools.isSessionUserInRole(userSession, ThesisConstants.SEC_ROLE_DOC_INITIATOR);
    }

    protected void initCardRolesFrame(T campaign){
        Proc process = campaign.getProc();
        boolean isActiveProc = (process != null);
        boolean isUserCanEditRoles = isUserCanEditCardRolesFrame(campaign, process);

        cardRolesFrame.setEditable(isActiveProc && isUserCanEditRoles);
        setComponentVisibility(cardRolesFrame, "moveDown", false);
        setComponentVisibility(cardRolesFrame, "moveUp", false);
        setComponentVisibility(cardRolesFrame, "removeRole", isActiveProc && isUserCanEditRoles);
        setComponentVisibility(cardRolesFrame, "remove", isActiveProc && isUserCanEditRoles);
    }

    protected boolean isUserCanEditCardRolesFrame(T campaign, Proc process) {
        return (userSessionTools.isCurrentUserAdministrator()
                && (",Improvement,".equals(campaign.getState()) && "EndorsementFull".equals(process.getJbpmProcessKey()))
        );
    }

    protected void setComponentVisibility(Container container, String componentName, boolean visibility){
        if (container != null){
            Component component = frame.getComponent(componentName);
            if (component != null) component.setVisible(visibility);
        }
    }

    @Override
    protected boolean getMainContainerEditable() {
        return getAccessData().getGlobalEditable();
    }
}
