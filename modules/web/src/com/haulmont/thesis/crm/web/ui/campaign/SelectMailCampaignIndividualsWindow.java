package com.haulmont.thesis.crm.web.ui.campaign;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.core.entity.Company;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.core.entity.Individual;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

public class SelectMailCampaignIndividualsWindow extends AbstractWindow {


    Set<Company> companies = new HashSet<>();
    Set<ContactPerson> contactPersons;
    //Set<ExtContactPerson> persons;
    //Set<Communications> communications;

    protected EmailCampaign campaign;

    protected LookupField campaignField;
    protected GroupTable targetsTable;
    protected Label sizeLabel, statusLabel;
    protected Button windowCommitBtn;

    protected GroupDatasource<CampaignTarget, UUID> campaignTargetsDs;
    protected CollectionDatasource<EmailCampaignTarget, UUID> targetsDs;

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    protected ActivityService activityCreator;

    @SuppressWarnings("unchecked")
    @Override
    public void init(final Map<String, Object> params) {
        super.init(params);

        //companies = (Set<ExtCompany>) params.get("selectedCompanies");
        contactPersons = (Set<ContactPerson>) params.get("selectedContactPersons");
        //communications = (Set<Communications>) params.get("selectedComm");

        findStandardComponents();
        initCampaignTargetsDs();
        initListeners();

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
        windowCommitBtn = getComponentNN(Editor.WINDOW_COMMIT);
    }

    protected void initCampaignTargetsDs() {
        ExtCompany tempComp = new ExtCompany();
        tempComp.setContactPersons(new ArrayList<ContactPerson>(contactPersons));
        companies.add(tempComp);

        /*for (ContactPerson contact : contactPersons) {
            if (contact.getCompany() != null) {
                companies.add(contact.getCompany());
            }
        }*/
        /*for (ExtContactPerson contact : contactPersons)
               {
               for (Communication comm : contact.getCommunications()) {
                if (comm != null && (comm.getEmailStatus() == null || comm.getEmailStatus().getUnisenderValid())
                        && comm.getPref() && comm.getDeleteTs() == null && comm.getCommKind() != null
                        &&comm.getCommKind().getCommunicationType().equals(CommunicationTypeEnum.email)) {

                    CampaignTarget target = new CampaignTarget();
                    target.setContactPerson(contact);
                    target.setCommunication(comm);
                    campaignTargetsDs.addItem(contactPersons);
                }
            }
        }*/
        Map<String, Object> params = new HashMap<>();
        params.put("companies", companies);
        params.put("type", CommunicationTypeEnum.email);
        campaignTargetsDs.refresh(params);
    }

    /*protected void initCampaignTargetsDs() {
        Map<String, Object> params = new HashMap<>();
        params.put("companies", companies);
        params.put("type", CommunicationTypeEnum.email);

        campaignTargetsDs.refresh(params);
    }*/

    protected void initListeners(){
        campaignField.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (value!=null){
                    windowCommitBtn.setEnabled(true);
                }
                else{
                    windowCommitBtn.setEnabled(false);
                }
                campaign = (EmailCampaign) value;
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
                for (EmailCampaignTarget item : targetsDs.getItems()) {
                    if(!item.getIsGroup())
                        tc.add(item.getName());
                }
            }

            for (CampaignTarget item : campaignTargetsDs.getItems()){
                if (item.getDescription().equals("") || item.getDescription().equals("targetOk") || item.getDescription().equals("double")){
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

                activityCreator =  AppBeans.get(ActivityService.class);

                for (CampaignTarget item : campaignTargetsDs.getItems()) {
                    if (item.getDescription().equals("targetOk")) {
                        //EmailCampaignTarget group = getGroup(item.getCompany());
                        /*if (group == null) {
                            group = createTargetGroup(item);
                            targetsDs.addItem(group);
                        }*/
                        EmailCampaignTarget newEntity = createTarget(item);
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
    protected EmailCampaignTarget getGroup(Company company){
        for(EmailCampaignTarget item : targetsDs.getItems()){
            if (item.getIsGroup() && item.getName().equals(company.getInstanceName()))
                return item;
        }
        return null;
    }

    /*protected EmailCampaignTarget createTargetGroup(CampaignTarget item){
        EmailCampaignTarget result = metadata.create(EmailCampaignTarget.class);

        result.setName(item.getCompany().getInstanceName());
        result.setCampaign(campaign);
        result.setCompany(item.getCompany());
        result.setIsGroup(true);

        return result;
    }*/

    protected EmailCampaignTarget createTarget(CampaignTarget item){
        EmailCampaignTarget result = metadata.create(EmailCampaignTarget.class);

        result.setName(item.getCommunication().getInstanceName());
        result.setCampaign(campaign);
        result.setCompany(item.getCompany());
        result.setCommunication(item.getCommunication());
        result.setIsGroup(false);

        return result;
    }
}

