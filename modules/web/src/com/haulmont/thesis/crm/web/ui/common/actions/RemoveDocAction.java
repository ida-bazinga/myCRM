/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.EntityLogItem;
import com.haulmont.thesis.crm.core.app.bp.IStatusService;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.workflow.core.entity.Card;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by d.ivanov on 07.04.2017.
 */
public class RemoveDocAction {

    protected DataManager dataManager;
    private Set<OrdDoc> ordDocSet;
    private Set<InvDoc> invDocSet;
    private Set<AcDoc> acDocSet;
    private int priority;
    public String txtMesage;
    protected Metadata metadata;
    protected UserSessionSource userSessionSource;
    protected Work1C work1C;
    protected IStatusService iStatusService;
    protected Date dateQuartal;
    protected Log log = LogFactory.getLog(RemoveDocAction.class);


    public RemoveDocAction () {
        this.dataManager = AppBeans.get(DataManager.class);
        this.metadata = AppBeans.get(Metadata.class);
        this.userSessionSource = AppBeans.get(UserSessionSource.class);
        this.txtMesage="";
        this.work1C = new Work1C();
        this.iStatusService = AppBeans.get(IStatusService.class);
        this.priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1;
        this.dateQuartal = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getDataQuartal();
    }

    public void removeOrd(Set<OrdDoc> doc) {
        this.ordDocSet = doc;
        isRemoveOrd();
    }

    public void removeInv(Set<InvDoc> doc) {
        this.invDocSet = doc;
        isRemoveInv();
    }

    public void removeAct(Set<AcDoc> doc) {
        this.acDocSet = doc;
        isRemoveAct();
    }

    private boolean isRemoveOrd() {
        for(Map.Entry<String, OrdDoc> pair : mapRemoveOrd().entrySet()){

            OrdDoc ord = pair.getValue();
            String key = pair.getKey();
            boolean isEmptyExtId = StringUtils.isEmpty(ord.getIntegrationResolver().getExtId());
            if (key.startsWith("ord")) {
                txtMesage = String.format("Заказ: %s помечен к удалению.", ord.getNumber());
                updateStatus(ord.getIntegrationResolver());
                delInv(ord.getId());
                delAc(ord.getId());
                if (!isEmptyExtId) {
                    //отправлен в 1С
                    newLog("crm$OrdDoc",ord.getId());
                }
            }
            infoMessage(key, true, ord.getNumber(), "Заказ");
        }
        return true;
    }

    private boolean isRemoveInv() {
        for(Map.Entry<String, InvDoc> pair : mapRemoveInv().entrySet()){

            InvDoc inv = pair.getValue();
            String key = pair.getKey();
            boolean isPeriod = isOpenPeriod(inv.getDate());
            boolean isEmptyExtId = StringUtils.isEmpty(inv.getIntegrationResolver().getExtId());
            boolean isExpense = inv.getDocKind().getCode().equalsIgnoreCase("ExpenseBill"); //расходный счет
            if (key.startsWith("inv") && isPeriod || isExpense) {
                txtMesage = String.format("Счет: %s помечен к удалению.", inv.getNumber());
                updateStatus(inv.getIntegrationResolver());
                if (!isExpense) {
                    delAc(inv.getId());
                    if (!isEmptyExtId) {
                        //отправлен в 1С
                        newLog("crm$InvDoc", inv.getId());
                    }
                }
            }
            infoMessage(key, isPeriod, inv.getNumber(), "Счет");
        }
        return true;
    }

    private boolean isRemoveAct() {
        for(Map.Entry<String, AcDoc> pair : mapRemoveAct().entrySet()){

            AcDoc ac = pair.getValue();
            String key = pair.getKey();
            boolean isPeriod = isOpenPeriod(ac.getDate());
            boolean isEmptyExtId = StringUtils.isEmpty(ac.getIntegrationResolver().getExtId());
            if (key.startsWith("ac") && isPeriod) {
                txtMesage = String.format("Акт: %s помечен к удалению.", ac.getNumber());
                updateStatus(ac.getIntegrationResolver());
                if (!isEmptyExtId) {
                    //отправлен в 1С
                    newLog("crm$AcDoc",ac.getId());
                }
            }
            infoMessage(key, isPeriod, ac.getNumber(), "Акт");
        }
        return true;
    }

    private void infoMessage (String key, boolean isPeriod, String num, String typeTxt) {
        if (key.startsWith("stateProc")) {
            txtMesage = txtMesage + String.format("Удалить %s: %s нельзя! У документа, есть не завершенный процесс! Завершите процесс \"Ознакомление\" и повторите удаление.", typeTxt, num);
        }
        if(!isPeriod) {
            txtMesage =  String.format("Удалить %s: %s нельзя! Период закрыт!", typeTxt, num);
        }
        if (key.startsWith("details")) {
            txtMesage = txtMesage + String.format("Удалить %s: %s нельзя! Отсутсвуют услуги!", typeTxt, num);
        }
        if (!txtMesage.isEmpty()) {
            txtMesage = txtMesage + System.lineSeparator();
        }
        if (key.startsWith("pay")) {
            txtMesage = txtMesage + String.format("Удалить %s: %s нельзя! Есть оплата!", typeTxt, num);
        }
        if (key.startsWith("postAc")) {
            txtMesage = txtMesage + String.format("Удалить %s: %s нельзя! Акт проведен в 1С!", typeTxt, num);
        }
    }

    private Map<String, OrdDoc> mapRemoveOrd() {
        Map<String, OrdDoc> map = new HashMap<>();
        int i = 0;
        for (OrdDoc ord:ordDocSet) {
            boolean isStateProc = ord.getState() != null ? "Acquaintance".equals(ord.getState()) ? true : false : false;
            if (!isStateProc) {
                if (isDocDetails(ord.getId(), "crm$OrderDetail") || StringUtils.isEmpty(ord.getIntegrationResolver().getExtId())) {
                    BigDecimal sumPay = ord.getIntegrationResolver().getSumPayment() != null ? ord.getIntegrationResolver().getSumPayment() : BigDecimal.valueOf(0);
                    if (sumPay.signum() == 1) {
                        map.put(String.format("pay%s", i), ord);
                    } else {
                        if (!isAct(ord.getId(), true)) {
                            //помечаем на удаление
                            map.put(String.format("ord%s", i), ord);
                        } else {
                            map.put(String.format("postAc%s", i), ord);
                        }
                    }
                } else {
                    map.put(String.format("details%s", i), ord);
                }
            } else {
                map.put(String.format("stateProc%s", i), ord);
            }
            i++;
        }
        return map;
    }

    private Map<String, InvDoc> mapRemoveInv() {
        Map<String, InvDoc> map = new HashMap<>();
        int i = 0;
        for (InvDoc inv:invDocSet) {
            if (isDocDetails(inv.getId(), "crm$InvoiceDetail") || StringUtils.isEmpty(inv.getIntegrationResolver().getExtId())) {
                BigDecimal sumPay = inv.getIntegrationResolver().getSumPayment() != null ? inv.getIntegrationResolver().getSumPayment() : BigDecimal.valueOf(0);
                if (sumPay.signum() == 1) {
                    map.put(String.format("pay%s", i), inv);
                } else {
                    if (!isAct(inv.getId(), true)) {
                        //помечаем на удаление
                        map.put(String.format("inv%s", i), inv);
                    } else {
                        map.put(String.format("postAc%s", i), inv);
                    }
                }
            } else {
                    map.put(String.format("details%s", i), inv);
            }
            i++;
        }
        return map;
    }

    private Map<String, AcDoc> mapRemoveAct() {
        Map<String, AcDoc> map = new HashMap<>();
        int i = 0;
        for (AcDoc ac:acDocSet) {
            if (isDocDetails(ac.getId(), "crm$ActDetail") || StringUtils.isEmpty(ac.getIntegrationResolver().getExtId())) {
                if (!isAct(ac.getId(), false)) {
                    //помечаем на удаление
                    map.put(String.format("ac%s", i), ac);
                } else {
                    map.put(String.format("postAc%s", i), ac);
                }
            } else {
                map.put(String.format("details%s", i), ac);
            }
            i++;
        }
        return map;
    }

    private boolean isAct(UUID Id, boolean isParentCard) {
        String cellsName = isParentCard ? "parentCard.id" : "id";
        LoadContext loadContext = new LoadContext(AcDoc.class).setView("edit");
        loadContext.setQueryString(String.format("select e from crm$AcDoc e where e.%s = :id", cellsName)).setParameter("id", Id);
        List<AcDoc> docList = dataManager.loadList(loadContext);
        if (docList.size() > 0) {
            for (AcDoc ac:docList) {
                if (ac.getIntegrationResolver() != null) {
                    if (!StringUtils.isEmpty(ac.getIntegrationResolver().getExtId())) {
                        boolean isPosted = ac.getIntegrationResolver().getPosted() != null ? ac.getIntegrationResolver().getPosted() : false;
                        if(!isPosted) {
                            boolean isStatus = updateStatusAct1c(ac.getIntegrationResolver());
                            if(isStatus) {
                                IntegrationResolver ir = getIntegrationResolver(ac.getIntegrationResolver().getId());
                                if(ir.getPosted()) {
                                    return true;
                                }
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean updateStatusAct1c(IntegrationResolver integrationResolver) {
        return this.iStatusService.getStatusOne(integrationResolver);
    }

    private IntegrationResolver getIntegrationResolver (UUID Id) {
        LoadContext loadContext = new LoadContext(IntegrationResolver.class).setView("_local");
        loadContext.setQueryString("select e from crm$IntegrationResolver e where e.id = :id").setParameter("id", Id);
        return dataManager.load(loadContext);
    }

    private void updateStatus(IntegrationResolver ir) {
        ir.setDel(true);
        ir.setStateDocSales(StatusDocSales.DELETE);
        dataManager.commit(ir);
        createEntityLogItem(ir.getEntityName(), ir.getEntityId()); //пишем в общий лог SEC_ENTITY_LOG
        updateCard(ir.getEntityId()); //WF_CARD
    }

    private void delInv(UUID Id) {
        LoadContext loadContext = new LoadContext(InvDoc.class)
                .setView("browse");
        loadContext.setQueryString("select e from crm$InvDoc e where e.parentCard.id = :id")
                .setParameter("id", Id);
        List<InvDoc> docList = dataManager.loadList(loadContext);
        for (InvDoc inv:docList) {
            updateStatus(inv.getIntegrationResolver());
            boolean isPeriod = isOpenPeriod(inv.getDate());
            if (!StringUtils.isEmpty(inv.getIntegrationResolver().getExtId()) && isPeriod) {
                newLog("crm$InvDoc",inv.getId());
            }
        }
    }

    private void delAc(UUID Id) {
        LoadContext loadContext = new LoadContext(AcDoc.class)
                .setView("browse");
        loadContext.setQueryString("select e from crm$AcDoc e where e.parentCard.id = :id")
                .setParameter("id", Id);
        List<AcDoc> docList = dataManager.loadList(loadContext);
        for (AcDoc ac:docList) {
            updateStatus(ac.getIntegrationResolver());
            boolean isPeriod = isOpenPeriod(ac.getDate());
            if (!StringUtils.isEmpty(ac.getIntegrationResolver().getExtId()) && isPeriod) {
                newLog("crm$AcDoc",ac.getId());
            }
        }
    }

    public boolean newLog(String entityName, UUID Id) {
        Log1C logNew = metadata.create(Log1C.class);
        logNew.setStartDate(java.util.Calendar.getInstance().getTime());
        logNew.setEntityId(Id);
        logNew.setEntityName(entityName);
        logNew.setShortServiceOperationResults(ServiceOperationResultsEnum.notwork);
        logNew.setPriority(this.priority);
        dataManager.commit(logNew);
        return true;
    }

    public Card getCard(UUID Id) {
        LoadContext loadContext = new LoadContext(Card.class).setView("_local");
        loadContext.setQueryString("select e from wf$Card e where e.id = :Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    private void createEntityLogItem (String entityName, UUID entityId) {
        EntityLogItem entityLogItem = metadata.create(EntityLogItem.class);
        entityLogItem.setUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        entityLogItem.setChanges(EntityLogItem.Type.DELETE.toString());
        entityLogItem.setType(EntityLogItem.Type.DELETE);
        entityLogItem.setEntity(entityName);
        entityLogItem.setEntityId(entityId);
        entityLogItem.setEventTs(java.util.Calendar.getInstance().getTime());
        dataManager.commit(entityLogItem);
    }

    private void updateCard(UUID Id) {
        Card card = getCard(Id);
        card.setState("Помечен на удаление");
        dataManager.commit(card);
    }

    private boolean isOpenPeriod(Date date) {
        try {
            if(date.compareTo(this.dateQuartal)==1) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

    }

    private boolean isDocDetails(UUID Id, String nameEntity) {
        String nameCells = "ordDoc";
        if("crm$InvoiceDetail".equals(nameEntity)) {
            nameCells = "invDoc";
        }
        if("crm$ActDetail".equals(nameEntity)) {
            nameCells = "acDoc";
        }
        String sql = String.format("select e from %s e where e.%s.id = :id", nameEntity, nameCells);
        Class c = this.metadata.getClass(nameEntity).getJavaClass();
        LoadContext loadContext = new LoadContext(c).setView("_minimal");
        loadContext.setQueryString(sql).setParameter("id", Id);
        List<Entity> docList = dataManager.loadList(loadContext);
        if (docList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

}
