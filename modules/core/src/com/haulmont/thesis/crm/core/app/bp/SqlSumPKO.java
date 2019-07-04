/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by d.ivanov on 05.06.2017.
 */
public class SqlSumPKO {

    private Log log = LogFactory.getLog(SqlSumPKO.class);
    protected List<Object> result;

    SqlSumPKO() {
        Persistence persistence = AppBeans.get(Persistence.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("SELECT \n" +
                    "CONVERT(varchar(10), cd.RECEIPT_CREATION_DATE_TIME, 104) DATE_TIME,\n" +
                    "c.PROJECT_ID PROJECT_ID, \n" +
                    "SUM(cdp.RESULT_SUM) TOTAL_SUM, \n" +
                    "SUM(cdp.RESULT_TAX_SUM) TOTAL_SUM_NDS,\n" +
                    "MIN(t.CODE) TAX_CODE, \n" +
                    "MIN(cd.DOC_TYPE) DOC_TYPE\n" +
                    "FROM CRM_CASH_DOCUMENT cd\n" +
                    "INNER JOIN CRM_CASH_DOCUMENT_POSITION cdp ON cdp.CASH_DOCUMENT_ID = cd.ID\n" +
                    "LEFT JOIN CRM_COST c ON c.ID = cdp.COST_ID\n" +
                    "INNER JOIN CRM_TAX t ON t.ID = cdp.TAX_ID\n" +
                    "WHERE cd.ID NOT IN (SELECT ENTITY_ID FROM CRM_INTEGRATION_RESOLVER WHERE ENTITY_NAME = 'crm$PKO')\n" +
                    "GROUP BY c.PROJECT_ID, CONVERT(varchar(10), cd.RECEIPT_CREATION_DATE_TIME, 104)"
            );
            result = query.getResultList();
            tx.commit();
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }

    }
}
