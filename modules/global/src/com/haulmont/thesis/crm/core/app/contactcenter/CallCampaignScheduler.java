package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.CallCampaign;

public interface CallCampaignScheduler  <T extends CallCampaign> {
    String NAME = "crm_CallCampaignScheduler";

    String startCampaign();

    String stopCampaign();

    String cleanCallTargets();
}
