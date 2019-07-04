package com.haulmont.thesis.crm.web.emailCampaign;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.crm.core.app.unisender.UniSender;
import com.haulmont.thesis.crm.core.app.unisender.entity.Campaign;
import com.haulmont.thesis.crm.core.app.unisender.entity.FieldData;
import com.haulmont.thesis.crm.core.app.unisender.entity.MailList;
import com.haulmont.thesis.crm.core.app.unisender.requests.ExportContactsRequest;
import com.haulmont.thesis.crm.core.app.unisender.requests.GetCampaignDeliveryStatsRequest;
import com.haulmont.thesis.crm.core.app.unisender.requests.ImportContactsRequest;
import com.haulmont.thesis.crm.core.app.unisender.responses.GetCampaignDeliveryStatsResponse;
import com.haulmont.thesis.crm.core.app.unisender.responses.ImportContactsResponse;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.ActivityDirectionEnum;
import com.haulmont.thesis.crm.enums.CampaignState;
import com.haulmont.thesis.crm.web.ui.basic.browse.AbstractCampaignCardBrowser;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmailCampaignBrowser<T extends EmailCampaign> extends AbstractCampaignCardBrowser<T> {

    @Named("cardsDs")
    protected GroupDatasource<EmailCampaign, UUID> cardsDs;

    @Inject
    private DataManager dataManager;

    protected List<ActivityRes> resList = new ArrayList<>();
    protected List<EmailActivity> emailActivities = new ArrayList<>();
    protected List<EmailCampaignTarget> emailCampaignTargets = new ArrayList<>();
    protected Set<Entity> toCommit = new HashSet<>();


    @Override
    protected void initTableColoring() {
        cardsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null)
                    return EmailCampaignBrowser.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(T entity) {
        if (entity.getState() != null && entity.getState().equals(CampaignState.InWork.getId())) return "taskremind";
        if (entity.getState() != null && entity.getState().equals(CampaignState.Canceled.getId())) return "thesis-task-finished";
        if (entity.getState() != null && entity.getState().equals(CampaignState.Completed.getId())) return "thesis-task-finished";

        return null;
    }

    public void sendTargets() {
        EmailCampaign campaign = cardsTable.getSingleSelected();
        if (campaign != null) {
            UniSender us = new UniSender();
            try {
                if (campaign.getList_id() != null || !campaign.getList_id().equals("") || !campaign.getList_id().equals("0")) {
                    getCampaignTargets(campaign);
                    ArrayList<String> addresses = new ArrayList<>();
                    for (EmailCampaignTarget item : emailCampaignTargets) {
                        addresses.add(item.getName());
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("Лист рассылки отправлен:");
                    sb.append(System.lineSeparator());

                    List<List<String>> chunksList = chunks(addresses, 500);

                    for (List<String> adr: chunksList) {
                        ImportContactsRequest ic = new ImportContactsRequest(createFieldData(campaign.getList_id(), adr));
                        ImportContactsResponse icr = us.importContacts(ic);
                        sb.append(String.format("total:%s, inserted:%s, updated:%s, deleted:%s, new_emails:%s",
                                icr.getTotal(), icr.getInserted(), icr.getUpdated(), icr.getDeleted(), icr.getNewEmails()));
                        sb.append(System.lineSeparator());
                    }
                    log.warn("EmailCampaignBrowser.sendTargets.Result: " + sb.toString());
                    showNotification(sb.toString(), NotificationType.HUMANIZED);
                }
            } catch (Exception e) {
                log.error("EmailCampaignBrowser.sendTargets" + e.getMessage());
                showNotification(e.getMessage(), NotificationType.HUMANIZED);
            }
        }
    }

    public void getSendList() {
        EmailCampaign campaign = cardsTable.getSingleSelected();
        UniSender us = new UniSender();
        try {
            if (campaign != null && campaign.getName() != "" && campaign.getList_id() == null) {
                MailList mailList = us.createList(new MailList(campaign.getName()));
                campaign.setList_id(mailList.getId().toString());
                dataManager.commit(new CommitContext(campaign));
            }
        } catch (Exception e) {
            log.error("EmailCampaignBrowser.getSendList: " + e.getMessage());
            showNotification(e.getMessage(), NotificationType.HUMANIZED);
        }
    }

    protected FieldData createFieldData(String listId, List<String> addresses) {
        FieldData fd = new FieldData();
        fd.addFields(new String[]{"email", "email_list_ids"});

        for (String adr:addresses) {
            fd.addValues(new String[]{adr, listId});
        }

        return fd;
    }

    static <T> List<List<T>> chunks(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
        }
        return parts;
    }

    public void getStatusLetters() {
        EmailCampaign emailCampaign = cardsTable.getSingleSelected();
        if (emailCampaign != null && emailCampaign.getList_id() != null && !emailCampaign.getList_id().equals("0") && !emailCampaign.getList_id().equals("")) {
            UniSender us = new UniSender();
            try {
                getActivityResults();
                getCampaignTargets(emailCampaign);
                getEmailActivitys(emailCampaign);

                ArrayList<Campaign> campaigns = us.getCampaigns();
                ArrayList<Campaign> resultCampaigns = filterCampaign(campaigns, Integer.valueOf(emailCampaign.getList_id()));
                for (Campaign resultCampaign:resultCampaigns) {
                    GetCampaignDeliveryStatsRequest campaignDeliveryStatsRequest = new GetCampaignDeliveryStatsRequest(resultCampaign);
                    GetCampaignDeliveryStatsResponse campaignDeliveryStats = us.getCampaignDeliveryStats(campaignDeliveryStatsRequest);

                    GetCampaignDeliveryStatsRequest visitedLinksRequest = new GetCampaignDeliveryStatsRequest(resultCampaign, true);
                    GetCampaignDeliveryStatsResponse visitedLinks = us.getVisitedLinks(visitedLinksRequest);

                    emailActivity(resultCampaign, campaignDeliveryStats, visitedLinks);
                }
                doCommit();
                showNotification("Статусы писем проверены (^_^)", NotificationType.HUMANIZED);
            } catch (Exception e) {
                log.error("EmailCampaignBrowser.getMessagesStatuses: " + e.getMessage());
            } finally {
                doClear();
            }
        }
    }

    protected ArrayList<Campaign> filterCampaign(ArrayList<Campaign> campaigns, Integer listId) {
        ArrayList<Campaign> resultCampaigns = new ArrayList<>();
        for (Campaign сampaign : campaigns) {
            if (listId.equals(сampaign.getList_id())) {
                resultCampaigns.add(сampaign);
            }
        }
        return resultCampaigns;
    }

    protected void emailActivity(Campaign campaign,
                                 GetCampaignDeliveryStatsResponse campaignDeliveryStats,
                                 GetCampaignDeliveryStatsResponse visitedLinks) {

        for (List<String> data : campaignDeliveryStats.getData()) {
            String email = data.get(0); //email campaignDeliveryStats.getFields()
            String sendResult = data.get(1); //send_result campaignDeliveryStats.getFields()
            String lastUpdate = data.get(2); //last_update campaignDeliveryStats.getFields()

            EmailActivity emailActivity = emailActivity(email, campaign.getSubject());

            if (emailActivity == null) {
                EmailCampaignTarget emailCampaignTarget = emailCampaignTarget(email);
                if (emailCampaignTarget != null) {
                    emailActivity = createEmailActivity(emailCampaignTarget, campaign);
                }
            }

            if (emailActivity != null) {
                emailActivity.setEndTimeFact(stringToDate(lastUpdate));
                emailActivity.setResult(activityResult(sendResult));
                emailActivity.setDetails(getVisitedLink(visitedLinks, emailActivity.getAddress()));
                toCommit.add(emailActivity);
            }
        }
    }

    protected String getVisitedLink(GetCampaignDeliveryStatsResponse visitedLinks, String email) {
        for (List<String> data : visitedLinks.getData()) {
            String emailLink = data.get(0);
            String link = data.get(1);
            if (emailLink.equals(email)) {
                return link;
            }
        }
        return "";
    }

    protected EmailActivity createEmailActivity(EmailCampaignTarget emailCampaignTarget, Campaign campaign) {
        EmailActivity emailActivity = metadata.create(EmailActivity.class);
        emailActivity.setCampaign(emailCampaignTarget.getCampaign());
        emailActivity.setAddress(emailCampaignTarget.getName());
        emailActivity.setDescription(campaign.getSubject());
        emailActivity.setKind(configuration.getConfig(CrmConfig.class).getActivityKindEmail());
        emailActivity.setDirection(ActivityDirectionEnum.OUTBOUND);
        emailActivity.setOrganization(configuration.getConfig(DefaultEntityConfig.class).getOrganizationDefault());
        emailActivity.setContactPerson(emailCampaignTarget.getCommunication().getContactPerson());
        emailActivity.setCompany(emailCampaignTarget.getCompany());
        emailActivity.setProject(emailCampaignTarget.getCampaign().getProject());
        return emailActivity;
    }

    protected void getActivityResults() {
        LoadContext loadContext = new LoadContext(ActivityRes.class).setView("_local");
        loadContext.setQueryString("select e from crm$ActivityRes e where e.entityType = :entityType")
                .setParameter("entityType", "crm$EmailActivity");
        resList = dataManager.loadList(loadContext);
    }

    protected ActivityRes activityResult(String deliveryStats) {
        for (ActivityRes activityRes : resList) {
            if(deliveryStats.equals(activityRes.getCode())){
                return activityRes;
            }
        }
        return null;
    }

    protected void getEmailActivitys(EmailCampaign campaign) {
        LoadContext loadContext = new LoadContext(EmailActivity.class).setView("edit");
        loadContext.setQueryString("select e from crm$EmailActivity e where e.campaign.id = :campaignId")
                .setParameter("campaignId", campaign.getId());
        emailActivities = dataManager.loadList(loadContext);
    }

    protected void getCampaignTargets(EmailCampaign campaign) {
        LoadContext loadContext = new LoadContext(EmailCampaignTarget.class).setView("local-company_minimal-contact_person_minimal");
        loadContext.setQueryString("select e from crm$EmailCampaignTarget e where e.campaign.id = :campaignId")
                .setParameter("campaignId", campaign.getId());
        emailCampaignTargets = dataManager.loadList(loadContext);
    }

    protected EmailActivity emailActivity(String email, String subject) {
        for (EmailActivity activity : emailActivities) {
            if (email.equals(activity.getAddress()) && subject.equals(activity.getDescription())) {
                return activity;
            }
        }
        return null;
    }

    protected EmailCampaignTarget emailCampaignTarget(String email) {
        for (EmailCampaignTarget emailCampaignTarget : emailCampaignTargets) {
            if(email.equals(emailCampaignTarget.getName())) {
                return emailCampaignTarget;
            }
        }
        return null;
    }

    protected void doCommit() {
        if (toCommit.size() > 0) {
            dataManager.commit(new CommitContext(toCommit));
        }
    }

    protected void doClear() {
        resList.clear();
        emailActivities.clear();
        emailCampaignTargets.clear();
        toCommit.clear();
    }

    protected Date stringToDate(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    }

    public void getStatusContacts() {
        EmailCampaign emailCampaign = cardsTable.getSingleSelected();
        if (emailCampaign != null && emailCampaign.getList_id() != null && !emailCampaign.getList_id().equals("0") && !emailCampaign.getList_id().equals("")) {
            UniSender us = new UniSender();
            try {
                List<EmailStatus> emailStatuses = getEmailStatuses();
                getCampaignTargets(emailCampaign);
                MailList mailList = new MailList(Integer.valueOf(emailCampaign.getList_id()));
                List<String> fieldNames = Arrays.asList("email", "email_status");
                int i = 0;
                boolean isWhile = true;
                while (isWhile) {
                    ExportContactsRequest exportContactsRequest = new ExportContactsRequest(mailList, fieldNames, i, 1000);
                    FieldData fd = us.exportContacts(exportContactsRequest);

                    if (fd.getDataCount() > 0) {
                        updateEmailStatus(fd, emailStatuses);
                        if (fd.getDataCount() < 1000) {
                            isWhile = false;
                        } else {
                            i = i + 1000;
                        }
                    } else {
                        isWhile = false;
                    }
                }

                doCommit();
                showNotification("Статусы контактов проверены (^_^)", NotificationType.HUMANIZED);
            } catch (Exception e) {
                log.error("EmailCampaignBrowser.getStatusContacts: " + e.getMessage());
            } finally {
                doClear();
            }
        }
    }

    protected void updateEmailStatus(FieldData fd, List<EmailStatus> emailStatuses) {
        int i = fd.getDataCount() - 1;
        while(i >= 0) {
            List<String> data = fd.getData(i);
            String email = data.get(0);
            String emailStatus = data.get(1);

            EmailCampaignTarget emailCampaignTarget = emailCampaignTarget(email);
            if (emailCampaignTarget != null) {
                Communication communication = emailCampaignTarget.getCommunication();
                if (communication != null) {
                    communication.setEmailStatus(getEmailStatus(emailStatuses, emailStatus));
                    toCommit.add(communication);
                }
            }
            i--;
        }
    }

    protected List<EmailStatus> getEmailStatuses() {
        LoadContext loadContext = new LoadContext(EmailStatus.class).setView("_local");
        loadContext.setQueryString("select e from crm$EmailStatus e");
        return dataManager.loadList(loadContext);
    }

    protected EmailStatus getEmailStatus(List<EmailStatus> emailStatuses, String emailStatus)  {
        for (EmailStatus status:emailStatuses) {
            if(emailStatus.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }


}