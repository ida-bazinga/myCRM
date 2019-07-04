package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_CallCampaignTargetEntityListener")
public class CallCampaignTargetEntityListener implements BeforeDeleteEntityListener<CallCampaignTrgt> {

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeDelete(CallCampaignTrgt entity) {
        if (!entity.getIsGroup() && !(getCountGroupTarget(entity) > 0)){
            EntityManager em = persistence.getEntityManager();
            em.remove(entity.getParent());
        }
    }

    protected int getCountGroupTarget(CallCampaignTrgt target) {
        EntityManager em = persistence.getEntityManager();
        Object count = em.createQuery(
                "select count(t) from crm$CallCampaignTrgt t where t.isGroup = false and t.parent.id = :group and t.id <> :entityId"
                ).setParameter("group", target.getParent().getId())
                .setParameter("entityId", target.getId())
                .getSingleResult();
        return Integer.parseInt(count.toString());
    }
}