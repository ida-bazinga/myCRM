/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.core.app
 */
package com.haulmont.thesis.crm.core.app;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.thesis.core.app.TaskWorker;
import com.haulmont.thesis.core.entity.Task;
import com.haulmont.thesis.core.entity.TaskPattern;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.entity.ExtTask;
import com.haulmont.workflow.core.app.WfEngineAPI;
import com.haulmont.workflow.core.entity.CardAttachment;
import com.haulmont.workflow.core.entity.CardRole;
import com.haulmont.workflow.core.entity.Proc;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author d.ivanov
 */
@Service(MyUtilsService.NAME)
public class MyUtilsServiceBean implements MyUtilsService {

    private Log log = LogFactory.getLog(MyUtilsService.NAME);
    protected TaskWorker taskWorker;
    protected WfEngineAPI wfEngineAPI;
    protected UserSessionSource userSessionSource;

    @Inject
    protected Persistence persistence;

    /*
        "SELECT ISNULL(c.ID, c1.ID) ID\n" +
        "FROM CRM_PRODUCT p\n" +
        "LEFT JOIN (SELECT MAX(ID) ID, PRODUCT_ID FROM CRM_COST WHERE START_DATE <= GETDATE() AND DELETE_TS IS NULL\n" +
        "AND PROJECT_ID=?2 GROUP BY PRODUCT_ID) c ON c.PRODUCT_ID = p.ID\n" +
        "LEFT JOIN (SELECT MAX(ID) ID, PRODUCT_ID FROM CRM_COST WHERE START_DATE <= GETDATE() AND DELETE_TS IS NULL\n" +
        "AND PROJECT_ID IS NULL GROUP BY PRODUCT_ID) c1 ON c1.PRODUCT_ID = p.ID\n" +
        "AND (p.EXHIBIT_SPACE_ID=?1 OR p.EXHIBIT_SPACE_ID IS NULL)\n" +
        "WHERE p.DELETE_TS IS NULL AND (c.ID IS NOT NULL OR c1.ID IS NOT NULL)\n" +
        "AND ((p.PRODUCT_TYPE_ID = 'FB5AF259-F7D1-CFB6-9EEF-4503C50CE73D' AND 1=?3)\n" +
        "OR (p.PRODUCT_TYPE_ID!='FB5AF259-F7D1-CFB6-9EEF-4503C50CE73D' AND 0=?3))"
     */

    public List<Object> getCostList(UUID spaceId, UUID projectId, UUID organizationId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
            "SELECT ISNULL(cx.ID, c1x.ID) ID\n" +
                    "FROM CRM_PRODUCT p\n" +
                    "INNER JOIN CRM_NOMENCLATURE n ON ISNULL(n.NOT_IN_USE, 0) = 0 AND n.ID = p.NOMENCLATURE_ID AND n.ORGANIZATION_ID=?3\n" +
                    "LEFT JOIN (SELECT PRODUCT_ID, MAX(START_DATE) START_DATE FROM CRM_COST WHERE START_DATE <= GETDATE() AND DELETE_TS IS NULL\n" +
                    "AND PROJECT_ID=?2 GROUP BY PRODUCT_ID) c ON c.PRODUCT_ID = p.ID\n" +
                    "LEFT JOIN (SELECT ID, PRODUCT_ID, START_DATE FROM CRM_COST WHERE START_DATE <= GETDATE() AND DELETE_TS IS NULL\n" +
                    "AND PROJECT_ID=?2) cx ON cx.PRODUCT_ID = c.PRODUCT_ID AND cx.START_DATE=c.START_DATE\n" +
                    "LEFT JOIN (SELECT PRODUCT_ID, MAX(START_DATE) START_DATE FROM CRM_COST WHERE START_DATE <= GETDATE() AND DELETE_TS IS NULL\n" +
                    "AND PROJECT_ID IS NULL GROUP BY PRODUCT_ID) c1 ON c1.PRODUCT_ID = p.ID\n" +
                    "AND (p.EXHIBIT_SPACE_ID=?1 OR p.EXHIBIT_SPACE_ID IS NULL)\n" +
                    "LEFT JOIN (SELECT ID, PRODUCT_ID, START_DATE FROM CRM_COST WHERE START_DATE <= GETDATE() AND DELETE_TS IS NULL\n" +
                    "AND PROJECT_ID IS NULL) c1x ON c1x.PRODUCT_ID = c1.PRODUCT_ID AND c1x.START_DATE=c1.START_DATE\n" +
                    "AND (p.EXHIBIT_SPACE_ID=?1 OR p.EXHIBIT_SPACE_ID IS NULL)\n" +
                    "WHERE p.DELETE_TS IS NULL AND (cx.ID IS NOT NULL OR c1x.ID IS NOT NULL)\n" +
                    "AND ISNULL(p.NOT_IN_USE, 0) = 0"
            ).setParameter(1, spaceId).setParameter(2, projectId).setParameter(3, organizationId);
            List<Object> result = query.getResultList();
            tx.commit();
            return result;

        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }

    }

    public boolean updateCompanyTxtSearch(UUID companyId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            em.createNativeQuery(
                    "UPDATE c\n" +
                            "SET c.TXTSEARCH = X.TXTSEARCH\n" +
                            "FROM DF_COMPANY c\n" +
                            "INNER JOIN (\n" +
                            "\tSELECT \n" +
                            "\tc.CONTRACTOR_ID,\n" +
                            "\tSUBSTRING(\n" +
                            "\tISNULL(cr.INN, '')  +'/'+\n" +
                            "\tISNULL(REPLACE(cor.NAME, '-', ''), '') +'/'+\n" +
                            "\tISNULL(REPLACE(c.FULL_NAME, '-', ''), '') +'/'+\n" +
                            "\tRTRIM(ISNULL(N.MAIN_PART, '')) +'/'+\n" +
                            "\tISNULL(c.WEB_ADDRESS, '') +'/'+\n" +
                            "\tISNULL(al.NAME_RU, '') +'/'+\n" +
                            "\tISNULL(af.NAME_RU, '')\n" +
                            "\t,1,8000) TXTSEARCH\n" +
                            "\tFROM DF_COMPANY c\n" +
                            "\tINNER JOIN DF_CONTRACTOR cr ON cr.CORRESPONDENT_ID = c.CONTRACTOR_ID\n" +
                            "\tINNER JOIN DF_CORRESPONDENT cor ON cor.ID = c.CONTRACTOR_ID\n" +
                            "\tLEFT JOIN CRM_ADDRESS al ON al.ID = c.EXT_LEGAL_ADDRESS_ID\n" +
                            "\tLEFT JOIN CRM_ADDRESS af ON af.ID = c.EXT_FACT_ADDRESS_ID\n" +
                            "\tLEFT JOIN (\n" +
                            "\tselect distinct Z2.[CONTRACTOR_ID],\n" +
                            "\t\tSTUFF((select '/'+CAST(Z1.[MAIN_PART]  AS VARCHAR(400))\n" +
                            "\t\tfrom (\n" +
                            "\t\t\tSELECT DISTINCT T1.[COMPANY_ID], N.MAIN_PART\n" +
                            "\t\t\tFROM DF_CONTACT_PERSON T1\n" +
                            "\t\t\tINNER JOIN (\n" +
                            "\t\t\tselect distinct [CONTACT_PERSON_ID] ,\n" +
                            "\t\t\tSTUFF((Select '/'+CAST([MAIN_PART]  AS VARCHAR(400))\n" +
                            "\t\t\tfrom CRM_COMMUNICATION T1\n" +
                            "\t\t\twhere T1.[CONTACT_PERSON_ID]=T2.[CONTACT_PERSON_ID]\n" +
                            "\t\t\tFOR XML PATH('')),1,1,'') as [MAIN_PART] from CRM_COMMUNICATION T2\n" +
                            "\t\t\t) N ON N.CONTACT_PERSON_ID = T1.ID\n" +
                            "\t\t)Z1\n" +
                            "\t\twhere Z1.[COMPANY_ID]=Z2.[CONTRACTOR_ID]\n" +
                            "\t\tFOR XML PATH('')),1,1,'') as [MAIN_PART] \t\n" +
                            "\tfrom DF_COMPANY Z2\n" +
                            "\t)N ON N.[CONTRACTOR_ID] = c.CONTRACTOR_ID\n" +
                            ")X ON X.CONTRACTOR_ID = c.CONTRACTOR_ID\n" +
                            "WHERE c.CONTRACTOR_ID = ?1"
            ).setParameter(1, companyId).executeUpdate();
            tx.commit();
            return true;

        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return false;
        }

    }

    public int getActCount(UUID ordId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT COUNT(CARD_ID) num FROM CRM_AC_DOC a\n" +
                            "INNER JOIN WF_CARD c ON c.ID = a.CARD_ID\n" +
                            "WHERE c.PARENT_CARD_ID = ?1 OR c.PARENT_CARD_ID IN (\n" +
                            "SELECT I.CARD_ID FROM CRM_INV_DOC i\n" +
                            "INNER JOIN WF_CARD c ON c.ID = i.CARD_ID\n" +
                            "WHERE c.PARENT_CARD_ID = ?1)"
            ).setParameter(1, ordId);
            int result = (int)query.getResultList().get(0);
            tx.commit();
            return result;

        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return 0;
        }
    }

    public boolean createTaskgScheduleCallDate(Map<String, Object> params) {
        try {
            this.taskWorker = AppBeans.get(TaskWorker.class);
            this.wfEngineAPI = AppBeans.get(WfEngineAPI.class);
            this.userSessionSource = AppBeans.get(UserSessionSource.class);
            TaskPattern taskPattern = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getTaskScheduleCallDate();
            Task task = createTaskFromPattern(taskPattern, params);
            startProcess(task);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }

        return true;
    }

    protected Task createTaskFromPattern(TaskPattern pattern, Map<String, Object> params) {
        Transaction tx = persistence.createTransaction();
        ExtTask newTask;
        try {
            ExtProject project = (ExtProject) params.get("project");
            ExtCompany company = (ExtCompany) params.get("company");
            Date scheduleCallDate = (Date) params.get("scheduleCallDate");
            String details = params.get("details") != null ? params.get("details").toString() : "";
            EntityManager em = persistence.getEntityManager();
            newTask = (ExtTask) taskWorker.cloneTask(pattern, true, true, true);
            newTask.setCreateDate(java.util.Calendar.getInstance().getTime());
            newTask.setCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
            newTask.setTaskName(String.format("%s %s", pattern.getTaskName(), company.getName()));
            newTask.setFullDescr(newTask.getTaskName() + System.lineSeparator() + details);
            newTask.setProject(project);
            newTask.setExtCompany(company);
            newTask.setFinishDatePlan(scheduleCallDate);
            newTask.setFinishDateTimePlan(scheduleCallDate);

            for (CardRole cr : newTask.getRoles()) {
                if ("10-Initiator".equals(cr.getCode()) || "20-Executor".equals(cr.getCode())) {
                    cr.setUser(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
                }
                em.persist(cr);
            }

            for (CardAttachment c : newTask.getAttachments()) {
                em.persist(c);
            }

            Proc taskProc = taskWorker.getDefaultProc();
            newTask.setProc(taskProc);

            em.persist(newTask);

            tx.commit();
        } finally {
            tx.end();
        }
        return newTask;
    }

    protected void startProcess(Task task) {
        Transaction tx = persistence.createTransaction();
        try {
            wfEngineAPI.startProcess(task);
            tx.commit();
        } finally {
            tx.end();
        }
    }

    public List<Object[]> getProductMap20() {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT * FROM _PNDS_20_18"
            );
            List<Object[]> result = query.getResultList();
            tx.commit();
            return result;

        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }

    }

}


