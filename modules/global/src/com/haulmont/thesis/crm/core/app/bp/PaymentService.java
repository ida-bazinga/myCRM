/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.crm.core.app.bp.efdata.Pay;
import com.haulmont.thesis.crm.core.app.bp.efdata.PayDetail;
import com.haulmont.thesis.crm.core.app.bp.efdata.PayList;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.entity.Currency;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.*;


/**
 * Created by d.ivanov on 19.05.2016.
 */
@ManagedBean(IPaymentService.NAME)
public class PaymentService implements IPaymentService {

    private Work1C work1C;
    protected Metadata metadata;
    private static PayList paymentList;
    protected Set<Entity> toCommit = new HashSet<>();
    protected Set<Entity> toRemovePaymentDetail = new HashSet<>();
    protected List<Object> objectArrayList =  new ArrayList<>();
    protected static Log log = LogFactory.getLog(PaymentService.class);


    @Inject
    private DataManager dataManager;


    public boolean getPayment() {
        toCommit.clear();
        toRemovePaymentDetail.clear();
        objectArrayList.clear();
        this.work1C = AppBeans.get(Work1C.class);
        this.metadata = AppBeans.get(Metadata.class);
        try {
           loadPayment();
           return doCommit();
        } catch (Exception e) {
            return false;
        }
    }

    private void loadPayment() {
        work1C.setExtSystem(ExtSystem.BP1CEFI);
        PaymentService.paymentList = work1C.getPayList();
        SavePayment();

        //work1C.setExtSystem(ExtSystem.BP1CNEVA);
        //PaymentService.paymentList = work1C.getPayList();
        //SavePayment();
    }


    private Payment getPayment(Pay pay) {
        Payment payment = metadata.create(Payment.class);
        IntegrationResolver integrationResolverPayment = getIRExtId(pay.getRef(),"crm$Payment");
        if (integrationResolverPayment != null && integrationResolverPayment.getEntityId() != null) {
            //обновить
            payment = getPayment(integrationResolverPayment.getEntityId());
            objectArrayList.add(integrationResolverPayment.getEntityId());
        }
        else {
            payment = checkCompany(payment, pay); //платежа в мостике нет
        }

        return payment;
    }

    private void SavePayment () {
        for (Pay row : paymentList.getListResult()) {
            Payment payment = getPayment(row);
            payment.setCode(row.getCode());
            payment.setOutboundDate(work1C.XMLGregorianCalendarToDate(row.getDate()));
            payment.setOperation(row.getTypeOperation());
            payment.setBankNumber(row.getBankNumber());
            payment.setBankDate(work1C.XMLGregorianCalendarToDate(row.getBankDate()));
            payment.setBankAccount(row.getBankAccount());
            payment.setDescription(row.getDescription());
            payment.setNote(row.getNote());
            payment.setFullSum(row.getSum());
            payment.setCurrency(getCurrency(row.getCurrencyCode()));
            payment.setPosted(row.isPosted());
            payment.setOrganization(row.getOrganization());
            payment.setBankOrganization(row.getBankOrganization());

            //если нет компании (нет связки с компанией, то не загружаем)
            if (payment.getCompany() != null) {
                toCommit.add(payment);
                payDetail(row.getPayDetail(),payment);
            }
        }
    }

    private Payment checkCompany(Payment payment, Pay pay) {
        IntegrationResolver irCompany = getIRExtId(pay.getRefAccount(), "crm$Company");
        if (irCompany != null) {
            createIRPayment(payment, pay.getRef());
            payment.setCompany(getCompany(irCompany.getEntityId()));
        }

        return payment;
    }

    private void createIRPayment(Payment payment, String ref) {
        IntegrationResolver ir = metadata.create(IntegrationResolver.class);
        ir.setEntityId(payment.getId());
        ir.setExtId(ref);
        ir.setEntityName(payment.getMetaClass().getName());
        ir.setStateDocSales(StatusDocSales.IN1C); //Отправлен в 1С
        ir.setExtSystem(work1C.getExtSystem());
        toCommit.add(ir);
    }

    private void findRemovePaymentDetail() {
        if(!objectArrayList.isEmpty()) {

            List<Object[]> chunksList = chunks(objectArrayList, 1000);

            for (Object[] object: chunksList) {
                LoadContext loadContext = new LoadContext(PaymentDetail.class).setView("1c");
                String txtSql = "select e from crm$PaymentDetail e where e.payment.id in (:searchString)";
                loadContext.setQueryString(txtSql).setParameter("searchString", object);
                List<PaymentDetail> payList = dataManager.loadList(loadContext);
                if (payList.size() > 0) {
                    toRemovePaymentDetail.addAll(payList);
                }
            }
        }
    }

    private void payDetail(List<PayDetail> paymentDetailList, Payment payment) {
        for (PayDetail rowdetail : paymentDetailList) {
            IntegrationResolver irInv = getIRExtId(rowdetail.getRefBasisDoc(), "crm$InvDoc");
            if (irInv != null) {
                InvDoc invDoc = getInvDoc(irInv.getEntityId());
                //если нет привязки к документу, то не загружаем
                if (invDoc != null) {
                    PaymentDetail paymentDetail = metadata.create(PaymentDetail.class);
                    paymentDetail.setCode(rowdetail.getCode());
                    paymentDetail.setPayment(payment);
                    paymentDetail.setOperation(rowdetail.getSDDS());
                    paymentDetail.setFullSum(rowdetail.getSum());
                    paymentDetail.setInvDoc(invDoc);
                    toCommit.add(paymentDetail);
                }
            }
        }
    }

    private boolean doCommit() {
        try{
            findRemovePaymentDetail(); //ищем строки платежа для удаления
            dataManager.commit(new CommitContext(toCommit, toRemovePaymentDetail));
            return true;
        }
        catch (Exception e) {
            log.error(String.format("PaymentService doCommit(): %s", e.getMessage()));
            return false;
        }
    }

    //поиск по extId
    private IntegrationResolver getIRExtId(String extId, String entityName) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("entityName", entityName);
        params.put("extId", extId);
        params.put("extSystem", work1C.getExtSystem());
        return work1C.buildQuery(IntegrationResolver.class, params);
    }

    private static <T> List<T[]> chunks(List<T> bigList, int n){
        List<T[]> chunks = new ArrayList<>();

        for (int i = 0; i < bigList.size(); i += n) {
            T[] chunk = (T[])bigList.subList(i, Math.min(bigList.size(), i + n)).toArray();
            chunks.add(chunk);
        }

        return chunks;
    }

    private Payment getPayment(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", Id);
        return work1C.buildQuery(Payment.class, params);
    }

    private ExtCompany getCompany(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", Id);
        return work1C.buildQuery(ExtCompany.class, params);
    }

    private Currency getCurrency(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("code", code);
        return work1C.buildQuery(Currency.class, params);
    }

    private InvDoc getInvDoc(UUID Id) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", Id);
        return work1C.buildQuery(InvDoc.class, params);
    }

}
