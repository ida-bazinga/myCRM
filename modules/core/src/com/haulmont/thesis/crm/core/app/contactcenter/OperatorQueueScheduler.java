/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.cuba.core.app.ClusterManagerAPI;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CallCampaignTarget;
import com.haulmont.thesis.crm.entity.CampaignTargetStatusEnum;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.crm.entity.Operator;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.*;

/**
 * @author k.khoroshilov
 */
@Deprecated
@ManagedBean(OperatorQueueSchedulerBean.NAME)
public class OperatorQueueScheduler implements OperatorQueueSchedulerBean {

    private int l_current = 0;
    private int l_max = 0;

    @Inject
    private DataManager dataManager;

    @Inject
    private ClusterManagerAPI clusterManager;

    @Inject
    private OperatorQueueService queueService;

    @Inject
    private CrmConfig config;

    private Log log = LogFactory.getLog(this.getClass());

    @Authenticated
    @Override
    public Boolean queueProcessing(){
        if (!AppContext.isStarted())
            return false;

        if (!clusterManager.isMaster())
            return false;

        try {
            log.info(String.format("Operators queue: %s operator(s)", queueService.getQueueSize()));
            if (queueService.getQueueSize() == 0) return true;

            Operator operator =  queueService.getFirstOperator();

            if (operator != null && operator.getEmployee() != null ) {
                log.debug(String.format("Get queue for %s", operator.toString()));

                l_max = config.getMaxOperatorQueue();
                l_current = getCountOperatorTargets(operator.getEmployee());

                if (l_max > l_current ) setTargetsForOperator(operator.getEmployee());
                queueService.removeOperator(operator);
            }
            return true;
        } catch (Throwable e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    private void setTargetsForOperator(final ExtEmployee operator) {
        List<CallCampaignTarget> targets = new ArrayList<>();
        targets.addAll(getTargetsListByOperatorAndDate(operator));
        targets.addAll(getTargetsListByDate(operator));
        targets.addAll(getTargetsListByOperator(operator));
        targets.addAll(getTargetsListByNulls(operator));
        Collections.sort(targets, new Comparator<CallCampaignTarget>() {
            public int compare(CallCampaignTarget o1, CallCampaignTarget o2) {
                if (o1.getNextCallDate() == null) return 1;
                if (o2.getNextCallDate() == null) return -1;
                int sort1 = o1.getNextCallDate().compareTo(o2.getNextCallDate());
                if (sort1 == 0){
                    if (o1.getNextCallOperator() == null) return 1;
                    if (o2.getNextCallOperator() == null) return -1;
                    return (o1.getNextCallOperator().equals(operator)) ? 1 : 0;
                }
                return sort1;
            }
        });

        //int current_l = getCountOperatorTargets(operator);
        if (targets.size() == 0) return;

        //10% от котла ореатора& в компаниях где опертор назначен
        final int L = targets.size();
        final double k = config.getOperatorQueueMultiplier();
        final int l_min = config.getMinOperatorQueue();


        int l = (L*k > l_min) ? l_max: (int) Math.ceil(L * k);

        if (l_current >= l) return;
        if (l > l_min && l_min > l_current) l = l_max-l_current;

        for (int i = 0; i < l; i++) {
            targets.get(i).setNextCallOperator(operator);
            targets.get(i).setStatus(CampaignTargetStatusEnum.PLANED);
        }

        CommitContext commitContext = new CommitContext(targets);
        dataManager.commit(commitContext);
    }

    private List<CallCampaignTarget> getTargetsListByOperatorAndDate(ExtEmployee operator) {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("setOperatorService");
        loadContext.setQueryString(
                                    "select t from crm$CallCampaignTarget t join t.outboundCampaign c join c.operators o " +
                                    "where o.employee.id = :operator and t.nextCallOperator.id = :operator " +
                                    "and c.status.code = :cStatus and t.status = :tStatus " +
                                    "and t.nextCallDate <= CURRENT_DATE order by t.nextCallDate"
                )
                .setParameter("tStatus", CampaignTargetStatusEnum.PLANED)
                .setParameter("cStatus", "inProgress")
                .setParameter("operator", operator)
                .setMaxResults(100);

        return dataManager.loadList(loadContext);
    }

    private List<CallCampaignTarget> getTargetsListByDate(ExtEmployee operator) {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("setOperatorService");
        loadContext.setQueryString(
                                    "select t from crm$CallCampaignTarget t join t.outboundCampaign c join c.operators o " +
                                    "where o.employee.id = :operator and t.nextCallOperator is null " +
                                    "and c.status.code = :cStatus and (t.status  is null or t.status = :tStatus) " +
                                    "and t.nextCallDate <= CURRENT_DATE order by t.nextCallDate"
                )
                .setParameter("tStatus", CampaignTargetStatusEnum.PLANED)
                .setParameter("cStatus", "inProgress")
                .setParameter("operator", operator)
                .setMaxResults(100);

        return dataManager.loadList(loadContext);
    }

    private List<CallCampaignTarget> getTargetsListByOperator(ExtEmployee operator) {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("setOperatorService");
        loadContext.setQueryString(
                        "select t from crm$CallCampaignTarget t join t.outboundCampaign c join c.operators o " +
                        "where o.employee.id = :operator and t.nextCallOperator.id = :operator " +
                        "and c.status.code = :cStatus and (t.status  is null or t.status <> :tStatus) " +
                        "and t.nextCallDate is null"
                )
                .setParameter("tStatus", CampaignTargetStatusEnum.COMPLETED)
                .setParameter("cStatus", "inProgress")
                .setParameter("operator", operator)
                .setMaxResults(100);

        return dataManager.loadList(loadContext);
    }
    private List<CallCampaignTarget> getTargetsListByNulls(ExtEmployee operator) {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("setOperatorService");
        loadContext.setQueryString(
                        "select t from crm$CallCampaignTarget t join t.outboundCampaign c join c.operators o " +
                        "where o.employee.id = :operator and t.nextCallOperator is null " +
                        "and c.status.code = :cStatus and t.status  is null and t.nextCallDate is null " +
                        "order by t.numberOfTries"
                 )
                .setParameter("cStatus", "inProgress")
                .setParameter("operator", operator)
                .setMaxResults(100);

        return dataManager.loadList(loadContext);
    }

    private int getCountOperatorTargets(ExtEmployee operator) {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("setOperatorService");
        loadContext.setQueryString("select count(t) from crm$CallCampaignTarget t join t.outboundCampaign c join c.operators o " +
                "where t.nextCallOperator.id = :operator and c.status.code = :cStatus and (t.status  is null or t.status <> :tStatus)")
                .setParameter("tStatus", CampaignTargetStatusEnum.COMPLETED)
                .setParameter("cStatus", "inProgress")
                .setParameter("operator", operator);
        Object num = dataManager.loadList(loadContext).get(0);
        return Integer.parseInt(num.toString());
    }
}