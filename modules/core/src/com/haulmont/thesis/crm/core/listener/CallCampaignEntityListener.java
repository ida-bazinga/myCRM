package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.thesis.crm.core.app.worker.CampaignWorker;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_CallCampaignEntityListener")
public class CallCampaignEntityListener implements BeforeInsertEntityListener<CallCampaign>, BeforeUpdateEntityListener<CallCampaign> {

    @Inject
    protected Persistence persistence;
    @Inject
    protected CampaignWorker<CallCampaign> callCampaignWorker;

    @Override
    public void onBeforeInsert(CallCampaign entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);

/*        if (CollectionUtils.containsAny(fields, Arrays.asList("kind", "number", "date"))) {
            entity.setDescription(callCampaignWorker.createDescription(entity));
        }*/

        if (CollectionUtils.containsAny(fields, Arrays.asList("project", "kind", "number", "date"))) {
            String name =  callCampaignWorker.createName(entity);
            entity.setDescription(name);
            entity.setName(name);
        }
    }


    @Override
    public void onBeforeUpdate(CallCampaign entity) {
        onBeforeInsert(entity);
    }
}