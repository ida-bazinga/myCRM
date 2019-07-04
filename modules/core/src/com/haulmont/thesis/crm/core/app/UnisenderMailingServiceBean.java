/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app
 */
package com.haulmont.thesis.crm.core.app;

import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.unisender.entity.InvalidArgumentListException;
import com.haulmont.thesis.crm.core.app.unisender.entity.UniContact;
import com.haulmont.thesis.crm.core.app.unisender.entity.UniResult;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.ActivityDirectionEnum;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author a.donskoy
 */
@Service(UnisenderMailingService.NAME)
public class UnisenderMailingServiceBean implements UnisenderMailingService {
    private Log log = LogFactory.getLog(getClass());

    @Inject
    private DataManager dataManager;
    @Inject
    private CrmConfig config;
    @Inject
    protected Configuration configuration;
    @Inject
    protected Metadata metadata;

    private static final String LANG = "ru";
    private String API_URL = "https://api.unisender.com/";
    private String KEY;



    private static final Log LOGGER = LogFactory.getLog(UnisenderMailingServiceBean.class);

    public void initUnisender(String api_url, String key) {
        KEY = key;
        API_URL = api_url;
    }

    public void initUnisender() {
        initUnisender("https://api.unisender.com/", config.getUniKey()); /*"6srqmy6tbgss8c9sjffqzbuzsg4iw79pdjmyx3ka"*/ //"5kiaipcazqitujziwpqpr3343pdr68u4nc5rg4jo");
    }

    private JSONArray postRequest(String method) {
        return postRequest(method, null);
    }

    private JSONArray postRequest(String method, String args) {
        String urlStr = API_URL + LANG + "/api/" + method + "?format=json&api_key=" + KEY;
        if (args != null) {
            urlStr += args;
        }

        try {

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }

            } };

            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLContext.setDefault(ctx);

            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            short attempts = 3;
            do {
                try {
                    StringBuffer response = new StringBuffer();
                    con.connect();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    con.disconnect();
                    JSONObject jsonResponse = new JSONObject(new JSONTokener(response.toString()));
                    Object item = jsonResponse.get("result");

                    try {
                        return jsonResponse.getJSONArray("result");
                    } catch (Exception e) {
                        return new JSONArray().put(jsonResponse.getJSONObject("result"));
                    }
                } catch (IOException e) {
                    attempts--;
                    Thread.sleep(50);
                    if (attempts == 0) {
                        LOGGER.error("Unisender error: " + e.getLocalizedMessage());
                    }
                }
            } while (attempts > 0);







            /*if (item instanceof JSONArray)
            {
                return jsonResponse.getJSONArray("result");
            }
            else
            {
                try {
                    return new JSONArray().put(jsonResponse.getJSONObject("result"));
                } catch (Exception e) {
                    return new JSONArray().put(jsonResponse);
                }
            }*/

        } catch (Exception e) {
            LOGGER.error("UNISENDER Error", e);
        }
        return new JSONArray();
    }



    public void refreshMessagesUnisenderTask() {
        ArrayList<MessageUnisender> lists;
        try {
            lists = getMessages();
        } catch (Exception e) {
            lists = null;
        }
        List<String> existingIds = new ArrayList<>();
        for (MessageUnisender item : getAllMessagesUnisender()) {
            existingIds.add(item.getCode());
        }
        for (int i = lists.size()-1; i >= 0; i--) {
            if (existingIds.remove(lists.get(i).getCode()))
            {
                lists.remove(i);
            }
        }
        try {
            if (lists.size() > 0) {
                dataManager.commit(new CommitContext(lists));
            }
        } catch (SecurityException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return;
    }

    public void refreshCampaignsUnisenderTask() {
        ArrayList<CampaignUnisender> lists;
        try {
            lists = getCampaigns();
        } catch (Exception e) {
            lists = null;
        }
        List<String> existingIds = new ArrayList<>();
        for (CampaignUnisender item : getAllCampaignsUnisender()) {
            existingIds.add(item.getCode());
        }
        for (int i = lists.size()-1; i >= 0; i--) {
            if (existingIds.remove(lists.get(i).getCode()))
            {
                lists.remove(i);
            }
        }
        try {
            if (lists.size() > 0) {
                dataManager.commit(new CommitContext(lists));
            }
        } catch (SecurityException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return;
    }

    @Override
    public void getAllCampaignStatusesTask() {
    }

    public ArrayList<MessageCampaignUnisender> getLists() throws Exception {
        JSONArray result = postRequest("getLists");
        ArrayList<MessageCampaignUnisender> list = new ArrayList<MessageCampaignUnisender>();
        if (result != null) {
            int len = result.length();
            for (int i=0;i<len;i++){
                MessageCampaignUnisender campaignUnisender = metadata.create(MessageCampaignUnisender.class);
                campaignUnisender.setCode(String.valueOf(result.getJSONObject(i).getInt("id")));
                campaignUnisender.setName_ru(result.getJSONObject(i).getString("title"));
                list.add(campaignUnisender);
            }
        }
        return list;
    }

    public ArrayList<CampaignUnisender> getCampaigns() throws Exception {
        JSONArray result = postRequest("getCampaigns");
        ArrayList<CampaignUnisender> list = new ArrayList<CampaignUnisender>();
        if (result != null) {
            HashMap<String, MessageCampaignStatus> campStatus = getAllCampaignStatuses();
            int len = result.length();
            for (int i=0;i<len;i++){
                CampaignUnisender campaignUnisender = metadata.create(CampaignUnisender.class);
                campaignUnisender.setCode(String.valueOf(result.getJSONObject(i).getInt("id")));
                campaignUnisender.setName_ru(result.getJSONObject(i).getString("subject"));
                campaignUnisender.setStatus(campStatus.get(result.getJSONObject(i).getString("status")));

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = formatter.parse(result.getJSONObject(i).getString("start_time"));
                    campaignUnisender.setStart_time(date);
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }

                campaignUnisender.setMessage_id(result.getJSONObject(i).getInt("message_id"));
                campaignUnisender.setList_id(result.getJSONObject(i).getInt("list_id"));
                campaignUnisender.setSender_name(result.getJSONObject(i).getString("sender_name"));
                campaignUnisender.setSender_email(result.getJSONObject(i).getString("sender_email"));
                campaignUnisender.setStats_url(result.getJSONObject(i).getString("stats_url"));

                list.add(campaignUnisender);
            }
        }
        return list;
    }

    public ArrayList<MessageUnisender> getMessages() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd%20HH:mm");//dd/MM/yyyy
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        Date dateFrom = calendar.getTime();
        calendar.add(Calendar.DATE, 8);


        JSONArray result = postRequest("getMessages", buildArgs("limit", "100", "date_from",  dateFormat.format(dateFrom), "date_to", dateFormat.format(calendar.getTime())));
        ArrayList<MessageUnisender> list = new ArrayList<MessageUnisender>();
        if (result != null) {
            int len = result.length();
            for (int i=0;i<len;i++){
                try {
                    list.add(new MessageUnisender(result.getJSONObject(i).getString("subject"), result.getJSONObject(i).getInt("id"), result.getJSONObject(i).getInt("list_id")));
                } catch (Exception e)
                {
                    LOGGER.error("UNISENDER Error", e);
                }
            }
        }
        return list;
    }

    public int createList(String title) throws Exception {
        if (title == null || title == "") {
            return -1;
        }
        try {
            JSONObject response = postRequest("createList", buildArgs("title", URLEncoder.encode(title, "UTF-8"))).getJSONObject(0);//&arg1=ARG_1&argN=ARG_N
            return response.getInt("id");
        } catch (Exception e) {
            return 0;
        }
    }

    //*** int-methods return:

    // -1, if the arguments did not pass prevalidation
    //  0, if a runtime error occured and org.json returned JSONException/unisender returned invalid_arg error
    //  1, if the method was successful

    public int updateList(int id, String title) throws Exception {
        if (id <= 0  || title == null || title == "") {
            return -1;
        }
        try {
            if (postRequest("updaInvteList", buildArgs("list_id", Integer.toString(id), "title", title)).length() == 0){
                return 1;
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public int deleteList(int id) throws Exception {
        if (id <= 0) {
            return -1;
        }
        try {
            if (postRequest("deleteList", buildArgs("list_id", Integer.toString(id))).length() == 0){
                return 1;
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public HashMap<String, UniContact> exportContacts(String listId) {
        try {
            JSONArray result = postRequest("exportContacts", buildArgs("list_id", listId));
            HashMap<String, UniContact> map = new HashMap<String, UniContact>();
            if (result != null) {
                HashMap<String, EmailStatus> statuses = getAddressStatuses();
                int len = result.getJSONObject(0).getJSONArray("data").length();
                for (int i=0;i<len;i++){
                    //list.add(new UniContact(result.getJSONObject(i).getInt("contactType"), result.getJSONObject(i).getString("title")));
                    UniContact newContact = metadata.create(UniContact.class);
                    newContact.setEmail(result.getJSONObject(0).getJSONArray("data").getJSONArray(i).getString(0));
                    newContact.setEmailStatus(statuses.get(result.getJSONObject(0).getJSONArray("data").getJSONArray(i).getString(1)));
                    //setStatus
                    map.put(newContact.getEmail(), newContact);
                }
            }
            return map;
        } catch (Exception e) {
            return new HashMap<String, UniContact>();
        }
    }

    public String getCampaignStatus(String campaignId) {
        try {
            JSONArray result = postRequest("getCampaignStatus", buildArgs("campaign_id", campaignId));
            return result.getJSONObject(0).getString("status");
        } catch (Exception e) {
        }
        return null;
    }

    public String getListFromMessage(String messageId) {
        try {
            JSONArray result = postRequest("getMessage", buildArgs("id", messageId));
            return result.getJSONObject(0).getString("list_id");
        } catch (Exception e) {
        }
        return null;
    }

    public HashMap<String, UniResult> getMessagesStatuses(String campaignId) {
        HashMap<String, UniResult> result = new HashMap<>();
        try {
            JSONArray resultJSON = postRequest("getCampaignDeliveryStats", buildArgs("campaign_id", campaignId));
            for (int i = 0; i < resultJSON.getJSONObject(0).getJSONArray("data").length(); i++)
            {
                if (resultJSON.getJSONObject(0).getJSONArray("data") != null) {
                    JSONArray item = resultJSON.getJSONObject(0).getJSONArray("data").getJSONArray(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = dateFormat.parse(item.getString(2));
                    result.put(item.getString(0), new UniResult(item.getString(1), date));
                }
            }
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return result;
    }


    public int importContacts(String uniCampaignId, ArrayList<String> addresses) throws Exception {
        int result = 0;
        do {
            ArrayList<String> tempAddresses = new  ArrayList<String>();
            tempAddresses.addAll((addresses.size()>=10) ? addresses.subList(0, 10) : addresses);
            String builtArgs = "&field_names[0]=email&field_names[1]=email_list_ids"/*&field_names[2]=double_optin", "field_names[3]", "Name"*/ + buildArgsForImport(uniCampaignId, tempAddresses);
            JSONArray resultJSON = postRequest("importContacts", builtArgs);
            result += resultJSON.getJSONObject(0).getInt("total") - resultJSON.getJSONObject(0).getInt("invalid");
            addresses.removeAll(tempAddresses);
        } while (addresses.size() > 0);

        return result;
    }

    private String buildArgs(String... args) throws Exception {
        StringBuffer argStrBuff = new StringBuffer();
        if (args.length % 2 == 0) {
            for (int i = 0; i < args.length; i+=2) {
                argStrBuff.append("&" + args[i] + "=" + args[i+1]  );
            }
            return argStrBuff.toString();
        } else {
            throw new InvalidArgumentListException("The amount of arguments supplied to buildArgs should be even, with odd arguments defining Argument Names and even arguments defining Argument Values.");
        }
    }

    private String buildArgsForImport(String listId, ArrayList<String> args) throws Exception {
        StringBuffer argStrBuff = new StringBuffer();
        for (int i = 0; i < args.size(); i+=1) {
            argStrBuff.append("&data[" + i + "][0]=" + args.get(i) + "&data[" + i + "][1]=" + listId/* + "&data[" + i + "][2]=3""&data[" + i + "][3]=DasNamen"*/);
        }
        return argStrBuff.toString();
    }

    public HashMap<String, MessageCampaignStatus> getAllCampaignStatuses() {
        LoadContext loadContext = new LoadContext(MessageCampaignStatus.class).setView("_local");
        loadContext.setQueryString("select e from crm$MessageCampaignStatus e");
        List<MessageCampaignStatus> listRes = dataManager.loadList(loadContext);
        HashMap<String, MessageCampaignStatus> resultMap = new HashMap<String,MessageCampaignStatus>();
        for (MessageCampaignStatus item : listRes)
        {
            resultMap.put(item.getCode(), item);
        }
        return resultMap;
    }

    /*public HashMap<String, ActivityResult> getActivityResults() {
        LoadContext loadContext = new LoadContext(ActivityResult.class).setView("_local");
        loadContext.setQueryString("select e from crm$ActivityResult e");
        List<ActivityResult> listRes;
        listRes = dataManager.loadList(loadContext);
        HashMap<String, ActivityResult> resultMap = new HashMap<String, ActivityResult>();
        for (ActivityResult item : listRes)
        {
            resultMap.put(item.getCode(), item);
        }
        return resultMap;
    }*/

    public HashMap<String, EmailStatus> getAddressStatuses() {
        LoadContext loadContext = new LoadContext(EmailStatus.class).setView("_local");
        loadContext.setQueryString("select e from crm$EmailStatus e");
        List<EmailStatus> listRes;
        listRes = dataManager.loadList(loadContext);
        HashMap<String, EmailStatus> resultMap = new HashMap<String, EmailStatus>();
        for (EmailStatus item : listRes)
        {
            resultMap.put(item.getCode(), item);
        }
        return resultMap;
    }

    public List<MessageUnisender> getAllMessagesUnisender() {
        LoadContext loadContext = new LoadContext(MessageUnisender.class).setView("_local");
        loadContext.setQueryString("select e from crm$MessageUnisender e");
        return dataManager.loadList(loadContext);
    }

    public List<CampaignUnisender> getAllCampaignsUnisender() {
        LoadContext loadContext = new LoadContext(CampaignUnisender.class).setView("_local");
        loadContext.setQueryString("select e from crm$CampaignUnisender e");
        return dataManager.loadList(loadContext);
    }

    public List<EmailCampaign> getActiveEmailCampaigns() {
        LoadContext loadContext = new LoadContext(EmailCampaign.class).setView("browse");
        loadContext.setQueryString("select e from crm$EmailCampaign e");
        return dataManager.loadList(loadContext);
    }

    private HashMap<String, ActivityRes> getActivityResults() {
        LoadContext loadContext = new LoadContext(ActivityRes.class).setView("_local");
        loadContext.setQueryString("select e from crm$ActivityRes e");
        List<ActivityRes> listRes;
        listRes = dataManager.loadList(loadContext);
        HashMap<String, ActivityRes> resultMap = new HashMap<String, ActivityRes>();
        for (ActivityRes item : listRes)
        {
            resultMap.put(item.getCode(), item);
        }
        return resultMap;
    }

    /*public List<CampaignUnisender> getCampaignByList(String list_id) {
        LoadContext loadContext = new LoadContext(CampaignUnisender.class).setView("_local");
        loadContext.setQueryString("select e from crm$CampaignUnisender e where e.list_id = '" + list_id + "' order by e.createTs");
        return dataManager.loadList(loadContext);
    }

    public List<CampaignUnisender> getCampaignByList(String list_id) {
        LoadContext loadContext = new LoadContext(CampaignUnisender.class).setView("_local");
        loadContext.setQueryString("select e from crm$CampaignUnisender e where e.list_id = " + Integer.parseInt(list_id));
        return dataManager.loadList(loadContext);
    }

    protected List<EmailActivity> getCampaignActivities(EmailCampaign campaign) {
        LoadContext loadContext = new LoadContext(EmailActivity.class).setView("for-email-targets");
        loadContext.setQueryString("select e from crm$EmailActivity e where e.emailCampaignTarget.campaign.id = :campaign").setParameter("campaign", campaign.getId());
        return dataManager.loadList(loadContext);
    }*/

    @Override
    public void getMessagesStatusesTask() {
        List<EmailCampaign> campaigns = getActiveEmailCampaigns();
        refreshCampaignsUnisenderTask();
        List<EmailActivity> activitiesToCommit = new ArrayList<>();
        List<EmailActivity> activWithNoResults = new ArrayList<>();
        List<EmailCampaignTarget> targetsToCommit = new ArrayList<>();
        List<Communication> communicationsToCommit = new ArrayList<Communication>();
        for (EmailCampaign campaign : campaigns) {
            if (campaign.getList_id() != null && !campaign.getList_id().equals("0") && !campaign.getList_id().equals("")) {
                List<CampaignUnisender> list = getCampaignByList(campaign.getList_id());
                if (list.size() > 0) {
                    if (campaign.getCampaignStatus() == null || campaign.getCampaignStatus().getRequestMessagesStatuses() == null || campaign.getCampaignStatus().getRequestMessagesStatuses()) {
                        HashMap<String, UniResult> resultsMap = getMessagesStatuses("" + list.get(0).getCode());
                        HashMap<String, ActivityRes> activityResults = getActivityResults();
                        List<EmailActivity> activities = new ArrayList<>();
                        List<EmailCampaignTarget> targetsWithoutActivities = new ArrayList<>();
                        List<EmailCampaignTarget> targets = getCampaignTargets(campaign);
                        /*
                        for (EmailCampaignTarget target : targets) {
                            if (target.getCommunication().getEmailStatus() == null
                                    || (target.getCommunication().getEmailStatus().getUnisenderValid()
                                    || target.getCommunication().getEmailStatus().getUnisenderValid() == null)) {
                                if (target.getEmailActivity() != null
                                        && target.getEmailActivity().getEndTimeFact() != null
                                        && !target.getEmailActivity().getEndTimeFact().before(list.get(0).getStart_time())) {
                                    activities.add(target.getEmailActivity());
                                } else {
                                    targetsWithoutActivities.add(target);
                                }
                            }
                        }
                        */
                        for (EmailCampaignTarget targetItem : targetsWithoutActivities) {
                            EmailActivity activity = createActivitiesFromTargets(campaign, targetItem);
                            //targetItem.setEmailActivity(activity);
                            activities.add(activity);
                        }
                        for (EmailActivity activity : activities) {
                            if (activity.getDeleteTs() == null && resultsMap.size()>0) {
                                UniResult currRes = resultsMap.get(activity.getAddress().toLowerCase());
                                if (currRes != null) {
                                    activity.setResult(activityResults.get(currRes.getResult()));
                                    activity.setEndTimeFact(resultsMap.get(activity.getAddress().toLowerCase()).getDate());
                                    activity.setDescription(list.get(0).getName_ru());
                                } else {
                                    activWithNoResults.add(activity);
                                    //targetsWithoutActivities.remove(activity.getEmailCampaignTarget());
                                }
                            }
                        }
                        activitiesToCommit.addAll(activities);
                        targetsToCommit.addAll(targetsWithoutActivities);
                    }
                }
                communicationsToCommit.addAll(exportAddressStatuses(campaign));
            }
        }
        try {
            activitiesToCommit.removeAll(activWithNoResults);
            dataManager.commit(new CommitContext(activitiesToCommit));
        } catch (SecurityException e) {
            log.error(e.getLocalizedMessage());
        }
        dataManager.commit(new CommitContext(targetsToCommit));
        dataManager.commit(new CommitContext(communicationsToCommit));
    }

    public List<Communication> exportAddressStatuses(EmailCampaign campaign) {
        HashMap<String, UniContact> contacts = exportContacts(campaign.getList_id());
        List<Communication> toCommit = new ArrayList<Communication>();
        List<EmailActivity> activities = getCampaignActivities(campaign);
        for (EmailActivity activity : activities)
        {
            /* пока закомментировал !!!
            if (activity.getCommunication() != null) {  //(activity.getDeleteTs() == null) &&
                Communication item = activity.getCommunication();
                if (contacts.containsKey(activity.getAddress().toLowerCase())) {
                    item.setEmailStatus(contacts.get(activity.getAddress().toLowerCase()).getEmailStatus());
                }
                toCommit.add(item);
            }
            */
        }
        return toCommit;
        /*
        try {
            dataManager.commit(new CommitContext(toCommit));
        } catch (SecurityException e) {
            log.error(e.getLocalizedMessage());
        }*/
    }

    private HashMap<String, ActivityRes> getActivityRes() {
        LoadContext loadContext = new LoadContext(ActivityRes.class).setView("_local");
        loadContext.setQueryString("select e from crm$ActivityRes e");
        List<ActivityRes> listRes;
        listRes = dataManager.loadList(loadContext);
        HashMap<String, ActivityRes> resultMap = new HashMap<String, ActivityRes>();
        for (ActivityRes item : listRes)
        {
            resultMap.put(item.getCode(), item);
        }
        return resultMap;
    }

    private EmailActivity createActivitiesFromTargets(EmailCampaign campaign, EmailCampaignTarget targetItem) {
        EmailActivity messageActivity = metadata.create(EmailActivity.class);
        if (targetItem.getCommunication() != null)
        {
            messageActivity.setAddress(targetItem.getCommunication().getMainPart());
            //messageActivity.setCommunication(targetItem.getCommunication());
            if (targetItem.getCommunication().getContactPerson() != null)
            {
                messageActivity.setContactPerson(targetItem.getCommunication().getContactPerson());
            }
        }
        if (campaign != null) {
            messageActivity.setCampaign(campaign);
            if (campaign.getProject() != null) {messageActivity.setProject(campaign.getProject());}
        }
        //messageActivity.setEmailCampaignTarget(targetItem);
        if (targetItem.getCompany() != null) {
            messageActivity.setCompany(targetItem.getCompany());
        }
        messageActivity.setKind(config.getActivityKindEmail());
        messageActivity.setDirection(ActivityDirectionEnum.OUTBOUND);
        Organization org = configuration.getConfig(DefaultEntityConfig.class).getOrganizationDefault();
        messageActivity.setOrganization(org);
        return messageActivity;
    }

    public List<CampaignUnisender> getCampaignByList(String list_id) {
        LoadContext loadContext = new LoadContext(CampaignUnisender.class).setView("_local");
        loadContext.setQueryString("select e from crm$CampaignUnisender e where e.list_id = " + Integer.parseInt(list_id) + " order by e.start_time desc");
        return dataManager.loadList(loadContext);
    }

    protected List<EmailCampaignTarget> getCampaignTargets(EmailCampaign campaign) {
        LoadContext loadContext = new LoadContext(EmailCampaignTarget.class).setView("edit");
        loadContext.setQueryString("select e from crm$EmailCampaignTarget e where e.campaign.id = :campaign").setParameter("campaign", campaign);
        return dataManager.loadList(loadContext);
    }

    protected List<EmailActivity> getCampaignActivities(EmailCampaign campaign) {
        LoadContext loadContext = new LoadContext(EmailActivity.class).setView("for-email-targets");
        loadContext.setQueryString("select e from crm$EmailActivity e where e.emailCampaignTarget.campaign.id = :campaign").setParameter("campaign", campaign);
        return dataManager.loadList(loadContext);
    }
}