/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.thesis.crm.core.app.bp.efdata.Budget;
import com.haulmont.thesis.crm.core.app.bp.efdata.PayOrd;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.InvDocBugetDetail;
import com.haulmont.thesis.crm.entity.Log1C;
import com.haulmont.thesis.crm.entity.StatusDocSales;

import javax.annotation.ManagedBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @author d.ivanov
 */
@ManagedBean(IPayOrdService.NAME)
public class PayOrdService implements IPayOrdService {

    private static Log1C  log;
    private  com.haulmont.thesis.crm.entity.PayOrd row;
    private Work1C work1C;
    private DataManager dataManager;

    public boolean setPayOrdOne(Log1C log)
    {
        PayOrdService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        dataManager = AppBeans.get(DataManager.class);
        return one();
    }

    private boolean one()
    {
        try {
            //обращаемся к веб-сервису и выводим результат
            PayOrd payOrd = work1C.setPayOrd(xdto());
            String Id1C = payOrd.getRefPayOrd();
            //проверка и запись в маппинг
            work1C.setIntegrationResolver(log.getEntityId(), log.getEntityName(), StatusDocSales.IN1C, Id1C);
            //запись в лог
            log.setExtId(Id1C);
            work1C.setLog1c(log, false);
            //записываем номер док сгенеренного в 1с
            row.setNumber(payOrd.getNumber());
            dataManager.commit(new CommitContext(row));
            return true;
        }
        catch (Exception e) {
            log.setError(e.toString());
            work1C.setLog1c(log, true);
            return false;
        }
    }


    //заполняю данными XDTO веб-сервиса
    private PayOrd xdto()
    {
        // 1 - получаю данные строки сущности из тезиса
        row = getPayOrd();
        String ref = row.getIntegrationResolver().getExtId();

        // 2 - поиск соответсвий (маппинг)
        //************************
        String companyId = "";

        String bik_swift = work1C.validate(row.getCompanyAccount().getBik()) ? row.getCompanyAccount().getBik() : row.getCompanyAccount().getSwift();
        if (row.getCompany() != null) {
            companyId = getIntegrationResolver(row.getCompany().getId()).getExtId();
        }

        //************************
        PayOrd doc = new PayOrd();
        doc.setRefPayOrd(ref);
        doc.setRfType(work1C.getTypeTransaction_1c(row.getTypeTransaction()));
        doc.setDate(work1C.dateToXMLGregorianCalendar(row.getPaymentDateFact()));
        doc.setRefAccount(companyId);
        doc.setContract(""); //getContract() не нужно заполнять
        doc.setABIKSWIFT(bik_swift);
        doc.setANumInv(row.getCompanyAccount().getNo());
        doc.setCurrencyCode(row.getCurrency().getCode());
        doc.setTotalSum(row.getFullSum());
        doc.setRfNDS(work1C.getNDS_1c(row.getTax().getCode()));
        doc.setNDSSum(row.getSumTax());
        doc.setPaymentDestination(row.getPaymentDestination());
        doc.setIsDel(row.getIntegrationResolver().getDel());
        doc.setKBK(work1C.getNullToEmpty(row.getInvDoc().getKbk()));
        doc.setOKTMO(work1C.getNullToEmpty(row.getInvDoc().getOktmo()));

        for (InvDocBugetDetail invDocBugetDetail:getBudgetDetail(row.getParentCard().getId())) {
            Budget budget = new Budget();
            budget.setBDDS(invDocBugetDetail.getBugetItem().getCfcItem().getCode());
            budget.setCFO(invDocBugetDetail.getDepartment().getCode());
            budget.setSum(invDocBugetDetail.getFullSum());
            budget.setRefProject(getProject(invDocBugetDetail));
            doc.getBudget().add(budget);
        }

        return doc;
    }

    private String getProject(InvDocBugetDetail invDocBugetDetail) {
        if (invDocBugetDetail.getProject() != null) {
            IntegrationResolver integrationResolverProject = getIntegrationResolver(invDocBugetDetail.getProject().getId());
            if (work1C.validate(integrationResolverProject)) {
               return integrationResolverProject.getExtId();
            }
        }

        return "";
    }

    private List<InvDocBugetDetail> getBudgetDetail(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("invDoc.id", Id);
        return work1C.buildQueryList(com.haulmont.thesis.crm.entity.InvDocBugetDetail.class, params);
    }

    /*
    private String getContract() {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        StringBuilder builderContract = new StringBuilder();
        if (work1C.validate(row.getContract())) {
            builderContract.append(row.getContract().getNumber());
            builderContract.append("&");
            builderContract.append(formatDate.format(work1C.validate(row.getContract().getDate()) ? row.getContract().getDate() : row.getCreateTs()));
            builderContract.append("&");
            builderContract.append(work1C.getNullToEmpty(row.getContract().getDescription()) + " " + work1C.getNullToEmpty(row.getContract().getComment()));
        }
        return builderContract.toString();
    }
    */

    private IntegrationResolver getIntegrationResolver(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("entityId", Id);
        return work1C.buildQuery(IntegrationResolver.class, params);
    }

    private com.haulmont.thesis.crm.entity.PayOrd getPayOrd() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", log.getEntityId());
        return work1C.buildQuery(com.haulmont.thesis.crm.entity.PayOrd.class, params);
    }



}