/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.thesis.crm.core.app.bp.efdata.Doc;
import com.haulmont.thesis.crm.core.app.bp.efdata.ParamsGeneric;
import com.haulmont.thesis.crm.entity.ExtSystem;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.StatusDocSales;
import com.haulmont.workflow.core.entity.Card;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;


/**
 * Created by d.ivanov on 19.05.2016.
 */
@ManagedBean(IStatusService.NAME)
public class StatusService implements IStatusService {

    protected Set<Entity> toCommit = new HashSet<>();
    protected static Log log = LogFactory.getLog(StatusService.class);
    private Work1C work1C;
    protected String stateName;

    @Inject
    private DataManager dataManager;

    public boolean getStatusAll() {
        try {
            work1C = AppBeans.get(Work1C.class);
            toCommit.clear();
            this.stateName = work1C.getMassegePack(StatusDocSales.DELETE);

            boolean isLoad = true;
            List<ExtSystem> extSystemList = new ArrayList<>();
            extSystemList.add(ExtSystem.BP1CEFI);

            for (ExtSystem extSystem:extSystemList) {
                work1C.setExtSystem(extSystem);
                isLoad = loadStatus() & isLoad;
            }
            return isLoad;
        } catch (Exception e) {
            log.error(String.format("StatusService.getStatusAll Description of the error: %s", e.getMessage()));
            return false;
        }
    }

    public boolean getStatusOne(IntegrationResolver row) {
        work1C = AppBeans.get(Work1C.class);
        toCommit.clear();

        try {
            this.stateName = work1C.getMassegePack(StatusDocSales.DELETE);
            String tName;
            switch (row.getEntityName()) {
                case "crm$OrdDoc": tName = "ЗаявкаНаВыставку";
                    break;
                case "crm$InvDoc": tName = "СчетНаОплатуПокупателю";
                    break;
                case "crm$AcDoc": tName = "РеализацияТоваровУслуг";
                    break;
                default: tName = "";
                    break;
            }
            if (!"".equals(tName)) {
                for (Doc doc : oneDocList_1c(tName, row)) {
                    updateStateDocSales(row, doc);
                }
            }

            return doCommit();
        }
        catch (Exception e) {
            log.error(String.format("StatusService getStatusOne: %s", e.getMessage()));
            return false;
        }
    }

    private List<Doc> oneDocList_1c(String tName, IntegrationResolver row) {
        ParamsGeneric paramsGeneric = new ParamsGeneric();
        paramsGeneric.setTName(tName);
        paramsGeneric.setRef(row != null ? row.getExtId() : "");
        XMLGregorianCalendar dateXMLGregorianCalendar = work1C.dateToXMLGregorianCalendar(null);
        paramsGeneric.setSDate(dateXMLGregorianCalendar);
        paramsGeneric.setEDate(dateXMLGregorianCalendar);
        paramsGeneric.setIsDateFind(false);
        if (row != null) {
            work1C.setExtSystem(row.getExtSystem());
        }
        return work1C.getDocList(paramsGeneric).getListResult();
    }

    private boolean doCommit() {
        try{
            if (toCommit.size() > 0) {
                dataManager.commit(new CommitContext(toCommit));
            }
            return true;
        }
        catch (Exception e) {
            log.error(String.format("StatusService doCommit(): %s", e.getMessage()));
            return false;
        }
    }

    private void updateCardState(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "_local");
        params.put("id", Id);
        Card card = work1C.buildQuery(Card.class, params);
        if (card != null) {
            card.setState(this.stateName);
            toCommit.add(card);
        }
    }

    private List<IntegrationResolver> listIntegrationResolverDoc(String entityName) {
        String notWhere = String.format("e.entityName = '%s' and e.extId is not null and e.extSystem = '%s'", entityName, work1C.getExtSystem().getId());
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("not", notWhere);
        return work1C.buildQueryList(IntegrationResolver.class, params);
    }

    private String getEntityName(String name) {
        String entityName = "crm$OrdDoc";
        if ("СчетНаОплатуПокупателю".equals(name)) {
            entityName = "crm$InvDoc";
        }
        if ("РеализацияТоваровУслуг".equals(name)) {
            entityName = "crm$AcDoc";
        }
        return entityName;
    }


    private boolean loadStatus() {

        String[] docNameList = {"ЗаявкаНаВыставку", "СчетНаОплатуПокупателю", "РеализацияТоваровУслуг"};

        for (String tName : docNameList) {
            List<Doc> allDocList_1c = oneDocList_1c(tName, null);
            List<IntegrationResolver> integrationResolverList = listIntegrationResolverDoc(getEntityName(tName));

            if (allDocList_1c.size() > 0 && integrationResolverList.size() > 0) {
                for (IntegrationResolver row : integrationResolverList) {
                    Doc doc = getDoc1c(row, allDocList_1c);
                    if (doc != null) {
                        updateStateDocSales(row, doc);
                    }
                }
            }
        }

        return doCommit();
    }

    private void updateStateDocSales(IntegrationResolver row, Doc doc) {
        boolean isDel = doc.isIsDel();
        boolean isPosted = doc.isIsPosted();

        if (!isPosted && !isDel && row.getDel()) {
            return;
        }

        row.setPosted(isPosted);
        row.setDel(isDel);

        if(isDel) {
            row.setStateDocSales(StatusDocSales.DELETE);
            updateCardState(row.getEntityId());
        } else {
            row.setStateDocSales(StatusDocSales.IN1C);
        }
        toCommit.add(row);
    }

    private Doc getDoc1c(IntegrationResolver ir, List<Doc> docList_1c) {
        String extId = ir.getExtId();
        String entityName = ir.getEntityName();
        for (Doc doc : docList_1c) {
            if ("crm$OrdDoc".equals(entityName) && doc.getRefOrd() != null && doc.getRefOrd().equals(extId)) {
                return doc;
            }
            if ("crm$InvDoc".equals(entityName) && doc.getRefInv() != null && doc.getRefInv().equals(extId)) {
                return doc;
            }
            if ("crm$AcDoc".equals(entityName) && doc.getRefAct() != null && doc.getRefAct().equals(extId)) {
                return doc;
            }
        }
        return null;
    }

}
