/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.unisender;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.thesis.crm.core.app.UnisenderMailingService;
import com.haulmont.thesis.crm.core.app.unisender.entity.UniContact;
import com.haulmont.thesis.crm.core.app.unisender.entity.UniResult;
import com.haulmont.thesis.crm.entity.CampaignUnisender;
import com.haulmont.thesis.crm.entity.MessageCampaignUnisender;
import com.haulmont.thesis.crm.entity.MessageUnisender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a.donskoy on 09.11.2017.
 */
@ManagedBean(UnisenderTools.NAME)
public class UnisenderTools implements UnisenderToolsMBean {
    public static final String NAME = "crm_UnisenderTools";

    protected UnisenderMailingService mailingService;

    @Inject
    private DataManager dataManager;

    Log log = LogFactory.getLog(getClass());

    public ArrayList<MessageCampaignUnisender> getLists(){
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.getLists();
        }
        catch (Exception e) {

        }
        return null;
    };

    public ArrayList<CampaignUnisender> getCampaigns(){
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.getCampaigns();
        }
        catch (Exception e) {

        }
        return null;
    };

    public String getListFromMessage(String messageId){
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.getListFromMessage(messageId);
        }
        catch (Exception e) {

        }
        return null;
    };

    public ArrayList<MessageUnisender> getMessages(){
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.getMessages();
        }
        catch (Exception e) {

        }
        return null;
    };

    public int importContacts(String uniCampaignId, ArrayList<String> addresses) {
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.importContacts(uniCampaignId, addresses);
        }
        catch (Exception e) {

        }
        return 0;
    }

    public HashMap<String, UniContact> exportContacts(String listId) {
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.exportContacts(listId);
        }
        catch (Exception e) {

        }
        return null;
    }

    public HashMap<String, UniResult> getCampaignMessagesStatuses(String campaign) {
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        try {
            return mailingService.getMessagesStatuses(campaign);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void getAllCampaignStatuses() {  //Daily task to re-fetch all non-expired Campaigns' Statuses
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        mailingService.getAllCampaignStatusesTask();
    }

    public void getMessagesStatuses() { //Daily task to re-fetch all non-expired campaigns' Activity Results
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        mailingService.getMessagesStatusesTask();
    }

    public void refreshMessagesUnisender() {
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        mailingService.refreshMessagesUnisenderTask();
    }

    public void getMessagesStatusesTask () {
        mailingService = AppBeans.get(UnisenderMailingService.class);
        mailingService.initUnisender();
        mailingService.getMessagesStatusesTask();
    }


}

