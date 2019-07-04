package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.ClusterManagerAPI;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import com.haulmont.thesis.crm.enums.CampaignState;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;

@Component(CallCampaignTargetManager.NAME)
public class CallCampaignTargetManagerBean implements CallCampaignTargetManager {
    private Log log = LogFactory.getLog(getClass());

    private int l_current = 0;
    private int l_max = 0;

    @Inject
    protected Persistence persistence;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CrmConfig config;
    @Inject
    protected ClusterManagerAPI clusterManager;
    @Inject
    protected CallCampaignOperatorQueueService queueService;

    @Override
    public List<CallCampaignTrgt> getGroupSubTargets(@Nonnull CallCampaignTrgt group, String viewName) {
        viewName = StringUtils.defaultIfBlank(viewName, View.MINIMAL);

        LoadContext loadContext = new LoadContext(CallCampaignTrgt.class)
                .setView(viewName);
        loadContext.setQueryString("select t from crm$CallCampaignTrgt t where t.isGroup = false and t.parent.id = group.id")
                .setParameter("group", group);
        return dataManager.loadList(loadContext);
    }

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

            ExtEmployee operator = queueService.getFirstOperator();

            if (operator != null) {
                log.debug(String.format("Get queue for %s", operator.getInstanceName()));

                l_max = config.getMaxOperatorQueue();
                l_current = getOperatorTargetsCount(operator.getId());
                if (l_current < 0) {
                    log.error("Calculate error count targets for operator");
                    return false;
                }
                if (l_max > l_current) assignTargetsToOperator(operator);
                //log.debug(String.format("Remove from queue %s", operator.getInstanceName()));
                //queueService.removeOperator(operator);
            }
            return true;
        } catch (Throwable e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    protected void assignTargetsToOperator(ExtEmployee operator) {
        if (operator == null){
            log.error("Error assign targets on operator is null.");
            return;
        }

        final List<CallCampaignTrgt> targets = new LinkedList<>();

        int iterator = 1;
        do {
            if(iterator == 1) targets.addAll(getTargetsByOperatorAndDate(operator.getId()));
            if(iterator == 2) targets.addAll(getTargetsByDate(operator.getId()));
            if(iterator == 3) targets.addAll(getTargetsByOperator(operator.getId()));
            if(iterator == 4) targets.addAll(getTargets(operator.getId()));
            iterator++;
        } while (iterator < 5 && targets.size() < config.getMaxOperatorQueue());


        int queueLen = calcOperatorQueueLength(operator.getId());
        if (targets.isEmpty()){
            log.info(String.format("No targets for operator %s", operator.getInstanceName()));
            return;
        }

        int iMax= queueLen < targets.size() ? queueLen : targets.size();

        for (int i = 0; i < iMax; i++) {
            targets.get(i).setOperator(operator);
            targets.get(i).setState(ActivityStateEnum.SCHEDULED);
        }

        CommitContext commitContext = new CommitContext(targets);
        dataManager.commit(commitContext);
    }

    /*
     * 10% от котла ореатора в компаниях где этот опертор назначен
     */
    protected int calcOperatorQueueLength(final UUID operatorId){
        final int L = getTargetsForOperatorCount(operatorId);
        if (L < 1) return 0;

        final double k = config.getOperatorQueueMultiplier();
        final int l_min = config.getMinOperatorQueue();

        int calc =(L*k > l_min) ? l_max : (int) Math.ceil(L * k);

        if (l_current >= calc) return 0;

        if (calc > l_min && l_min > l_current) return l_max-l_current;

        return calc;
    }

    protected List<CallCampaignTrgt> getTargetsByOperatorAndDate(UUID operatorId){
        String queryString = "select t from crm$CallCampaignTrgt t join t.nextActivity a join t.campaign c join c.operators o " +
                "where t.isGroup = true and o.id = :operatorId and a.owner.id = :operatorId and c.state = :cState and t.state = :tState " +
                "and a.state = t.state and a.endTimePlan <= CURRENT_DATE order by a.endTimePlan";

        Map<String, Object> params = new HashMap<>();
        params.put("tState", ActivityStateEnum.SCHEDULED);
        params.put("cState", CampaignState.InWork);
        params.put("operatorId", operatorId);

        return getTargetsFromDB(queryString, params);
    }

    protected List<CallCampaignTrgt> getTargetsByDate(UUID operatorId){
        String queryString = "select t from crm$CallCampaignTrgt t join t.nextActivity a join t.campaign c join c.operators o " +
                "where t.isGroup = true and o.id = :operatorId and a.owner is null and c.state = :cState and a.state = :tState " +
                "and a.endTimePlan <= CURRENT_DATE order by a.endTimePlan";

        Map<String, Object> params = new HashMap<>();
        params.put("tState", ActivityStateEnum.SCHEDULED);
        params.put("cState", CampaignState.InWork);
        params.put("operatorId", operatorId);

        return getTargetsFromDB(queryString, params);
    }

    protected List<CallCampaignTrgt> getTargetsByOperator(UUID operatorId){
/*
         String queryString = "select t from crm$CallCampaignTrgt t join t.nextActivity a join t.campaign c join c.operators o " +
                 "where t.isGroup = true and o.id = :operatorId and a.owner.id is null and c.state = :cState " +
                 "and (t.state is null or t.state <> :tState) and (a.state is null or a.state <> t.state) " +
                 "and a.endTimePlan is null order by a.endTimePlan";

                .setParameter("tState", ActivityStateEnum.COMPLETED)
                .setParameter("cState", CampaignState.InWork)
                .setParameter("operatorId", operatorId)
                .setMaxResults(l_max);

        Map<String, Object> params = new HashMap<>();
        params.put("tState", ActivityStateEnum.COMPLETED);
        params.put("cState", CampaignState.InWork);
        params.put("operatorId", operatorId);

        return getTargetsFromDB(queryString, params);
 */
        return new ArrayList<>();
    }

    protected List<CallCampaignTrgt> getTargets(UUID operatorId){
        String queryString = "select t from crm$CallCampaignTrgt t join t.campaign c join c.operators o " +
                "where t.isGroup = true and o.id = :operatorId and t.nextActivity is null and c.state = :cState and t.state is null " +
                "order by t.countTries desc";

        Map<String, Object> params = new HashMap<>();
        params.put("cState", CampaignState.InWork);
        params.put("operatorId", operatorId);

        List<CallCampaignTrgt> groups = getTargetsFromDB(queryString, params);
        List<CallCampaignTrgt> result = new ArrayList<>();
        if (!groups.isEmpty()){
            result.addAll(groups);
        }
        return result;
    }


    protected int getOperatorTargetsCount(UUID operatorId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Object count = em.createQuery("select count(t) from crm$CallCampaignTrgt t join t.campaign c join c.operators o " +
                    "where t.isGroup = true and t.operator.id = :operatorId and o.id = :operatorId and c.state = :cState and t.state <> :tState")
                    .setParameter("tState", ActivityStateEnum.COMPLETED.getId())
                    .setParameter("cState", CampaignState.InWork.getId())
                    .setParameter("operatorId", operatorId)
                    .getSingleResult();
            tx.commit();
            return Integer.parseInt(count.toString());
        }catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return -1;
        } finally {
            tx.end();
        }
    }

    protected int getTargetsForOperatorCount(UUID operatorId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Object count = em.createQuery("select count(t) from crm$CallCampaignTrgt t join t.campaign c join c.operators o " +
                    "where t.isGroup = true and o.id = :operatorId and c.state = :cState and t.operator is null and (t.state is null or t.state =:tState )")
                    .setParameter("tState", ActivityStateEnum.SCHEDULED.getId())
                    .setParameter("cState", CampaignState.InWork.getId())
                    .setParameter("operatorId", operatorId)
                    .getSingleResult();
            tx.commit();
            return Integer.parseInt(count.toString());
        }catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return -1;
        } finally {
            tx.end();
        }
    }

    protected List<CallCampaignTrgt> getTargetsFromDB(String queryString, Map<String, Object> params) {
        LoadContext loadContext = new LoadContext(CallCampaignTrgt.class)
                .setView("local-with-last-and-next-activity");

        LoadContext.Query query = new LoadContext.Query(queryString);
        query.setParameters(params);
        query.setMaxResults(l_max);

        loadContext.setQuery(query);

        return dataManager.loadList(loadContext);
    }
}
