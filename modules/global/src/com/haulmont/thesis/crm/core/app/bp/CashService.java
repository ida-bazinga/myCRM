/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.crm.core.app.bp.efdata.Cash;
import com.haulmont.thesis.crm.entity.CashDocument;
import com.haulmont.thesis.crm.entity.ExtSystem;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.StatusDocSales;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by d.ivanov on 05.06.2017.
 */
@ManagedBean(ICashService.NAME)
public class CashService implements ICashService {

    protected static Log log = LogFactory.getLog(CashService.class);
    private static String entityNameProject = "crm$Project";
    private String extId;
    private Date receiptCreationDateTime;
    protected Set<Entity> toCommit = new HashSet<>();
    private Work1C work1C;

    @Inject
    private DataManager dataManager;

    public boolean setCashService(List<Object> cashList) {
        toCommit.clear();
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(ExtSystem.BP1CEFI);
        for (Object object:cashList) {
            Object[] objects = (Object[]) object;
            if (objects[1] != null) {
                Cash cash = xdto(objects);
                if (cash != null) {
                    this.extId = "";
                    this.extId = work1C.setCash(cash);
                    if(!this.extId.isEmpty()) {
                        UUID projectId = UUID.fromString(objects[1].toString());
                        sel(projectId);
                    }
                }
            }
        }
        doCommit();
        return true;
    }

    private Date sToDate(String sDate, String dFormat) {
        try {
            return new SimpleDateFormat(dFormat).parse(sDate);
        }
        catch (Exception e) {
            return java.util.Calendar.getInstance().getTime();
        }
    }

    private Cash xdto(Object[] values) {
        Cash cash = new Cash();
        String sDate = values[0].toString();
        this.receiptCreationDateTime = sToDate(sDate, "dd.MM.yyyy");
        UUID projectId = values[1] != null ? UUID.fromString(values[1].toString()) : UUID.fromString("00000000-0000-0000-0000-000000000000");
        BigDecimal sum = values[2] != null ? new BigDecimal(values[2].toString()).setScale(2,BigDecimal.ROUND_HALF_UP) : BigDecimal.valueOf(0);
        BigDecimal sumNDS = values[3] != null ? new BigDecimal(values[3].toString()).setScale(2,BigDecimal.ROUND_HALF_UP) : BigDecimal.valueOf(0);
        String nds = values[4] != null ? work1C.getNDS_1c(values[4].toString()) : "НДС18";
        String type = values[5] != null ? values[5].toString() : "";
        IntegrationResolver integrationResolverProject = work1C.getIntegrationResolverEntityId(projectId, entityNameProject);
        String refProject = (integrationResolverProject != null) ? integrationResolverProject.getExtId() : "";
        if (!refProject.isEmpty()) {
            XMLGregorianCalendar dataNew = work1C.dateToXMLGregorianCalendar(this.receiptCreationDateTime);
            dataNew.setHour(22);
            cash.setDate(dataNew);
            cash.setRefProject(refProject);
            cash.setTotalSum(sum);
            cash.setSumNDS(sumNDS);
            cash.setRfNDS(nds);
            cash.setRfType("ОплатаПокупателя");
            return cash;
        } else {
            return null;
        }
    }

    private void sel(UUID projectId) {
        GregorianCalendar cal = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        cal.setTime(this.receiptCreationDateTime);
        cal.add(Calendar.DATE, 1);
        String sDate1 = df.format(this.receiptCreationDateTime);
        String sDate2 = df.format(cal.getTime());
        LoadContext loadContext = new LoadContext(CashDocument.class).setView("browse");
        String txtSql = String.format("select e from crm$CashDocumentPosition p join p.cashDocument e " +
                "where p.cost.project.id = :projectId and e.receiptCreationDateTime between '%s' and '%s' and " +
                "e.id not in (select i.entityId from crm$IntegrationResolver i where i.entityName='crm$PKO')", sDate1, sDate2);
        loadContext.setQueryString(txtSql).setParameter("projectId", projectId);
        List<CashDocument> cashDocumentList = dataManager.loadList(loadContext);
        if (cashDocumentList.size() > 0) {
            createIntegrationResolver(cashDocumentList);
        }
    }

    private boolean createIntegrationResolver(List<CashDocument> cashDocumentList) {
        for (CashDocument cash:cashDocumentList) {
            IntegrationResolver integrationResolver = new IntegrationResolver();
            integrationResolver.setEntityId(cash.getId());
            integrationResolver.setExtId(this.extId);
            integrationResolver.setEntityName("crm$PKO");
            integrationResolver.setStateDocSales(StatusDocSales.NEW);
            toCommit.add(integrationResolver);
        }
        return true;
    }

    private void doCommit() {
        try {
            if (toCommit.size() > 0) {
                dataManager.commit(new CommitContext(toCommit));
                toCommit.clear();
            }
        } catch (Exception e) {
            log.error(String.format("CashService_doCommit: %s", e.getMessage()));
        }
    }


}
