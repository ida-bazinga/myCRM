package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Kirill Khoroshilov
 */
@Service(CallCampaignTargetService.NAME)
public class CallCampaignTargetServiceBean implements CallCampaignTargetService {

    @Inject
    protected CallCampaignTargetManager callCampaignTargetManager;

    @Override
    public List<CallCampaignTrgt> getGroupSubTargets(CallCampaignTrgt group) {
        return callCampaignTargetManager.getGroupSubTargets(group, null);
    }

    @Override
    public List<CallCampaignTrgt> getGroupSubTargets(CallCampaignTrgt group, String viewName) {
        return callCampaignTargetManager.getGroupSubTargets(group, viewName);
    }
}