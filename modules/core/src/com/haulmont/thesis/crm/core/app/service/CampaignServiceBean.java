/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.crm.core.app.worker.CampaignWorker;
import com.haulmont.thesis.crm.core.app.worker.EmailCampaignWorker;
import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.thesis.crm.entity.CampaignKind;
import com.haulmont.thesis.crm.entity.EmailCampaign;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 10.03.2017.
 */
@Service(CampaignService.NAME)
public class CampaignServiceBean implements CampaignService {
    private Log log = LogFactory.getLog(getClass());

    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CampaignWorker<CallCampaign> callCampaignWorker;
    @Inject
    protected EmailCampaignWorker<EmailCampaign> emailCampaignWorker;

    @Override
    public Long getCountWithSpecifiedKind(UUID kindId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createQuery("select count(a) from crm$BaseCampaign a join fetch a.kind where a.kind.id = :kindId");
            query.setParameter("kindId", kindId);
            Long count = (Long) query.getSingleResult();
            tx.commit();
            return count;
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return -1L;
        } finally {
            tx.end();
        }
    }

    @Override
    public boolean isSameKindExist(CampaignKind kind) {
        Query query;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("select k.name from crm$CampaignKind k where k.name = :kindName and k.entityType = :type and k.id <> :id");
            query.setParameter("id", kind.getId());
            query.setParameter("type", kind.getEntityType());
            query.setParameter("kindName", kind.getName());
            List result = query.getResultList();
            tx.commit();
            return !result.isEmpty();
        } finally {
            tx.end();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BaseCampaign> getWithKind(UUID kindId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            deleteLinkedCategoryAttributeValue(em, kindId);
            Query query = em.createQuery("select a from crm$BaseCampaign a join fetch a.kind where a.kind.id = :kindId");
            query.setView(metadata.getViewRepository().getView(BaseCampaign.class, "browse"));
            query.setParameter("kindId", kindId);
            List<BaseCampaign> result = query.getResultList();
            tx.commit();
            return result;
        } finally {
            tx.end();
        }
    }

    private void deleteLinkedCategoryAttributeValue(EntityManager em, UUID prevId) {
        String deleteQuery = "delete from sys$CategoryAttributeValue cav where exists (select a from crm$BaseCampaign a where a.kind.id = :kindId and cav.entityId = a.id)";
        Query updateQuery = em.createQuery(deleteQuery);
        updateQuery.setParameter("kindId", prevId);
        updateQuery.executeUpdate();
    }

    @Override
    public String createName(CallCampaign entity){
        return callCampaignWorker.createName(entity);
    }

    public String createName(EmailCampaign entity){
        return emailCampaignWorker.createName(entity);
    }
}
