/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.infoUserNewOrders;


import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(IInfoUserNewOrdersMBean.NAME)
public class InfoUserNewOrders implements IInfoUserNewOrdersMBean {

    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;


    protected Log log = LogFactory.getLog(InfoUserNewOrders.class);
    protected EmailService emailService;
    protected String sqlParam;

    public String send(String address, String sqlParam) {
        emailService = AppBeans.get(EmailService.class);
        this.sqlParam = !sqlParam.isEmpty() ? sqlParam : null;
        try {
             sendNewOrders(address);
             return "Сообщение отправлено!";
        } catch (Exception e) {
            log.error(String.format("InfoUserNewOrders.send: %s", e.getMessage()));
            return e.getMessage();
        }
    }

    protected void sendNewOrders(String address){
        try {
            Map<String, Object> result = buildMessage();
            emailService.sendEmail(address, result.get("subject").toString(), result.get("body").toString(), new EmailAttachment[]{});
        } catch (EmailException e) {
            log.error(String.format("InfoUserNewOrders.sendNewOrders: %s", e.getMessages()));
        }
    }

    protected Map<String, Object> buildMessage() {
        try {
            List docs = sqlOrds();
            groovy.lang.Binding binding = new groovy.lang.Binding();
            binding.setProperty("docs",docs);
            Scripting scripting = AppBeans.get(Scripting.NAME);
            Map<String, Object> result = scripting.runGroovyScript("com/haulmont/thesis/crm/core/scripts/InfoUserNewOrders.groovy", binding);
            return result;
        } catch (Exception e) {
            log.error(String.format("InfoUserNewOrders.buildMessage: %s", e.getMessage()));
        }
        return Collections.emptyMap();
    }

    protected List sqlOrds() {
        try {

            Transaction tx = persistence.createTransaction();

            EntityManager em = persistence.getEntityManager();
            String sqlString = "{call getWeekOrd(?1)}";
            Query query = em.createNativeQuery(sqlString);
            query.setParameter(1, this.sqlParam);
            List docs = query.getResultList();

            tx.commit();
            return docs;
        } catch (Exception e) {
            log.error("InfoUserNewOrders.sqlOrds: " + e.getMessage());
            return null;
        }
    }

}