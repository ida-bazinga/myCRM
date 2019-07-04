/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.infoUserOrdPayPaid;


import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.crm.core.app.infoUserNewOrders.InfoUserNewOrders;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(IInfoUserOrdPayPaidMBean.NAME)
public class InfoUserOrdPayPaid implements IInfoUserOrdPayPaidMBean {

    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;

    protected Log log = LogFactory.getLog(InfoUserNewOrders.class);
    protected EmailService emailService;

    public String send() {
        emailService = AppBeans.get(EmailService.class);        
        try {
            sendUser();
            return "Сообщение отправлено!";
        } catch (Exception e) {
            log.error(String.format("InfoUserOrdPayPaid.send: %s", e.getMessage()));
            return e.getMessage();
        }
    }

    protected List<String> emails(List<Object[]> docs) {
        List<String> emails = new ArrayList<>();
        for (Object[] o:docs) {
            String e = o[3].toString();
            if (!emails.contains(e)) {
                emails.add(e);
            }
        }
        return emails;
    }

    protected List docGroupEmail(List<Object[]> docs, String address) {
        List group = new ArrayList<>();
        for (Object[] o:docs) {
            if (address.equals(o[3])) {
                group.add(o);
            }
        }
        return group;
    }

    protected void sendUser(){
        try {
            List<Object[]> docs = sqlPayOrdPaid();
            List<String> emails = emails(docs);

            for (String address:emails) {
                List group = docGroupEmail(docs, address);
                if (group.size() > 0) {
                    Map<String, Object> result = buildMessage(group);
                    emailService.sendEmail(address, result.get("subject").toString(), result.get("body").toString(), new EmailAttachment[]{});
                }
            }

        } catch (EmailException e) {
            log.error(String.format("InfoUserOrdPayPaid.sendUser: %s", e.getMessages()));
        }
    }

    protected Map<String, Object> buildMessage(List docs) {
        try {
            groovy.lang.Binding binding = new groovy.lang.Binding();
            binding.setProperty("docs",docs);
            Scripting scripting = AppBeans.get(Scripting.NAME);
            Map<String, Object> result = scripting.runGroovyScript("com/haulmont/thesis/crm/core/scripts/InfoUserPayOrdPaid.groovy", binding);
            return result;
        } catch (Exception e) {
            log.error(String.format("InfoUserOrdPayPaid.buildMessage: %s", e.getMessage()));
        }
        return Collections.emptyMap();
    }

    protected List<Object[]> sqlPayOrdPaid() {
        try {

            Transaction tx = persistence.createTransaction();

            EntityManager em = persistence.getEntityManager();
            String sqlString = "{call getPayOrdPaid()}";
            Query query = em.createNativeQuery(sqlString);
            List<Object[]> docs = query.getResultList();

            tx.commit();
            return docs;
        } catch (Exception e) {
            log.error("InfoUserOrdPayPaid.sqlPayOrdPaid: " + e.getMessage());
            return null;
        }
    }
}
