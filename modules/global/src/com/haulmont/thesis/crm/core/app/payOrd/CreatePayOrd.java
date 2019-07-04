/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.payOrd;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.OrganizationAccount;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.workflow.core.entity.Card;
import com.haulmont.workflow.core.entity.CardAttachment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@ManagedBean(ICreatePayOrdMBean.NAME)
public class CreatePayOrd implements  ICreatePayOrdMBean {

    @Inject
    private DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    private CrmConfig config;


    protected Set<Entity> toCommit = new HashSet<>();
    protected Log log = LogFactory.getLog(PayOrd.class);
    protected InvDoc invDoc;
    protected PayOrd payOrd;


    public boolean create(UUID invId) {
        try {
            toCommit.clear();
            invDoc = getInvDoc(invId);
            if (invDoc != null) {
                payOrd = metadata.create(PayOrd.class);
                boolean isCreate = newPayOrd();
                if (isCreate) {
                    copyAttachment();
                    doCommit();
                }
            }
            return true;
        } catch (Exception e) {
            log.error(String.format("CreatePayOrd.create: %s", e.getMessage()));
            return false;
        }
    }

    protected InvDoc getInvDoc(UUID Id) {
        LoadContext loadContext = new LoadContext(InvDoc.class).setView("edit");
        loadContext.setQueryString("select e from crm$InvDoc e where e.id = :Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    protected boolean newPayOrd() {
        try {
            payOrd.setCategory(config.getPayOrdCategory());
            payOrd.setParentCard(invDoc);
            payOrd.setInvDoc(invDoc);
            payOrd.setDescription(invDoc.getComment());
            payOrd.setHasAttachments(false);
            payOrd.setHasAttributes(false);
            payOrd.setParentCardAccess(true);
            payOrd.setState("");
            payOrd.setTypeTransaction(invDoc.getTypeTransaction());
            payOrd.setNumber("new");
            payOrd.setCompany(invDoc.getCompany());
            payOrd.setOrganization(invDoc.getOrganization());
            payOrd.setCurrency(invDoc.getCurrency());
            payOrd.setCompanyAccount(invDoc.getCompanyAccount());
            Card card = invDoc.getParentCard();
            if (card != null && "df$Contract".equals(card.getCategory().getEntityType())) {
                payOrd.setContract((Contract)card);
            }
            payOrd.setOrganizationAccount(getOrganizationAccount(invDoc));
            payOrd.setFullSum(invDoc.getFullSum());
            payOrd.setTax(invDoc.getTax());
            payOrd.setSumTax(invDoc.getTaxSum());
            payOrd.setPaymentDestination(invDoc.getPaymentDestination());
            payOrd.setPaymentDateFact(invDoc.getPaymentDate());
            payOrd.setIntegrationResolver(createIntegrationResolver());
            toCommit.add(payOrd);
            return true;
        } catch (Exception e) {
            log.error(String.format("CreatePayOrd.newPayOrd: %s", e.getMessage()));
            return false;
        }
    }

    private boolean doCommit() {
        try{
            if (toCommit.size() > 0) {
                dataManager.commit(new CommitContext(toCommit));
            }
            return true;
        }
        catch (javax.persistence.PersistenceException e) {
            log.error(String.format("CreatePayOrd doCommit(): %s", e.getMessage()));
            return false;
        }
    }

    protected OrganizationAccount getOrganizationAccount(InvDoc invDoc) {
        String invValCode = invDoc.getCurrency().getCode();
        if (invDoc.getOrganization() != null) {
            for (OrganizationAccount account:invDoc.getOrganization().getAccounts()) {
               String  accValCode = account.getCurrency().getDigitalCode();
               if (invValCode.equals(accValCode)) {
                  return account;
               }
            }
        }
        return null;
    }

    protected IntegrationResolver createIntegrationResolver() {
        IntegrationResolver integrationResolver = metadata.create(IntegrationResolver.class);
        integrationResolver.setEntityId(payOrd.getId()); //*
        integrationResolver.setEntityName(payOrd.getMetaClass().getName()); //*
        integrationResolver.setStateDocSales(StatusDocSales.NEW); //*
        integrationResolver.setPosted(false); //*
        integrationResolver.setDel(false); //*
        integrationResolver.setExtSystem(ExtSystem.BP1CEFI);
        toCommit.add(integrationResolver);
        return integrationResolver;
    }

    protected void copyAttachment() {
        List<CardAttachment> invAttachments = getCardAttachment();
        for (CardAttachment invAttachment: invAttachments) {
            CardAttachment payOrdAttachment = metadata.create(CardAttachment.class);
            payOrdAttachment.setCard(payOrd);
            payOrdAttachment.setName(invAttachment.getName());
            payOrdAttachment.setAttachType(invAttachment.getAttachType());
            payOrdAttachment.setFile(invAttachment.getFile());
            payOrdAttachment.setSubstitutedCreator(invAttachment.getSubstitutedCreator());
            payOrdAttachment.setVersionNum(invAttachment.getVersionNum());
            payOrdAttachment.setVersionOf(invAttachment.getVersionOf());
            toCommit.add(payOrdAttachment);
        }
    }

    protected List<CardAttachment> getCardAttachment() {
        LoadContext loadContext = new LoadContext(CardAttachment.class).setView("card-edit");
        loadContext.setQueryString("select a from wf$CardAttachment a where a.card.id = :cardId")
                .setParameter("cardId", invDoc.getId());
        return dataManager.loadList(loadContext);
    }

}
