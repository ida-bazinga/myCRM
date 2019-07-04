package com.haulmont.thesis.crm.core.listener;

import com.google.common.collect.Lists;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import com.haulmont.thesis.crm.entity.CommKind;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_CommunicationEntityListener")
public class CommunicationEntityListener implements BeforeDeleteEntityListener<Communication>, BeforeInsertEntityListener<Communication>, BeforeUpdateEntityListener<Communication> {

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeDelete(Communication entity) {
        EntityManager em = persistence.getEntityManager();
        for(CallCampaignTrgt trgt : getTargets(entity.getId())){
            em.remove(trgt);
        }
    }

    protected List<CallCampaignTrgt> getTargets(UUID communicationId) {
        EntityManager em = persistence.getEntityManager();
        TypedQuery<CallCampaignTrgt> query = em.createQuery(
                "select t from crm$CallCampaignTrgt t where t.isGroup = false  and t.communication.id = :commId", CallCampaignTrgt.class
                ).setParameter("commId", communicationId);
                //.setParameter("states", Lists.newArrayList(ActivityStateEnum.LOCKED.getId(), ActivityStateEnum.SCHEDULED.getId()));
        return query.getResultList();
    }

    @Override
    public void onBeforeInsert(Communication entity) {
        CommKind commKind = entity.getCommKind();
        if (entity.getMainPart() != null && commKind != null && commKind.getCommunicationType().equals(CommunicationTypeEnum.phone)){
            entity.setMainPart(entity.getMainPart().replaceAll("\\D", ""));
        }
    }

    @Override
    public void onBeforeUpdate(Communication entity) {
        onBeforeInsert(entity);
    }
}