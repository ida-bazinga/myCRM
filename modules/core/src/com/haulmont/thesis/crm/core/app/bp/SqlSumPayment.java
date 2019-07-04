/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Created by d.ivanov on 21.02.2017.
 */
public class SqlSumPayment {

    protected static Log log = LogFactory.getLog(SqlSumPayment.class);
    public Boolean result = false;

    SqlSumPayment()
    {
        Persistence persistence = AppBeans.get(Persistence.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            em.createNativeQuery("UPDATE I\n" +
                    "SET I.SUM_PAYMENT = (N1.FULL_SUM - ISNULL(N2.FULL_SUM, 0))\n" +
                    "FROM CRM_INTEGRATION_RESOLVER I\n" +
                    "INNER JOIN (SELECT N.FULL_SUM, N.INTEGRATION_RESOLVER_ID\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT\n" +
                    "\t\tSUM(pd.FULL_SUM) as FULL_SUM\n" +
                    "\t\t,i.INTEGRATION_RESOLVER_ID as INTEGRATION_RESOLVER_ID\t\n" +
                    "\t\tFROM CRM_PAYMENT p\n" +
                    "\t\tINNER JOIN CRM_PAYMENT_DETAIL pd ON pd.PAYMENT_ID = p.ID\n" +
                    "\t\tINNER JOIN CRM_INV_DOC i ON i.CARD_ID = pd.INV_DOC_ID\n" +
                    "\t\tWHERE p.OPERATION IN ('Оплата от покупателя', 'Поступление по платежным картам') \n" +
                    "\t\tGROUP BY i.INTEGRATION_RESOLVER_ID\n" +
                    "\n" +
                    "\t\tUNION ALL\n" +
                    "\n" +
                    "\t\tSELECT\n" +
                    "\t\tSUM(pd.FULL_SUM) as FULL_SUM\n" +
                    "\t\t,o.INTEGRATION_RESOLVER_ID as INTEGRATION_RESOLVER_ID\t\n" +
                    "\t\tFROM CRM_PAYMENT p\n" +
                    "\t\tINNER JOIN CRM_PAYMENT_DETAIL pd ON pd.PAYMENT_ID = p.ID\n" +
                    "\t\tINNER JOIN CRM_INV_DOC i ON i.CARD_ID = pd.INV_DOC_ID\n" +
                    "\t\tINNER JOIN WF_CARD c ON c.ID = i.CARD_ID\n" +
                    "\t\tINNER JOIN CRM_ORD_DOC o ON o.CARD_ID = c.PARENT_CARD_ID\n" +
                    "\t\tWHERE p.OPERATION IN ('Оплата от покупателя', 'Поступление по платежным картам') \n" +
                    "\t\tGROUP BY o.INTEGRATION_RESOLVER_ID\n" +
                    "\t)N\n" +
                    ")N1 ON N1.INTEGRATION_RESOLVER_ID = I.ID\n" +
                    "LEFT JOIN (SELECT N.FULL_SUM, N.INTEGRATION_RESOLVER_ID\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT\n" +
                    "\t\tSUM(pd.FULL_SUM) as FULL_SUM\n" +
                    "\t\t,i.INTEGRATION_RESOLVER_ID as INTEGRATION_RESOLVER_ID\t\n" +
                    "\t\tFROM CRM_PAYMENT p\n" +
                    "\t\tINNER JOIN CRM_PAYMENT_DETAIL pd ON pd.PAYMENT_ID = p.ID\n" +
                    "\t\tINNER JOIN CRM_INV_DOC i ON i.CARD_ID = pd.INV_DOC_ID\n" +
                    "\t\tWHERE p.OPERATION = 'Возврат покупателю' \n" +
                    "\t\tGROUP BY i.INTEGRATION_RESOLVER_ID\n" +
                    "\n" +
                    "\t\tUNION ALL\n" +
                    "\n" +
                    "\t\tSELECT\n" +
                    "\t\tSUM(pd.FULL_SUM) as FULL_SUM\n" +
                    "\t\t,o.INTEGRATION_RESOLVER_ID as INTEGRATION_RESOLVER_ID\t\n" +
                    "\t\tFROM CRM_PAYMENT p\n" +
                    "\t\tINNER JOIN CRM_PAYMENT_DETAIL pd ON pd.PAYMENT_ID = p.ID\n" +
                    "\t\tINNER JOIN CRM_INV_DOC i ON i.CARD_ID = pd.INV_DOC_ID\n" +
                    "\t\tINNER JOIN WF_CARD c ON c.ID = i.CARD_ID\n" +
                    "\t\tINNER JOIN CRM_ORD_DOC o ON o.CARD_ID = c.PARENT_CARD_ID\n" +
                    "\t\tWHERE p.OPERATION = 'Возврат покупателю' \n" +
                    "\t\tGROUP BY o.INTEGRATION_RESOLVER_ID\n" +
                    "\t)N\n" +
                    ")N2 ON N2.INTEGRATION_RESOLVER_ID = I.ID").executeUpdate();
            tx.commit();
            result = true;
        } catch (Exception e) {
            log.error(String.format("SqlSumPayment: %s", e.getMessage()));
        }
         finally {
            tx.end();
        }
    }
}
