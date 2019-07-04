package com.haulmont.thesis.crm.web.ui.campaign;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.core.entity.Company;
import com.haulmont.thesis.crm.entity.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

public class SelectCallCampaignWindow extends AbstractWindow {

    Set<ExtCompany> companies;

    protected CallCampaign campaign;

    protected LookupField campaignField, roleField;
    protected GroupTable targetsTable;
    protected Label sizeLabel, statusLabel;
    protected Button windowCommitBtn;

    protected GroupDatasource<CampaignTarget, UUID> campaignTargetsDs;
    protected CollectionDatasource<CallCampaignTrgt, UUID> targetsDs;

    @Inject
    protected Metadata metadata;

    @SuppressWarnings("unchecked")
    @Override
    public void init(final Map<String, Object> params) {
        super.init(params);

        companies = (Set<ExtCompany>) params.get("selectedCompanies");
        //persons = (Set<ExtContactPerson>) params.get("selectedContacts");
        //communications = (Set<Communications>) params.get("selectedComm");

        findStandardComponents();

        initListeners();
        initCampaignTargetsDs(null);

        initContactsTableColumns();
        setRowIcon();
        addWindowAction();

        getDialogParams().setWidth(800).setHeight(600).setResizable(true);
    }

    protected void findStandardComponents(){
        campaignTargetsDs = getDsContext().getNN("campaignTargetsDs");
        targetsDs = getDsContext().getNN("targetsDs");

        targetsTable = getComponentNN("targetsTable");
        campaignField = getComponentNN("campaign");
        sizeLabel = getComponentNN("sizeLabel");
        statusLabel = getComponentNN("statusLabel");
        roleField = getComponentNN("role");
        windowCommitBtn = getComponentNN(Editor.WINDOW_COMMIT);
    }

    protected void initCampaignTargetsDs(ContactRole role) {
        Map<String, Object> params = new HashMap<>();
        params.put("companies", companies);
        params.put("type", CommunicationTypeEnum.phone);
        params.put("role", role);

        campaignTargetsDs.refresh(params);
    }

    protected void initListeners(){
        roleField.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                initCampaignTargetsDs((ContactRole) value);
            }
        });

        campaignField.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (value!=null){
                    windowCommitBtn.setEnabled(true);
                }
                else{
                    windowCommitBtn.setEnabled(false);
                }
                campaign = (CallCampaign) value;
                checkNewTargets();
            }
        });
    }

    //todo проверка актуальности телефонов у контакта и таргета с пометкой
    protected void checkNewTargets(){
        int successCount = 0;
        int doublesCount = 0;
        int notFoundCommCount = 0;

        targetsDs.refresh();

        if (!campaignTargetsDs.getItems().isEmpty()){
            Set<String> tc = new HashSet<>();

            if (!targetsDs.getItems().isEmpty()) {
                for (CallCampaignTrgt item : targetsDs.getItems()) {
                    if(!item.getIsGroup())
                        tc.add(item.getName());
                }
            }

            for (CampaignTarget item : campaignTargetsDs.getItems()){
                if (item.getDescription().equals("targetOk") || item.getDescription().equals("double")){
                    if(!tc.isEmpty() && tc.contains(item.getCommunication().getInstanceName())){
                        doublesCount++;
                        item.setDescription("double");
                    }else{
                        item.setDescription("targetOk");
                        successCount++;
                    }
                }

                if (item.getDescription().equals("noCommunications")|| item.getDescription().equals("noContacts")) {
                    notFoundCommCount++;
                }
            }
        }

        targetsTable.repaint();
        statusLabel.setValue(messages.formatMessage(getClass(), "label.status", successCount,  doublesCount, notFoundCommCount));
    }

    @Override
    public void ready() {
        sizeLabel.setValue(messages.formatMessage(getClass(), "label.selectedCount", companies.size(), campaignTargetsDs.size()));
        targetsTable.expandAll();
    }

    protected void initContactsTableColumns() {
        targetsTable.addGeneratedColumn("description", new Table.ColumnGenerator<CampaignTarget>() {
            @Override
            public Component generateCell(CampaignTarget entity) {
                return new Table.PlainTextCell(getMessage(entity.getDescription()));
            }
        });
    }

    protected void setRowIcon() {
        targetsTable.setIconProvider(new Table.IconProvider() {
            @Nullable
            @Override
            public String getItemIcon(Entity entity) {
                String description = ((CampaignTarget) entity).getDescription();
                switch (description) {
                    case "targetOk":
                        return "icons/ok.png";
                    case "noCommunications":
                    case "noContacts":
                    case "double":
                        return "icons/cancel.png";
                    //case "noContacts":"icons/minus.png";

                    default:
                        return null;
                }
            }
        });
    }

    protected void addWindowAction() {

        Action winCommitAction = new AbstractAction(Editor.WINDOW_COMMIT) {
            @Override
            public void actionPerform(Component component) {
                //close("close");
                Integer successAdded = 0;
                Set<CampaignTarget> targetsToExclude = new HashSet<>();

                for (CampaignTarget item : campaignTargetsDs.getItems()) {
                    if (item.getDescription().equals("targetOk")) {
                        CallCampaignTrgt group = getGroup(item.getCompany());
                        if (group == null) {
                            group = createTargetGroup(item);
                            targetsDs.addItem(group);
                        }
                        CallCampaignTrgt newEntity = createTarget(item, group);
                        targetsToExclude.add(item);
                        successAdded++;

                        targetsDs.addItem(newEntity);
                    }
                }
                targetsDs.commit();
                targetsDs.refresh();
                for (CampaignTarget item : targetsToExclude) {
                    campaignTargetsDs.excludeItem(item);
                }
                showNotification(messages.formatMessage(getClass(), "label.successAdded", successAdded), NotificationType.HUMANIZED);
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Select");
            }

            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled && campaign != null);
            }

        };
        winCommitAction.setEnabled(false);
        addAction(winCommitAction);

        AbstractAction winCloseAction = new AbstractAction(Editor.WINDOW_CLOSE) {
            @Override
            public void actionPerform(Component component) {
                close(Editor.CLOSE_ACTION_ID);
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Close");
            }
        };
        winCloseAction.setPrimary(true);
        addAction(winCloseAction);
    }

    @Nullable
    protected CallCampaignTrgt getGroup(Company company){
        for(CallCampaignTrgt item : targetsDs.getItems()){
            if (item.getIsGroup() && item.getName().equals(company.getInstanceName()))
                return item;
        }
        return null;
    }

    protected CallCampaignTrgt createTargetGroup(CampaignTarget item){
        CallCampaignTrgt result = metadata.create(CallCampaignTrgt.class);

        result.setName(item.getCompany().getInstanceName());
        result.setCampaign(campaign);
        result.setCompany(item.getCompany());
        result.setCountFailedTries(0);
        result.setCountTries(0);
        result.setIsGroup(true);

        return result;
    }

    protected CallCampaignTrgt createTarget(CampaignTarget item, @Nonnull CallCampaignTrgt group){
        CallCampaignTrgt result = metadata.create(CallCampaignTrgt.class);

        result.setName(item.getCommunication().getInstanceName());
        result.setCampaign(campaign);
        result.setCompany(item.getCompany());
        result.setCommunication(item.getCommunication());
        result.setCountFailedTries(0);
        result.setCountTries(0);
        result.setPriority(item.getPriority());
        result.setIsGroup(false);
        result.setParent(group);

        return result;
    }
}