package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.CallCampaignTrgt;

import java.util.List;

/**
 * @author Kirill Khoroshilov
 */
public interface CallCampaignTargetService {
    String NAME = "crm_CallCampaignTargetService";

    List<CallCampaignTrgt> getGroupSubTargets(CallCampaignTrgt group);

    List<CallCampaignTrgt> getGroupSubTargets(CallCampaignTrgt group, String viewName);
}