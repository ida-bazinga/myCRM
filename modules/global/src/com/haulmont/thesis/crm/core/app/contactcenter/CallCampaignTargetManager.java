package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.CallCampaignTrgt;

import javax.annotation.Nonnull;
import java.util.List;

public interface CallCampaignTargetManager {
    String NAME = "crm_CallCampaignTargetManager";

    List<CallCampaignTrgt> getGroupSubTargets(@Nonnull CallCampaignTrgt group, String viewName);

    Boolean queueProcessing();
}
