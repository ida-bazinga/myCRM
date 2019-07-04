/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.app.Authenticated;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;

/**
 * Created by d.ivanov on 24.03.2017.
 */
@ManagedBean(INomenclatureGoTo1cMBean.NAME)
public class NomenclatureGoTo1c implements INomenclatureGoTo1cMBean {

    protected static Log log = LogFactory.getLog(NomenclatureGoTo1c.class);
    public String result = "Номенклатура добавлена на синхронизацию 1С!";

    @Authenticated
    public String goTo1CAllNomenclature() {

        Persistence persistence = AppBeans.get(Persistence.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            em.createNativeQuery("INSERT INTO CRM_LOG1_C\n" +
                    "([ID]\n" +
                    ",[CREATE_TS]\n" +
                    ",[CREATED_BY]\n" +
                    ",[ENTITY_ID]\n" +
                    ",[ENTITY_NAME]\n" +
                    ",[SHORT_SERVICE_OPERATION_RESULTS]\n" +
                    ",[EXT_ID]\n" +
                    ",[START_DATE]\n" +
                    ",[ERROR]\n" +
                    ",[PRIORITY]\n" +
                    ",[EXT_SYSTEM])\n" +
                    "SELECT \n" +
                    "NEWID() as [ID]\n" +
                    ",GETDATE() as [CREATE_TS]\n" +
                    ",'system' as [CREATED_BY]\n" +
                    ",N.ID as [ENTITY_ID]\n" +
                    ",'crm$Nomenclature' as [ENTITY_NAME]\n" +
                    ",1 as [SHORT_SERVICE_OPERATION_RESULTS]\n" +
                    ",NULL as [EXT_ID]\n" +
                    ",GETDATE() as [START_DATE]\n" +
                    ",NULL as [ERROR]\n" +
                    ",2 as [PRIORITY]\n" +
                    ",CASE WHEN 'A851BEAF-6890-4AB2-B847-B7A810C4C2B9' = N.ORGANIZATION_ID THEN 'bp1cefi' ELSE 'bp1cneva' END as  [EXT_SYSTEM]\n" +
                    "FROM CRM_NOMENCLATURE N\n" +
                    "LEFT JOIN CRM_INTEGRATION_RESOLVER I ON I.ENTITY_ID = N.ID and I.ENTITY_NAME = 'crm$Nomenclature'\n" +
                    "WHERE ISNULL(N.NOT_IN_USE, 0) != 1 and N.DELETE_TS IS NULL and I.ID IS NULL").executeUpdate();
            tx.commit();
            return result;
        } catch (Exception e) {
            result = String.format("buildQueryList goTo1CAllNomenclature: %s", e.getMessage());
            log.error(result);
            return result;
        }
        finally {
            tx.end();
        }

    }

}
