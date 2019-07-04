package com.haulmont.thesis.crm.core.app.contactcenter;

import com.google.common.collect.Lists;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.core.app.CardWorker;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import com.haulmont.thesis.crm.enums.CallModeEnum;
import com.haulmont.thesis.crm.enums.CampaignState;
import com.haulmont.workflow.core.WfHelper;
import com.haulmont.workflow.core.entity.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component(CallCampaignScheduler.NAME)
public class CallCampaignSchedulerBean<T extends CallCampaign> implements CallCampaignScheduler<T> {

    protected Log log = LogFactory.getLog(getClass());

    protected final String PROC_ROLE_INITIATOR = "Initiator";

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Configuration configuration;
    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CardWorker cardWorker;
    @Inject
    protected TimeSource timeSource;

    @Authenticated
    @Override
    public String startCampaign() {
        Proc proc = getProc();
        StringBuilder sb = new StringBuilder();

        if (proc != null) {
            for (T campaign : getNewCampaigns()) {
                sb.append("Start campaign ");
                sb.append(campaign.getNumber());

                Transaction tx = persistence.createTransaction();
                try {
                    campaign = addInitiatorCardRole(campaign, proc);

                    WfHelper.getEngine().startProcess(campaign);
                    tx.commit();
                    sb.append("- success; ");
                } catch (Exception exc){
                    sb.append("- fail; ");
                } finally {
                    tx.end();
                }
            }
        }
        return sb.toString().trim();
    }

    @Authenticated
    @Override
    public String stopCampaign() {
        Proc proc = getProc();
        StringBuilder sb = new StringBuilder();
        for (T campaign : getCampaigns()){
            for(CardProc cp : campaign.getProcs()){
                if (cp.getActive() && cp.getProc().equals(proc)){
                    sb.append("Stop campaign ");
                    sb.append(campaign.getNumber());
                    try {
                        Assignment la = cardWorker.findActiveAssignment(campaign);
                        WfHelper.getEngine().finishAssignment(la.getId(), "Ok", "completed automatically by date");
                        sb.append("- success; ");
                    } catch (final Throwable ex){
                        log.error(ExceptionUtils.getStackTrace(ex));
                        sb.append("- fail; ");
                    }
                }
            }
        }
        return sb.toString().trim();
    }

    @Override
    public String cleanCallTargets(){
        StringBuilder sb = new StringBuilder();

        for (T campaign : getOldCampaigns()){
            sb.append("Clean targets for campaign ");
            sb.append(campaign.getNumber());
            try {
                Set<Entity> toCommit = new HashSet<>();

                for(CallCampaignTrgt target : getTargets(campaign.getId())){
                    target.setOperator(null);
                    target.setState(null);

                    toCommit.add(target);
                }
                CommitContext ctx = new CommitContext(toCommit);
                dataManager.commit(ctx);
                sb.append("- success; ");
            } catch (final Throwable ex){
                log.error(ExceptionUtils.getStackTrace(ex));
                sb.append("- fail; ");
            }
        }
        return sb.toString().trim();
    }

    protected T addInitiatorCardRole(T campaign, Proc proc){
        campaign.setProc(proc);

        CardRole initiatorCardRole = null;
        for (CardRole cr : campaign.getRoles()){
            if (cr.getCode().equals(PROC_ROLE_INITIATOR)){
                initiatorCardRole = cr;
                break;
            }
        }

        if (initiatorCardRole == null){
            ProcRole initiatorProcRole = null;

            for (ProcRole pr : proc.getRoles()){
                if (pr.getCode().equals(PROC_ROLE_INITIATOR)){
                    initiatorProcRole = pr;
                    break;
                }
            }
            if (initiatorProcRole == null) throw new NullPointerException("Proc role 'Initiator' is not exists");

            initiatorCardRole = metadata.create(CardRole.class);
            initiatorCardRole.setProcRole(initiatorProcRole);
            initiatorCardRole.setCode(initiatorProcRole.getCode());
            initiatorCardRole.setCard(campaign);
        }

        initiatorCardRole.setUser(campaign.getOwner().getUser());
        initiatorCardRole.setNotifyByEmail(true);
        initiatorCardRole.setNotifyByCardInfo(false);
        campaign.getRoles().add(initiatorCardRole);

        dataManager.commit(initiatorCardRole);
        View campaignView = metadata.getViewRepository().getView(campaign.getMetaClass(), "edit");
        return dataManager.commit(campaign, campaignView);
    }

    protected List<T> getNewCampaigns() {
        LoadContext ctx = new LoadContext(CallCampaign.class).setView("edit");
        ctx.setQueryString("select e from crm$CallCampaign e " +
                "where e.callMode = :callMode and e.state = :state and e.startTimePlan <= :time and e.endTimePlan >= :time")
                .setParameter("callMode", CallModeEnum.AUTOMATIC.getId())
                .setParameter("state", CampaignState.New.getId())
                .setParameter("time", timeSource.currentTimestamp());
        return dataManager.loadList(ctx);
    }

    protected List<T> getCampaigns() {
        LoadContext ctx = new LoadContext(CallCampaign.class).setView("local-with-assignments");
        ctx.setQueryString("select e from crm$CallCampaign e where e.callMode = :callMode and e.state = :state and e.endTimePlan < :time")
                .setParameter("callMode", CallModeEnum.AUTOMATIC.getId())
                .setParameter("state", CampaignState.InWork.getId())
                .setParameter("time", timeSource.currentTimestamp());
        return dataManager.loadList(ctx);
    }

    protected List<T> getOldCampaigns() {
        LoadContext ctx = new LoadContext(CallCampaign.class).setView(View.LOCAL);
        ctx.setQueryString("select e from crm$CallCampaign e where e.state in :states")
                .setParameter("states", Lists.newArrayList(
                        CampaignState.InWork.getId(),
                        CampaignState.Completed.getId(),
                        CampaignState.NotCompleted.getId(),
                        CampaignState.Canceled.getId()
                ));
        return dataManager.loadList(ctx);
    }

    protected List<CallCampaignTrgt> getTargets(UUID campaignId){
        LoadContext ctx = new LoadContext(CallCampaignTrgt.class).setView("with-group");
        ctx.setQueryString("select t from crm$CallCampaignTrgt t " +
                "where t.campaign.id = :campaignId and t.isGroup = true and t.state = :tState")
                .setParameter("tState", ActivityStateEnum.SCHEDULED.getId())
                .setParameter("campaignId", campaignId);

        return dataManager.loadList(ctx);
    }

    @Nullable
    protected Proc getProc(){
        CrmConfig config = configuration.getConfig(CrmConfig.class);
        LoadContext ctx = new LoadContext(Proc.class).setView("edit");
        ctx.setQueryString("select p from wf$Proc p where p.code = :code")
                .setParameter("code", config.getDefaultCampaignProcCode());
        return dataManager.load(ctx);
    }
}
