package com.haulmont.thesis.crm.web.softphone.core;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;
import com.haulmont.thesis.crm.parsers.SoftphoneSessionDecoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 26.09.2017 extract interface & rename to SoftPhoneManagerMBean
// TODO: 29.09.2017 Add Listener on change softPhoneSession
@ManagedBean(SoftPhoneSessionManager.NAME)
public class SoftPhoneSessionManager {
    final static String NAME = "crm_SoftPhoneSessionManager";

    private View sessionView;
    private Log log = LogFactory.getLog(getClass());
    private static Map<UUID, SoftPhoneSession> cache = new ConcurrentHashMap<>();

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;

    //see com.haulmont.thesis.portal.reminder.websocket.AssistantPortalWebSocketWorkerBean.addSession()
    // TODO: 29.10.2017 move to DAO
    public void addSession(UUID id, Map<String, String> messageParams){
        SoftPhoneSession entity = getSessionFromDB(id);

        if (entity == null){
            SoftPhoneSession template = SoftphoneSessionDecoder.decode(messageParams);
            entity = createSessionEntity(id, template);
        }

        cache.put(id, entity);
        this.log.debug("Count session: " + cache.size() + ". Add new session: " + entity.toString());
        refreshSession();
    }

    // TODO: 29.10.2017 move to DAO
    protected SoftPhoneSession getSessionFromDB(UUID sessionId){
        LoadContext loadContext = new LoadContext(SoftPhoneSession.class)
                .setId(sessionId)
                .setView(View.MINIMAL);
        return dataManager.load(loadContext) ;
    }

    // TODO: 29.10.2017 move to DAO
    protected SoftPhoneSession createSessionEntity(UUID id, SoftPhoneSession template){
        SoftPhoneSession entity = metadata.create(SoftPhoneSession.class);
        entity.setId(id);
        entity.setLastName(template.getLastName());
        entity.setFirstName(template.getFirstName());
        entity.setLineNumber(template.getLineNumber());
        entity.setSoftphoneVendor(template.getSoftphoneVendor());
        entity.setActiveProfileId(template.getActiveProfileId());
        entity.setActiveProfile(template.getActiveProfile());
        entity.setProfilesJsonString(template.getProfilesJsonString());
        entity.setStatus(template.getStatus());
        entity.setIsActive(true);

        return dataManager.commit(entity, getSessionView());
    }

    public void removeSession(@Nonnull String sessionId) {
        SoftPhoneSession deleted = cache.remove(UuidProvider.fromString(sessionId));
        deleted.setCampaignEnabled(false);
        deleted.setIsActive(false);
        dataManager.commit(deleted);
        this.log.debug("Count session: " + cache.size() + ". Remove session " + deleted.toString());
    }

    //TODO Timer
    protected void refreshSession(){
        LoadContext ctx = new LoadContext(SoftPhoneSession.class).setView(getSessionView());
        List<SoftPhoneSession> toSave = new ArrayList<>();
        ctx.setQueryString("select s from crm$SoftPhoneSession s where s.isActive = true");
        List<SoftPhoneSession> sessions = dataManager.loadList(ctx);
        for (SoftPhoneSession session: sessions){
            if (!cache.containsKey(session.getId())){
                session.setIsActive(false);
                toSave.add(session);
            }
        }
        CommitContext context = new CommitContext(toSave);
        dataManager.commit(context);
    }

    public void killSessions() {
        cache.clear();
        this.log.debug("kill all session");
        refreshSession();
    }

    @Nullable
    public SoftPhoneSession getSession(UUID id) {
        if (id == null) return null;
        return cache.get(id);
    }

    @Nullable
    public SoftPhoneSession initSession(@Nonnull User user, String lineNumber) {
        SoftPhoneSession result = null;

        if (StringUtils.isBlank(lineNumber)) return result;

        ExtEmployee emp = getEmployeeByUser(user);
        if (emp == null) return result;

        for (SoftPhoneSession item : cache.values()) {
            if (item.getLineNumber().equalsIgnoreCase(lineNumber)) {
                result = item;
                break;
            }
        }

        if (result != null) {
            result.setOperator(emp);

            result = dataManager.commit(result, getSessionView());
        }
        return result;
    }

    protected View getSessionView(){
        if (sessionView == null)
            sessionView = metadata.getViewRepository().getView(SoftPhoneSession.class, "edit");
        return sessionView;
    }

    //TODO newOrgStructureService
    //TODO by Employee
    public String getLineNumberByUser(@Nonnull User currentUser) {
        String result = "";
        ExtEmployee emp = getEmployeeByUser(currentUser);

        if (emp == null) return result;

        result = StringUtils.defaultIfBlank(emp.getPhoneNumExtnesion(), "").trim();

        if (StringUtils.isBlank(result)){
            log.warn(String.format("Line number for employee: %s is empty or null", emp));
        }
        return StringUtils.defaultIfBlank(result, "");
    }

    //TODO newOrgStructureService
    @Nullable
    protected ExtEmployee getEmployeeByUser(User currentUser){
        LoadContext loadContext = new LoadContext(ExtEmployee.class)
                .setView("browse");
        loadContext.setQueryString("select e from df$Employee e where e.user.id = :userId")
                .setParameter("userId", currentUser.getId());
        ExtEmployee emp =  dataManager.load(loadContext) ;

        if (emp == null){
            log.warn(String.format("Employee for user %s not found", currentUser));
        }
        return emp;
    }

    public String getLineNumber(@Nonnull UUID sessionId){
        SoftPhoneSession session = this.getSession(sessionId);
        return (session != null) ? session.getLineNumber() : "";
    }

    public int getCountSessions() {
        return cache.size();
    }

    public Collection<UUID> getSessionIds() {
        return new HashSet<>(cache.keySet());
    }

    //add from db storage
    public Collection<SoftPhoneSession> getSessions() {
        return new ArrayList<>(cache.values());
    }

    public void setActiveProfile(UUID sessionId, SoftPhoneProfileName profile){
        SoftPhoneSession session = getSession(sessionId);
        if (session != null){
            session.setActiveProfile(profile);
            cache.put(sessionId, session);
        }
    }

    public void setActiveCall(UUID sessionId, CallStatus activeCall){
        SoftPhoneSession session = getSession(sessionId);
        if (session != null){
            session.setActiveCall(activeCall);
            cache.put(sessionId, session);
        }
    }

    public void setCampaignEnabled(UUID sessionId, boolean campaignEnabled){
        SoftPhoneSession session = getSession(sessionId);
        if (session != null){
            session.setCampaignEnabled(campaignEnabled);
            cache.put(sessionId, session);
        }
    }
}
