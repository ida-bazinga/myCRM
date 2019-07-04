/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.thesis.crm.core.app.bp.efdata.ListParamStatus;
import com.haulmont.thesis.crm.core.app.bp.efdata.ParamStatus;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.*;


/**
 * Created by d.ivanov on 19.05.2016.
 */
@ManagedBean(IPayOrdStatusService.NAME)
public class PayOrdStatusService implements IPayOrdStatusService {

    protected Set<Entity> toCommit = new HashSet<>();
    protected static Log log = LogFactory.getLog(PayOrdStatusService.class);
    private Work1C work1C;
    protected List<IntegrationResolver> integrationResolverList = new ArrayList<>();


    @Inject
    private DataManager dataManager;

    public boolean updatePayOrdStatus() {
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(ExtSystem.BP1CEFI);
        toCommit.clear();
        integrationResolverList.clear();
        String statusDocSalesList[] = {StatusDocSales.IN1C.getId(), StatusDocSales.SHIP.getId(), StatusDocSales.PREPARE.getId()};
        for (String statusDocSales:statusDocSalesList) {
            this.integrationResolverList.addAll(getIntegrationResolverList(statusDocSales));
        }
        if (this.integrationResolverList.size() > 0) {
            return SaveStatus();
        } else {
            return true;
        }
    }

    private boolean SaveStatus() {
        try {
            ListParamStatus listParamStatus = xdto();
            if (work1C.validate(listParamStatus, listParamStatus.getListResult())) {
               ListParamStatus result = work1C.getStatusList(listParamStatus);
                for (ParamStatus status: result.getListResult()) {
                    IntegrationResolver ir = getIntgrationResolver(status.getRef());
                    if (isAddCommit(ir, status)) {
                        PayOrd payOrd = getPayOrd(ir.getEntityId());
                        isAddCommit(getInvIR(payOrd), status);
                    }
                }
            }
            return doCommit();
        } catch (Exception e) {
            log.error(String.format("PayOrdStatusService SaveStatus(): %s", e.getMessage()));
            return false;
        }
    }

    private PayOrd getPayOrd(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", Id);
        return work1C.buildQuery(PayOrd.class, params);
    }

    private IntegrationResolver getInvIR(PayOrd payOrd) {
        Map<String, Object> params = new HashMap<>();
        UUID parentId = payOrd.getParentCard().getId();
        params.put("view", "1c");
        params.put("id", parentId);
        List<InvDoc> invDocList = work1C.buildQueryList(InvDoc.class, params);
        if (invDocList.size() > 0) {
            return invDocList.get(0).getIntegrationResolver();
        }
        return null;
    }

    private StatusDocSales getStatusDocSales(String s) {
        if ("Оплачено".equals(s)) {
            return StatusDocSales.PAID;
        }
        if("Отклонено".equals(s)) {
            return StatusDocSales.REFUSED;
        }
        if("Отправлено".equals(s)) {
            return StatusDocSales.SHIP;
        }
        if("Подготовлено".equals(s)){
            return StatusDocSales.PREPARE;
        }
        return StatusDocSales.NEW;
    }

    private IntegrationResolver getIntgrationResolver(String ref) {
        for (IntegrationResolver ir:this.integrationResolverList) {
            if (ref.equals(ir.getExtId())) {
                return ir;
            }
        }
        return null;
    }

    private List<IntegrationResolver> getIntegrationResolverList(String statusDocSales) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("entityName", "crm$PayOrd");
        params.put("stateDocSales", statusDocSales);
        params.put("extSystem", ExtSystem.BP1CEFI);
        params.put("del", false);
        return work1C.buildQueryList(IntegrationResolver.class, params);
    }

    private ListParamStatus xdto () {

        String tName = "ПлатежноеПоручение";
        ListParamStatus listParamStatus = new ListParamStatus();
        for (IntegrationResolver integrationResolver:this.integrationResolverList) {
            ParamStatus paramStatus = new ParamStatus();
            paramStatus.setTName(tName);
            paramStatus.setRef(integrationResolver.getExtId());
            paramStatus.setIsPosted(false);
            paramStatus.setIsDel(false);
            paramStatus.setStatus("");
            listParamStatus.getListResult().add(paramStatus);
        }
        return listParamStatus;
    }

    private boolean doCommit() {
        try{
            if (toCommit.size() > 0) {
                dataManager.commit(new CommitContext(toCommit));
            }
            return true;
        }
        catch (Exception e) {
            log.error(String.format("PayOrdStatusService doCommit(): %s", e.getMessage()));
            return false;
        }
    }

    protected boolean isAddCommit(IntegrationResolver ir, ParamStatus status) {
        if (work1C.validate(ir)) {
            boolean posted = status.isIsPosted();
            boolean del = status.isIsDel();
            StatusDocSales s = getStatusDocSales(status.getStatus());
            ir.setPosted(posted);
            ir.setDel(del);
            if (!s.equals(StatusDocSales.NEW)) {
                ir.setStateDocSales(s);
            }
            toCommit.add(ir);
            return true;
        }
        return false;
    }


}
