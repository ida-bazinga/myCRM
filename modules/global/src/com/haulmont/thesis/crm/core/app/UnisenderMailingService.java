/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app
 */
package com.haulmont.thesis.crm.core.app;

import com.haulmont.thesis.crm.core.app.unisender.entity.UniContact;
import com.haulmont.thesis.crm.core.app.unisender.entity.UniResult;
import com.haulmont.thesis.crm.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author a.donskoy
 */
public interface UnisenderMailingService {
    String NAME = "crm_UnisenderMailingService";
    String USER_AGENT = "Chrome";
    void initUnisender();
    //void refreshUnisenderData();
    int createList(String title) throws Exception;
    int importContacts(String uniCampaignId, ArrayList<String> addresses) throws Exception;
    ArrayList<CampaignUnisender> getCampaigns() throws Exception;

    ArrayList<MessageCampaignUnisender> getLists() throws Exception;
    ArrayList<MessageUnisender> getMessages() throws Exception;
    HashMap<String, UniContact> exportContacts(String listId);

    HashMap<String, UniResult> getMessagesStatuses(String campaignId);
    String getListFromMessage(String messageId);
    HashMap<String, MessageCampaignStatus> getAllCampaignStatuses();
    List<MessageUnisender> getAllMessagesUnisender();

    void refreshMessagesUnisenderTask();
    void refreshCampaignsUnisenderTask();
    void getMessagesStatusesTask();
    void getAllCampaignStatusesTask();
}