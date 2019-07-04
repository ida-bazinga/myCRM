package com.haulmont.thesis.crm.core.app.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.haulmont.bali.datastruct.Pair;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.worker.ActivityWorker;
import com.haulmont.thesis.crm.core.app.worker.CampaignWorker;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.ActivityDirectionEnum;
import com.haulmont.thesis.crm.enums.ActivityResultEnum;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.MappedSuperclass;
import java.util.*;

/**
 * Created by k.khoroshilov on 19.02.2017.
 */
@Service(ActivityService.NAME)
public class ActivityServiceBean implements ActivityService {
    private Log log = LogFactory.getLog(getClass());

    @Inject
    protected ActivityWorker activityWorker;
    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Configuration configuration;
    @Inject
    protected CampaignWorker<CallCampaign> callCampaignWorker;
    @Inject
    protected TimeSource timeSource;
    @Inject
    private CrmConfig config;

    @Override
    public Long getCountWithSpecifiedKind(UUID kindId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createQuery("select count(a) from crm$BaseActivity a join fetch a.kind where a.kind.id = :kindId");
            query.setParameter("kindId", kindId);
            Long count = (Long) query.getSingleResult();
            tx.commit();
            return count;
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return -1L;
        } finally {
            tx.end();
        }
    }

    @Override
    public boolean isSameKindExist(ActivityKind kind) {
        Query query;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("select k.name from crm$ActivityKind k where k.name = :kindName and k.entityType = :type and k.id <> :id");
            query.setParameter("id", kind.getId());
            query.setParameter("type", kind.getEntityType());
            query.setParameter("kindName", kind.getName());
            List result = query.getResultList();
            tx.commit();
            return !result.isEmpty();
        } finally {
            tx.end();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BaseActivity> getWithKind(UUID kindId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            deleteLinkedCategoryAttributeValue(em, kindId);
            Query query = em.createQuery("select a from crm$BaseActivity a join fetch a.kind where a.kind.id = :kindId");
            query.setView(metadata.getViewRepository().getView(BaseActivity.class, "browse"));
            query.setParameter("kindId", kindId);
            List<BaseActivity> result = query.getResultList();
            tx.commit();
            return result;
        } finally {
            tx.end();
        }
    }

    private void deleteLinkedCategoryAttributeValue(EntityManager em, UUID prevId) {
        String deleteQuery = "delete from sys$CategoryAttributeValue cav where exists (select a from crm$BaseActivity a where a.kind.id = :kindId and cav.entityId = a.id)";
        Query updateQuery = em.createQuery(deleteQuery);
        updateQuery.setParameter("kindId", prevId);
        updateQuery.executeUpdate();
    }

    @Override
    public String createDescription(BaseActivity entity){
        return activityWorker.createDescription(entity);
    }

    @Override
    public Set<MetaClass> getCardTypes(List<String> excludedTypes){
        return getCardTypes("crm$BaseActivity", excludedTypes);
    }

    @Override
    public Set<MetaClass> getCardTypes(String entityName, List<String> excludedTypes) {
        MetaClass cardMetaClass = metadata.getClassNN(entityName);
        Collection<MetaClass> metaClasses = cardMetaClass.getDescendants();

        Set<MetaClass> items = new HashSet<>();
        for (MetaClass metaClass : metaClasses) {
            metaClass = metadata.getClassNN(metaClass.getName());
            if (!excludedTypes.contains(metaClass.getName()) && metaClass.getJavaClass().getAnnotation(MappedSuperclass.class) == null) {
                items.add(metaClass);
            }
        }
        return items;
    }

    @Nullable
    @Override
    public ActivityKind getKindByCode(@Nonnull String kindCode) {
        LoadContext ctx = new LoadContext(ActivityKind.class)
                .setView("edit");
        ctx.setQueryString("select k from crm$ActivityKind k where k.code = :kindCode")
                .setParameter("kindCode", kindCode);
        return dataManager.load(ctx);
    }

    @Nullable
    @Override
    public CallActivity getActivityByCallId(@Nonnull String callId, @Nonnull UUID softphoneSessionId){
        LoadContext ctx = new LoadContext(CallActivity.class)
                .setView("edit");
        ctx.setQueryString("select a from crm$CallActivity a where a.callId = :callId and a.state = :state and a.softphoneSessionId = :spsId ")
                .setParameter("callId", callId)
                .setParameter("state", ActivityStateEnum.NEW)
                .setParameter("spsId", softphoneSessionId);
        return dataManager.load(ctx);
    }

    @Nullable
    @Override
    public ActivityRes getResultByCode(@Nonnull ActivityResultEnum resultCode) {
        LoadContext ctx = new LoadContext(ActivityRes.class)
                .setView("edit");
        ctx.setQueryString("select r from crm$ActivityRes r where r.code = :resultCode")
                .setParameter("resultCode", resultCode.getId());
        return dataManager.load(ctx);
    }

    //https://metanit.com/java/tutorial/5.6.php => new PersonNameComparator().thenComparing(new PersonAgeComparator())...
    @Nonnull
    @Override
    public List<Pair<CallActivity, CallCampaignTrgt>> getOperatorQueue(final @Nonnull ExtEmployee operator){
        List<Pair<CallActivity, CallCampaignTrgt>> result = new LinkedList<>();

        List<CallCampaignTrgt> groups = Lists.newArrayList(getTargetGroups(operator));

        if (groups.isEmpty())
            return result;

        List<CallCampaignTrgt> targets = Lists.newArrayList(getTargetByGroups(groups));

        if (targets.isEmpty())
            return result;

        Collections.sort(groups, new Comparator<CallCampaignTrgt>() {
            public int compare(CallCampaignTrgt t1, CallCampaignTrgt t2) {
                //Поднимаем на верх группы у которых есть запланированные активности
                int sort1;
                if (t1.getNextActivity() == null && t2.getNextActivity() == null){
                    sort1 = 0;
                } else if (t1.getNextActivity() == null) {
                    sort1 = 1;
                } else if (t2.getNextActivity() == null) {
                    sort1 = -1;
                } else {
                    //Поднимаем на верх группы у которых есть дата запланированной активности меньше
                    CallActivity a1 = t1.getNextActivity();
                    CallActivity a2 = t2.getNextActivity();
                    if (a1.getEndTimePlan() == null && a2.getEndTimePlan() == null){
                        return 0;
                    } else if (a1.getEndTimePlan() == null){
                        sort1 = 1;
                    } else if (a2.getEndTimePlan() == null){
                        sort1 = -1;
                    } else {
                        sort1 = a1.getEndTimePlan().compareTo(a2.getEndTimePlan());
                    }
                }
                if (sort1 == 0) {
                    //Поднимаем на верх группы с меньшим количеством попыток дозвона
                    if (t1.getCountTries() == null) return 1;
                    if (t2.getCountTries() == null) return -1;
                    return t1.getCountTries().compareTo(t2.getCountTries());
                }
                return sort1;
            }
        });

        for (CallCampaignTrgt group : groups){
            if (group.getNextActivity() != null && group.getNextActivity().getOwner() != null && group.getNextActivity().getAddress() != null){
                Pair<CallActivity, CallCampaignTrgt> pair = new Pair<>(group.getNextActivity(), group);
                result.add(pair);
            } else {
                List<CallCampaignTrgt> subTargets = Lists.newArrayList(filterTargets(targets, group));
                if (!subTargets.isEmpty()){
                    Collections.sort(subTargets, new Comparator<CallCampaignTrgt>() {
                        public int compare(CallCampaignTrgt o1, CallCampaignTrgt o2) {
                            //Поднимаем на верх средства связи с мельшим количеством попыток дозвона
                            if (o1.getCountTries() == null) return 1;
                            if (o2.getCountTries() == null) return -1;
                            return o1.getCountTries().compareTo(o2.getCountFailedTries());
                        }
                    });
                    for(CallCampaignTrgt target : subTargets){
                        CallActivity activity = createCampaignOutgoingActivity(operator, target);
                        Pair<CallActivity, CallCampaignTrgt> pair = new Pair<>(activity, target);
                        result.add(pair);
                    }
                }
                if (group.getNextActivity() != null && group.getNextActivity().getOwner() == null && group.getNextActivity().getAddress() == null)
                    dataManager.remove(group.getNextActivity());
            }
        }
        return result;
    }

    protected List<CallCampaignTrgt> getTargetGroups(@Nonnull ExtEmployee operator) {
        LoadContext loadContext = new LoadContext(CallCampaignTrgt.class)
                .setView("local-with-last-and-next-activity");
        loadContext.setQueryString("select t from crm$CallCampaignTrgt t where t.operator.id = :operator and t.isGroup = true and (t.state is null or t.state not in (:states))")
                .setParameter("states", Lists.newArrayList(ActivityStateEnum.LOCKED, ActivityStateEnum.COMPLETED))
                .setParameter("operator", operator);
        return dataManager.loadList(loadContext);
    }

    protected List<CallCampaignTrgt> getTargetByGroups(@Nonnull List<CallCampaignTrgt> groups) {
        LoadContext loadContext = new LoadContext(CallCampaignTrgt.class)
                .setView("local-with-last-and-next-activity");
        loadContext.setQueryString("select t from crm$CallCampaignTrgt t where t.isGroup = false and t.parent.id in ( :groups )")
                .setParameter("groups", groups);
        return dataManager.loadList(loadContext);
    }

    protected Iterable<CallCampaignTrgt> filterTargets(Collection<CallCampaignTrgt> targets, final CallCampaignTrgt groupItem) {
        return Iterables.filter(targets, new Predicate<CallCampaignTrgt>() {
            @Override
            public boolean apply(CallCampaignTrgt input) {
                return input.getParent() != null && input.getParent().equals(groupItem);
            }
        });
    }

    //todo move to worker
    protected CallActivity createCampaignOutgoingActivity(@Nullable ExtEmployee operator, @Nonnull CallCampaignTrgt target){
        CallActivity result = createActivity("campaign-outgoing-call", operator);

        result.setCampaign(target.getCampaign());
        result.setProject(target.getCampaign().getProject());
        result.setCommunication(target.getCommunication());
        result.setContactPerson(target.getCommunication().getContactPerson());
        result.setCompany((ExtCompany) result.getContactPerson().getCompany());
        result.setAddress(target.getCommunication().getMainPart());
        result.setState(ActivityStateEnum.SCHEDULED);

        return result;
    }

    @Nonnull
    @Override
    public CallActivity copyActivity(@Nonnull CallActivity source, @Nullable ActivityStateEnum state, @Nullable ExtEmployee operator){
        CallActivity result = createActivity(source.getKind(), source.getMetaClass().getName(), operator);

        result.setCampaign(source.getCampaign());
        result.setProject(source.getCampaign().getProject());
        result.setCommunication(source.getCommunication());
        result.setContactPerson(source.getContactPerson());
        result.setCompany(source.getCompany());
        result.setAddress(source.getCommunication().getMainPart());
        if (state != null)
            result.setState(state);

        return result;
    }

    @Nonnull
    @Override
    public CallActivity createOutgoingActivity(ExtEmployee operator){
        return createActivity("outgoing-call", operator);
    }

    @Nonnull
    @Override
    public CallActivity createIncomingActivity(ExtEmployee operator){
        return createActivity("incoming-call", operator);
    }

    @Nonnull
    @Override
    public CallActivity createCampaignSchedulingActivity(ExtEmployee operator){
        CallActivity activity = createActivity("campaign-scheduled-target", operator);
        activity.setState(ActivityStateEnum.SCHEDULED);
        return activity;
    }

    @Nonnull
    protected CallActivity createActivity(String kindCode, ExtEmployee operator){
        ActivityKind activityKind = getKindByCode(kindCode);

        if (activityKind == null){
            String errorMessage = String.format("ActivityKind with code '%s' not found", kindCode);
            log.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
        return createActivity(activityKind, activityKind.getEntityType(), operator);
    }

    @Nonnull
    @Override
    public <T extends BaseActivity> T createActivity(ActivityKind activityKind, @Nonnull Class<? extends BaseActivity> clazz, Employee employee){
        MetaClass metaClass = metadata.getClassNN(clazz);
        return createActivity(activityKind, metaClass.getName(), employee);
    }

    @Nonnull
    @Override
    public <T extends BaseActivity> T createActivity(@Nonnull ActivityKind activityKind, @Nonnull String metaClassName, @Nullable Employee employee) {
        MetaClass metaClass = metadata.getClassNN(metaClassName);
        CrmConfig config = configuration.getConfig(CrmConfig.class);
        T result = metadata.create(metaClass);
        result.setKind(activityKind);
        result.setDirection(activityKind.getDirection());
        result.setCreateTime(timeSource.currentTimestamp());
        result.setCreator(userSessionSource.getUserSession().getUser());
        result.setSubstitutedCreator(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        result.setState(ActivityStateEnum.NEW);
        CallCampaign defCampaign= null;
        if (activityKind.getDirection().equals(ActivityDirectionEnum.INBOUND)){
            defCampaign = getDefaultCampaign(config.getDefaultIncomingCampaign());
            if (defCampaign == null) throw new IllegalStateException("CallCampaign with number " + config.getDefaultIncomingCampaign() + " not found");
        }
        if (activityKind.getDirection().equals(ActivityDirectionEnum.OUTBOUND)){
            defCampaign = getDefaultCampaign(config.getDefaultOutboundCampaign());
            if (defCampaign == null) throw new IllegalStateException("CallCampaign with number " + config.getDefaultOutboundCampaign() + " not found");
        }
        result.setCampaign(defCampaign);
        result.setOwner((ExtEmployee) employee);
        if (employee != null && employee.getDepartment() != null) {
            result.setOrganization(employee.getOrganization());
            result.setDepartment(employee.getDepartment());
        }
        return result;
    }

    public EmailActivity createActivitiesFromTargets(EmailCampaign campaign, EmailCampaignTarget targetItem) {
        EmailActivity messageActivity = metadata.create(EmailActivity.class);
            if (targetItem.getCommunication() != null)
            {
                messageActivity.setAddress(targetItem.getCommunication().getMainPart());
                //messageActivity.setCommunication(targetItem.getCommunication());
                if (targetItem.getCommunication().getContactPerson() != null)
                {
                    messageActivity.setContactPerson(targetItem.getCommunication().getContactPerson());
                }
            }
            if (campaign != null) {
                messageActivity.setCampaign(campaign);
                if (campaign.getProject() != null) {messageActivity.setProject(campaign.getProject());}
            }
            //messageActivity.setEmailCampaignTarget(targetItem);
            if (targetItem.getCompany() != null) {
                messageActivity.setCompany(targetItem.getCompany());
            }
            messageActivity.setKind(config.getActivityKindEmail());
            messageActivity.setDirection(ActivityDirectionEnum.OUTBOUND);
            Organization org = configuration.getConfig(DefaultEntityConfig.class).getOrganizationDefault();
            messageActivity.setOrganization(org);
        return messageActivity;
    }

    @Nullable
    protected CallCampaign getDefaultCampaign(@Nonnull String number) {
        return callCampaignWorker.getDefaultCampaign(number, "activity-edit");
    }
}
