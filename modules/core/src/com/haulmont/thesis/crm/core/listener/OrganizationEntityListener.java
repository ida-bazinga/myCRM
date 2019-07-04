/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.crm.core.app.worker.CampaignWorker;
import com.haulmont.thesis.crm.entity.CallCampaign;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import com.haulmont.thesis.core.entity.Organization;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_OrganizationEntityListener")
public class OrganizationEntityListener implements AfterUpdateEntityListener<Organization> {

    @Inject
    protected Persistence persistence;
    @Inject
    DataManager dataManager;
    @Inject
    protected CampaignWorker<CallCampaign> callCampaignWorker;

    @Override
    public void onAfterUpdate(Organization entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);
        if (fields.contains("name")) {
            updateCampaign(entity.getId(), entity.getName());
        }
    }

    protected void updateCampaign(UUID entityId, String newName){
        LoadContext context = new LoadContext(CallCampaign.class)
                .setView("browse");

        context.setQueryString("select e from crm$CallCampaign e where e.organization.id = :orgId")
                .setParameter("orgId", entityId);
        List<CallCampaign> campaigns = dataManager.loadList(context);

        Set<CallCampaign> toCommit = new HashSet<>();
        if (campaigns != null && !campaigns.isEmpty()){
            for(CallCampaign campaign : campaigns){
                campaign.setDescription(
                        callCampaignWorker.createDescription(campaign.getKind().getInstanceName(), newName, campaign.getNumber(), campaign.getDate())
                );
                campaign.setName(
                        callCampaignWorker.createName(
                                campaign.getProject() != null ? campaign.getProject().getInstanceName() : "",
                                campaign.getKind().getInstanceName(),
                                newName,
                                campaign.getNumber(),
                                campaign.getDate()
                        )
                );
                toCommit.add(campaign);
            }
        }
        if (!toCommit.isEmpty()){
            dataManager.commit(new CommitContext(toCommit));
        }
    }
}