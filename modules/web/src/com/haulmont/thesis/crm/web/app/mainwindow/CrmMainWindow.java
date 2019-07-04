package com.haulmont.thesis.crm.web.app.mainwindow;

import com.google.common.collect.Lists;
import com.haulmont.bali.datastruct.Pair;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.AppUI;
import com.haulmont.thesis.crm.core.app.contactcenter.CallCampaignOperatorQueueService;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.core.app.service.ContactPersonService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.*;
import com.haulmont.thesis.crm.gui.components.SPIPanel;
import com.haulmont.thesis.crm.web.CrmApp;
import com.haulmont.thesis.crm.web.CrmAppUI;
import com.haulmont.thesis.crm.web.softphone.actions.MakeCallAction;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneSessionManager;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;
import com.haulmont.thesis.crm.web.softphone.core.entity.EventMessage;
import com.haulmont.thesis.crm.web.softphone.core.listener.AbstractSoftphoneListener;
import com.haulmont.thesis.crm.web.softphone.core.listener.CallTransferListener;
import com.haulmont.thesis.crm.web.softphone.core.listener.SoftPhoneListener;
import com.haulmont.thesis.crm.web.ui.baseactivity.ActiveCallWindow;
import com.haulmont.thesis.crm.web.ui.callactivity.CallStatusHolder;
import com.haulmont.thesis.crm.web.ui.softphoneSession.datasource.SoftphoneSessionDatasource;
import com.haulmont.thesis.gui.components.ThesisLogoutButton;
import com.haulmont.thesis.web.app.mainwindow.ThesisAppMainWindow;
import com.haulmont.workflow.core.app.WorkCalendarService;
import com.haulmont.workflow.core.entity.Card;
import com.haulmont.workflow.core.global.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

/**
 * @author Kirill Khoroshilov
 */
// move to campanion call logic
//https://docs.cuba-platform.com/cuba/5.6/manual/ru/html-single/manual.html#companions
public class CrmMainWindow extends ThesisAppMainWindow {
    private final Log log = LogFactory.getLog(getClass());
    private LinkedList<Pair<CallActivity, CallCampaignTrgt>> activityQueue = new LinkedList<>();

    protected static final String TARGET_VIEW_NAME = "edit";
    protected static final String LINE_NUMBER_ATTR_NAME = "lineNumber";
    protected boolean isConnected = false;
    protected boolean isAutoDrop = false;
    protected boolean isInternalCall = false;
    protected Boolean tryingToTransfer = false;
    protected SoftPhoneListener softphoneListener;
    //protected boolean spiPanelInited = false;

    protected CallCampaignTrgt nextTarget;
    protected CallActivity nextActivity;
    protected Map<String, CallCampaignTrgt> currentTargets = new HashMap<>();
    protected Set<CallActivity> currentActivities = new HashSet<>();
    protected Window activeWindow;
    protected CrmConfig config;

    @Inject
    protected SPIPanel spiPanel;

    @Inject
    protected Metadata metadata;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected SoftPhoneSessionManager softphoneSessionManager;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected ContactPersonService contactPersonService;
    @Inject
    protected CallCampaignOperatorQueueService callCampaignOperatorQueueService;
    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected WorkCalendarService workCalendarService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        config = configuration.getConfigCached(CrmConfig.class);
    }

    @Override
    public void ready() {
        // TODO: 27.02.2018 Add checkRole
        User currentUser = userSession.getCurrentOrSubstitutedUser();

        String lineNumber = softphoneSessionManager.getLineNumberByUser(currentUser);
        SoftPhoneSession session = softphoneSessionManager.initSession(currentUser, lineNumber);

        if (session != null){
            if (!isAppSPIInit(session.getId())) {
                initSPIPanel(session);

                // TODO: 09.05.2018 Messages
                showNotification(String.format("Соединено с софтфоном: %s", spiPanel.getLineNumber()), NotificationType.TRAY);
                softphoneListener = createSoftPhoneListener();
                CrmAppUI.getCurrent().addSoftPhoneListener(spiPanel.getSoftphoneSessionId(), softphoneListener);
            }
            addUiLogoutListener();
            initTargetsTimer();
        }
    }

    protected boolean isAppSPIInit(UUID sessionId){
        return CrmApp.getInstance().isSPIInit(sessionId);
    }

    protected void initSPIPanel(SoftPhoneSession session){
        // todo проверять роль оператора КЦ и гасить статус и галку очередей с меткой длинны
        spiPanel.setFrame(this);
        spiPanel.setLineNumber(session.getLineNumber());

        Datasource<SoftPhoneSession> ds = createSPIPanelDs();
        spiPanel.setDatasource(ds);
        ds.setItem(session);

        userSession.setAttribute(LINE_NUMBER_ATTR_NAME, session.getLineNumber());
        userSession.setAttribute(SoftphoneConstants.SESSION_ID_ATTR_NAME, session.getId());
    }

    public void addUiLogoutListener() {
        ThesisLogoutButton.BeforeLogoutUiListener uiListener = new ThesisLogoutButton.BeforeLogoutUiListener() {
            @Override
            public void beforeLogout() {
                disposeSPIPanel();

                userSession.removeAttribute(LINE_NUMBER_ATTR_NAME);
                userSession.removeAttribute(SoftphoneConstants.SESSION_ID_ATTR_NAME);
            }
        };
        addLogoutListener(uiListener);
    }

    public void disposeSPIPanel(){
        if(softphoneListener != null && getSoftphoneSessionId() != null){
            CrmAppUI.getCurrent().removeSoftPhoneListener(getSoftphoneSessionId(), softphoneListener);
        }

        if (spiPanel.getDatasource() != null)
            spiPanel.getDatasource().setItem(null);
    }

    protected Datasource<SoftPhoneSession> createSPIPanelDs(){
        MetaClass metaClass = metadata.getSession().getClass(SoftPhoneSession.class);
        Datasource<SoftPhoneSession> ds = new SoftphoneSessionDatasource();
        ds.setup(
                getDsContext(),
                getDsContext().getDataSupplier(),
                "softphoneSessionDs",
                metaClass,
                metadata.getViewRepository().getView(metaClass, "edit")
        );

        ds.addListener(new DsListenerAdapter<SoftPhoneSession>() {
            @Override
            public void valueChanged(SoftPhoneSession source, String property, Object prevValue, Object value) {
                if (property.equals("campaignEnabled")) {
                    boolean newValue = (boolean) value;
                    softphoneSessionManager.setCampaignEnabled(source.getId(), newValue);
                    Timer t = getTimer("targetsTimer");
                    if (t != null){
                        if (newValue){
                            t.start();
                        }else{
                            t.stop();
                        }
                    }
                }

                if (property.equals("activeCall")) {
                    CallStatus newValue = (CallStatus) value;
                    softphoneSessionManager.setActiveCall(source.getId(), newValue);
                }
            }
        });
        ds.refresh();
        return ds;
    }

    protected boolean isOperatorInQueue() {
        return callCampaignOperatorQueueService.isOperatorInQueue(getOperator());
    }

    @Nullable
    public SoftPhoneSession getSoftphoneSession() {
        return spiPanel != null ? spiPanel.getSoftphoneSession() : null;
    }

    @Nullable
    public UUID getSoftphoneSessionId() {
        return getSoftphoneSession() != null ? getSoftphoneSession().getId() : null;
    }

    protected void addCurrentActivity(@Nonnull CallActivity activity){
        if (currentActivities.contains(activity)) removeCurrentActivity(activity);
        currentActivities.add(activity);
    }

    protected void removeCurrentActivity(@Nonnull CallActivity activity){
        currentActivities.remove(activity);
    }

    @Nullable
    protected CallActivity getCurrentActivity(String callId){
        if (StringUtils.isBlank(callId)) return null;

        for (CallActivity a :currentActivities){
            if (StringUtils.isNotBlank(a.getCallId()) && a.getCallId().equals(callId)) return a;
        }
        return null;
    }

    protected void addCurrentTarget(String callId,  @Nonnull CallCampaignTrgt target){
        if (StringUtils.isBlank(callId))
            throw new IllegalStateException("callId is null or is not specified");

        if (currentTargets.containsKey(callId)) removeCurrentTarget(callId);

        currentTargets.put(callId, target);
    }

    protected void removeCurrentTarget(String callId){
        if (StringUtils.isBlank(callId))
            throw new IllegalStateException("callId is null or is not specified");

        currentTargets.remove(callId);
    }

    @Nullable
    protected CallCampaignTrgt getCurrentTarget(String callId){
        if (StringUtils.isBlank(callId)) return null;

        return currentTargets.get(callId);
    }

    @Nullable
    protected ExtEmployee getOperator(){
        if(getSoftphoneSession() != null)
            return getSoftphoneSession().getOperator();
        return null;
    }

    protected void initTargetsTimer() {
        Timer timer = AppBeans.get(ComponentsFactory.class).createTimer();
        timer.setId("targetsTimer");
        timer.setRepeating(true);
        timer.setDelay(5000);

        timer.addTimerListener(new Timer.TimerListener(){
            @Override
            public void onTimer(Timer timer) {
                final SoftPhoneSession session = getSoftphoneSession();
                if (session != null && session.getIsActive() && getOperator() != null){

                    if (session.getCampaignEnabled() && calculateQueueSize() < config.getMinOperatorQueue() && !isOperatorInQueue()) {
                        callCampaignOperatorQueueService.addOperator(getOperator());
                        log.debug("add operator to queue");
                    } else{
                        log.debug("not add operator to queue");
                    }

                    if (session.getCampaignEnabled() && calculateQueueSize() < 1 && session.getActiveCall() == null && activeWindow == null) {
                        activityQueue.addAll(activityService.getOperatorQueue(getOperator()));
                        session.setQueueSize(calculateQueueSize());
                    }

                    if (session.getCampaignEnabled() && session.getActiveProfile().equals(SoftPhoneProfileName.AVAILABLE)
                            && session.getQueueSize() > 0 && session.getActiveCall() == null && activeWindow == null){

                        for(Window w : windowManager.getOpenWindows()){
                            if (w.getId().equals(getEditorWindowId())){
                                return;
                            }
                        }
                        //if (windowManager.getOpenWindows().size()>0){
                        //    showNotification("manyWindow", NotificationType.TRAY);
                        //    return;
                        //}

                        log.info(String.format("Get target for %s", getOperator().getName()));

                        Pair<CallActivity, CallCampaignTrgt> pair = activityQueue.pollFirst();
                        if (pair != null){
                            nextActivity = pair.getFirst();
                            nextTarget = pair.getSecond();

                            if (nextActivity.getCommunication() != null && StringUtils.isNotBlank(nextActivity.getCommunication().getMainPart())){
                                MakeCallAction action = new MakeCallAction(session.getId(), nextActivity.getCommunication().getMainPart());
                                action.actionPerform(null);
                            }
                        } else {
                            nextActivity = null;
                            nextTarget = null;
                        }
                    }
                }
            }

            @Override
            public void onStopTimer(Timer timer) {
                activityQueueClear();
            }
        });
        addTimer(timer);
    }

    protected void activityQueueClear(){
        Set<Entity> toCommit = new HashSet<>();
        while (activityQueue.size() > 0){
            Pair<CallActivity, CallCampaignTrgt> pair = activityQueue.pollFirst();
            if (getSoftphoneSession() != null)
                getSoftphoneSession().setQueueSize(calculateQueueSize());

            if (pair != null){
                CallCampaignTrgt target =
                        pair.getSecond().getIsGroup() ? pair.getSecond() : reload(pair.getSecond().getParent(), TARGET_VIEW_NAME);
                if (target.getState().equals(ActivityStateEnum.SCHEDULED)){
                    target.setOperator(null);
                    target.setState(null);
                    toCommit.add(target);
                }
            }
        }
        CommitContext ctx = new CommitContext(toCommit);
        getDsContext().getDataSupplier().commit(ctx);
    }

    protected SoftPhoneListener createSoftPhoneListener(){
        return new AbstractSoftphoneListener() {
            @Override
            public void onAction(ActionMessage action) {
                if (getSoftphoneSession() != null && getSoftphoneSession().getId().equals(action.getSessionId())) {
                    addEvent(action.getName(), action.getMessageParams());
                }
            }

            @Override
            public void onEvent(EventMessage event) {
                if (getSoftphoneSession() != null && getSoftphoneSession().getId().equals(event.getSessionId())) {
                    addEvent(event.getName(), event.getMessageParams());
                }
            }

            @Override
            public void onRinging(UUID sessionId, CallStatus callStatus) {
                //onRinging(sessionId, callStatus);

                if (callStatus.getOtherPartyNumber().length() < 5) {
                    showNotification(formatMessage(
                            "incomingInternalCall", callStatus.getOtherPartyName(), callStatus.getOtherPartyNumber()), NotificationType.TRAY
                    );
                    isInternalCall = true;
                    return;
                }

                if(spiPanel.getActiveCall() != null && spiPanel.getActiveCall().equals(callStatus)) {
                    spiPanel.setActiveCall(callStatus);
                    return;
                }

                spiPanel.setActiveCall(callStatus);

                TreeSet<Communication> communications = getCommunicationByAddress(callStatus.getOtherPartyNumber());
                CallActivity newActivity = activityService.createIncomingActivity(getOperator());
                newActivity.setSoftphoneSessionId(sessionId);
                newActivity.setCallId(callStatus.getCallId());
                newActivity.setAddress(callStatus.getOtherPartyNumber());

                if (!communications.isEmpty()){
                    newActivity.setCommunication(communications.first());
                    newActivity.setContactPerson(newActivity.getCommunication().getContactPerson());
                    newActivity.setCompany((ExtCompany) newActivity.getContactPerson().getCompany());
                }

                newActivity.setResult(getResultByCode(ActivityResultEnum.MISSED_CALL));
                newActivity = commit(newActivity);
                addCurrentActivity(newActivity);
                nextTarget = null;
                openActiveCallWindow(newActivity);
            }

            @Override
            public void onDialing(UUID sessionId, CallStatus callStatus) {
                //onDialing(sessionId, callStatus);

                if (callStatus.getOtherPartyNumber().length() < 5) {
                    showNotification(formatMessage(
                            "outgoingInternalCall", callStatus.getOtherPartyName(), callStatus.getOtherPartyNumber()), NotificationType.TRAY
                    );
                    isInternalCall = true;
                    return;
                }

                if (tryingToTransfer)
                    return;

                spiPanel.setActiveCall(callStatus);

                CallActivity currentActivity;

                if(nextActivity != null && nextTarget != null) {
                    //звонок по компании
                    currentActivity = nextActivity;
                    CallCampaignTrgt currentTarget = nextTarget;

                    if (currentTarget.getIsGroup()){
                        currentTarget.setState(ActivityStateEnum.LOCKED);
                    } else {
                        CallCampaignTrgt group = currentTarget.getParent();
                        group.setState(ActivityStateEnum.LOCKED);
                        commit(group);
                    }
                    currentTarget.setCountTries(currentTarget.getCountTries()+1);
                    addCurrentTarget(callStatus.getCallId(), currentTarget);

                } else {
                    //звонок вне кампании
                    currentActivity = activityService.createOutgoingActivity(getOperator());
                    currentActivity.setAddress(callStatus.getOtherPartyNumber());

                    TreeSet<Communication> communications = getCommunicationByAddress(callStatus.getOtherPartyNumber());
                    if (!communications.isEmpty()){
                        currentActivity.setCommunication(communications.first());
                        currentActivity.setContactPerson(currentActivity.getCommunication().getContactPerson());
                        currentActivity.setCompany((ExtCompany) currentActivity.getContactPerson().getCompany());
                    }
                }

                currentActivity.setSoftphoneSessionId(sessionId);
                currentActivity.setCallId(callStatus.getCallId());
                currentActivity.setResult(getResultByCode(ActivityResultEnum.NO_ANSWER));
                addCurrentActivity(currentActivity);

                commit(callStatus.getCallId());

                openActiveCallWindow(currentActivity);
            }

            @Override
            public void onCallConnected(UUID sessionId, final CallStatus callStatus) {
                //onCallConnected();

                CallActivity currActivity = getCurrentActivity(callStatus.getCallId());

                if (currActivity == null) return;

                if (isInternalCall || tryingToTransfer) return;

                // OriginatorType.Queue игнорируем так как это система генерирует два входящих один из очереди, второй
                // на extension с точно такими же параметрами, но OriginatorType.None
                if (callStatus.getOriginatorType().equals(OriginatorType.Queue)) return;

                if (activeWindow instanceof CallStatusHolder){
                    CallStatusHolder callHolder = (CallStatusHolder)activeWindow;
                    if (callHolder.getCallStatus() != null && callHolder.getCallStatus().equals(callStatus))
                        return;
                }

                isConnected = true;
                if (activeWindow instanceof ActiveCallWindow){
                    activeWindow.close(Editor.WINDOW_CLOSE);
                    activeWindow = null;
                }

                currActivity.setResult(null);
                currActivity.setConnectionStartTime(timeSource.currentTimestamp());
                currActivity.setState(ActivityStateEnum.IN_WORK);
                currActivity = commit(currActivity);
                addCurrentActivity(currActivity);

                HashMap<String, Object> params = new HashMap<>();
                params.put("justCreated", true);
                params.put("mainWindowCallTransferListener", createCallTransferListener());

                TreeSet<Communication> communications = getCommunicationByAddress(callStatus.getOtherPartyNumber());
                if (!communications.isEmpty() && communications.size() > 1) {
                    Map<String, Communication> commMap = new HashMap<>();
                    for (Communication comm :communications){
                        commMap.put(comm.getMaskedAddress(), comm);
                    }
                    params.put("commMap",commMap);
                }

                final CallStatusHolder window = openEditor(getEditorWindowId(), currActivity, WindowManager.OpenType.DIALOG, params);
                window.setCallStatus(callStatus);
                activeWindow = window;
                window.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        activeWindow = null;
                        CallActivity currentActivity = getCurrentActivity(callStatus.getCallId());
                        //todo несколько окон активности - теряем состояние очереди и текущих объектов.
                        if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                            if (currentActivity != null) {
                                currentActivity = reload(currentActivity, "edit");
                            } else {
                                currentActivity = reloadByCallId(callStatus.getCallId(), "edit");
                            }
                            if (currentActivity == null){
                                return;
                            }

                            addCurrentActivity(currentActivity);
                            currentActivity.setEndTimeFact(timeSource.currentTimestamp());

                            CallCampaignTrgt currentTarget = getCurrentTarget(callStatus.getCallId());

                            if (!callStatus.isIncoming() && currentTarget != null){
                                //совершён исходящий звонок по компании
                                if (Lists.newArrayList(ActivityResultEnum.RECALL_LATER.getId(), ActivityResultEnum.RECALL_NOW.getId())
                                    .contains(currentActivity.getResult().getCode())) {
                                //if (currentActivity.getResult().getCode().equals(ActivityResultEnum.RECALL_LATER.getId()) ||
                                //        currentActivity.getResult().getCode().equals(ActivityResultEnum.RECALL_NOW.getId())) {
                                    //результат = перезвонить позже или сейчас
                                    CallActivity newActivity = null;
                                    for (Card subCard : currentActivity.getSubCards()) {
                                        if (subCard instanceof CallActivity) {
                                            newActivity = (CallActivity) subCard;
                                            break;
                                        }
                                    }
                                    if (currentTarget.getIsGroup()) {
                                        currentTarget.setCountTries(0);
                                        currentTarget.setLastActivity(currentActivity);
                                        if (newActivity != null)
                                            currentTarget.setNextActivity(newActivity);
                                        currentTarget.setState(ActivityStateEnum.SCHEDULED);
                                        if (currentActivity.getResult().getCode().equals(ActivityResultEnum.RECALL_LATER.getId())) {
                                            currentTarget.setOperator(null);
                                        }
                                        if (currentActivity.getResult().getCode().equals(ActivityResultEnum.RECALL_NOW.getId())) {
                                            Pair<CallActivity, CallCampaignTrgt> newPair = new Pair<>(newActivity, currentTarget);
                                            activityQueue.addFirst(newPair);
                                        }
                                    } else {
                                        CallCampaignTrgt group = reload(currentTarget.getParent(), TARGET_VIEW_NAME);
                                        group.setCountTries(0);
                                        group.setLastActivity(currentActivity);
                                        if (newActivity != null)
                                            group.setNextActivity(newActivity);
                                        group.setState(ActivityStateEnum.SCHEDULED);
                                        if (currentActivity.getResult().getCode().equals(ActivityResultEnum.RECALL_LATER.getId())) {
                                            group.setOperator(null);
                                        }

                                        while (!activityQueue.isEmpty()) {
                                            Pair<CallActivity, CallCampaignTrgt> nextPair = activityQueue.getFirst();
                                            CallCampaignTrgt nxtTarget = nextPair.getSecond();

                                            if (!nxtTarget.getIsGroup() && nxtTarget.getParent().equals(group)) {
                                                //следующий таргет в очереди не группа и группа сл. таргета соответствует текущей
                                                // удаляем таргет и проверяем следующий.
                                                activityQueue.removeFirst();
                                            } else {
                                                break;
                                            }
                                        }
                                        commit(group);

                                        if (currentActivity.getResult().getCode().equals(ActivityResultEnum.RECALL_NOW.getId())) {
                                            Pair<CallActivity, CallCampaignTrgt> newPair = new Pair<>(newActivity, group);
                                            activityQueue.addFirst(newPair);
                                        }
                                    }
                                } else if (Lists.newArrayList(
                                            ActivityResultEnum.NO_ANSWER.getId(), ActivityResultEnum.AUTO_ANSWER.getId(), ActivityResultEnum.ERROR)
                                        .contains(currentActivity.getResult().getCode())) {
                                    //currentActivity.getResult().getCode().equals(ActivityResultEnum.NO_ANSWER.getId())){

                                    CallCampaignTrgt group = currentTarget.getIsGroup() ? currentTarget : reload(currentTarget.getParent(), TARGET_VIEW_NAME);

                                    if (activityQueue.isEmpty() ||
                                            !activityQueue.getFirst().getSecond().getIsGroup() && !activityQueue.getFirst().getSecond().getParent().equals(group)) {
                                        //следующий таргет в очереди не группа и группа сл. таргета другая. При этом в очереди еще есть элементы
                                        // т.е это последний таргет группы, а значит обновляем значения группового таргета.
                                        group.setOperator(null);
                                        group.setLastActivity(currentActivity);
                                        group.setCountTries(group.getCountTries() + 1);
                                        group.setCountFailedTries(group.getCountFailedTries() + 1);

                                        if (group.getCountTries() >= currentTarget.getCampaign().getMaxAttemptCount()) {
                                            //превышено количество попыток набора
                                            group.setState(ActivityStateEnum.COMPLETED);
                                            group.setNextActivity(null);
                                        } else {
                                            //не превышено количество попыток набора
                                            //создаем фиктивную активность, чтобы отложить таргет на 1 день.
                                            CallActivity newActivity = activityService.createCampaignSchedulingActivity(null);
                                            newActivity.setEndTimePlan(
                                                    workCalendarService.addInterval(new Date(), 8, TimeUnit.HOUR)
                                            );
                                            newActivity.setParentCard(currentActivity);
                                            newActivity.setCampaign(currentTarget.getCampaign());
                                            newActivity.setProject(currentTarget.getCampaign().getProject());
                                            newActivity.setCompany(currentTarget.getCompany());
                                            newActivity = commit(newActivity);

                                            group.setNextActivity(newActivity);
                                            group.setState(ActivityStateEnum.SCHEDULED);
                                        }
                                        commit(group);
                                    }
                                }else if(currentActivity.getResult().getCode().equals(ActivityResultEnum.NON_ACTUAL_COMM.getId())){
                                    //Удаляем неактуальные СС (softdelete!)
                                    // и через EntityListeners удаляются все таргеты и их группы если в них неосталось таргетов)
                                    removeBadCompanyCommunications(callStatus.getOtherPartyNumber(), currentActivity.getCompany());
                                    currentActivity.setDetails(currentActivity.getDetails() + "\n" + "средство связи удалено");
                                } else {
                                    //все остальные результаты кроме перезвонов, нет ответа и неактуальных СС
                                    //проверять на фильнальность тип результата не надо так как групповой таргет содержит только одну активность
                                    if (currentTarget.getIsGroup()) {
                                        currentTarget.setCountTries(currentTarget.getCountTries()+1);
                                        currentTarget.setLastActivity(currentActivity);
                                        currentTarget.setState(ActivityStateEnum.COMPLETED);
                                        currentTarget.setNextActivity(null);
                                        currentTarget.setOperator(null);
                                    } else {
                                        CallCampaignTrgt group = reload(currentTarget.getParent(), TARGET_VIEW_NAME);
                                        group.setCountTries(group.getCountTries()+1);
                                        group.setLastActivity(currentActivity);

                                        if(currentActivity.getResult().getResultType().equals(ActivityResTypeEnum.SUCCESSFUL) ||
                                                currentActivity.getResult().getResultType().equals(ActivityResTypeEnum.UNSUCCESSFUL)){
                                            //Для финальных статусов группе ставим признак завершено и убираем таргеты из очереди
                                            group.setState(ActivityStateEnum.COMPLETED);
                                            group.setOperator(null);
                                            while (!activityQueue.isEmpty()) {
                                                Pair<CallActivity, CallCampaignTrgt> nextPair = activityQueue.getFirst();
                                                CallCampaignTrgt nxtTarget = nextPair.getSecond();

                                                if (!nxtTarget.getIsGroup() && nxtTarget.getParent().equals(group)) {
                                                    //следующий таргет в очереди не группа и группа сл. таргета соответствует текущей
                                                    // удаляем таргет и проверяем следующий.
                                                    activityQueue.removeFirst();
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                        commit(group);
                                    }
                                }
                            }
                            if (callStatus.isIncoming()){
                                //входящий звонок
                            }

                            commit(callStatus.getCallId());
                        }
                        if (currentActivity != null) removeCurrentActivity(currentActivity);
                        removeCurrentTarget(callStatus.getCallId());
                        if (getSoftphoneSession() != null) getSoftphoneSession().setQueueSize(calculateQueueSize());
                    }
                });
            }

            @Override
            public void onCallEnded(UUID sessionId, String callId) {
                //onCallEnded();
                if (isInternalCall){
                    isInternalCall = false;
                    return;
                }

                if (tryingToTransfer) {
                    tryingToTransfer = false;
                    return;
                }

                // OriginatorType.Queue игнорируем так как это система генерирует два входящих один из очереди, второй
                // на extension с точно такими же параметрами, но OriginatorType.None
                if (spiPanel.getActiveCall().getOriginatorType().equals(OriginatorType.Queue)) return;

                boolean isIncoming = spiPanel.getActiveCall().isIncoming();
                spiPanel.setActiveCall(null);

                CallCampaignTrgt currentTarget = getCurrentTarget(callId);

                if (!isIncoming && currentTarget != null){
                    //совершён исходящий звонок по компании
                    if (getSoftphoneSession() != null) getSoftphoneSession().setQueueSize(calculateQueueSize());
                }

                if (isConnected){
                    //Было соединение (проверять длителность соединения?)
                    if (activeWindow instanceof CallStatusHolder) {
                        ((CallStatusHolder) activeWindow).setCallStatus(null);
                    }
                    isConnected = false;
                } else {
                    //Соединения не было
                    //закрываем окно ответа на звонок
                    if (activeWindow instanceof ActiveCallWindow) {
                        activeWindow.close(Editor.WINDOW_CLOSE);
                    }

                    CallActivity currentActivity = getCurrentActivity(callId);

                    if (currentActivity != null) {
                        currentActivity.setEndTimeFact(timeSource.currentTimestamp());

                        if (isIncoming) {
                            //Ожидание соединения завершено по таймеру для входящего звонка
                            //OnAutoDropIncomingCall()

                            currentActivity.setState(ActivityStateEnum.COMPLETED);
                        } else {
                            // Вызов зовершен до установления соединения или до срабатывания таймера или по таймеру
                            //OnAutoDropOutgoingCall()

                            if (currentTarget != null) {
                                if (currentTarget.getIsGroup()) {
                                    //звонили по групповому таргету значит это запланированная активность
                                    if (currentTarget.getCountTries() >= currentTarget.getCampaign().getMaxAttemptCount()) {
                                        //превышено количество попыток набора
                                        currentTarget.setState(ActivityStateEnum.COMPLETED);
                                        currentTarget.setNextActivity(null);
                                    } else {
                                        //не превышено количество попыток набора
                                        CallActivity newActivity = activityService.copyActivity(
                                                currentActivity, ActivityStateEnum.SCHEDULED, currentActivity.getOwner()
                                        );
                                        newActivity.setEndTimePlan(
                                                workCalendarService.addInterval(new Date(), 8, TimeUnit.HOUR)
                                        );
                                        newActivity.setParentCard(currentActivity);
                                        newActivity = commit(newActivity);

                                        currentTarget.setNextActivity(newActivity);
                                        currentTarget.setState(ActivityStateEnum.SCHEDULED);
                                    }
                                    currentTarget.setOperator(null);
                                    currentTarget.setLastActivity(currentActivity);
                                } else {
                                    //звонили не по групповому таргету
                                    CallCampaignTrgt group = reload(currentTarget.getParent(), TARGET_VIEW_NAME);

                                    if (activityQueue.isEmpty() ||
                                            !activityQueue.getFirst().getSecond().getIsGroup() && !activityQueue.getFirst().getSecond().getParent().equals(group)) {
                                        //следующий таргет в очереди не группа и группа сл. таргета другая. При этом в очереди еще есть елементы
                                        // т.е это последний таргет группы, а значит обновляем значения группового таргета.
                                        group.setOperator(null);
                                        group.setLastActivity(currentActivity);
                                        group.setCountTries(group.getCountTries() + 1);
                                        group.setCountFailedTries(group.getCountFailedTries() + 1);

                                        if (group.getCountTries() >= currentTarget.getCampaign().getMaxAttemptCount()) {
                                            //превышено количество попыток набора
                                            group.setState(ActivityStateEnum.COMPLETED);
                                            group.setNextActivity(null);
                                        } else {
                                            //не превышено количество попыток набора
                                            //создаем фиктивную активность, чтобы отложить таргет на 1 день.
                                            CallActivity newActivity = activityService.createCampaignSchedulingActivity(null);
                                            newActivity.setEndTimePlan(
                                                    workCalendarService.addInterval(new Date(), 8, TimeUnit.HOUR)
                                            );
                                            newActivity.setParentCard(currentActivity);
                                            newActivity.setCampaign(currentTarget.getCampaign());
                                            newActivity.setProject(currentTarget.getCampaign().getProject());
                                            newActivity.setCompany(currentTarget.getCompany());
                                            newActivity = commit(newActivity);

                                            group.setNextActivity(newActivity);
                                            group.setState(null);
                                        }
                                        commit(group);
                                    }
                                }
                                currentTarget.setCountFailedTries(currentTarget.getCountFailedTries() + 1);
                                currentActivity.setState(ActivityStateEnum.COMPLETED);
                            }
                        }
                    }
                    commit(callId);
                    if (currentActivity != null) removeCurrentActivity(currentActivity);
                }
            }

            @Override
            public void onProfileChange(UUID sessionId, SoftPhoneProfileName profile) {
                if (spiPanel.getSoftphoneSessionId() != null && spiPanel.getSoftphoneSessionId().equals(sessionId) && profile != null){
                    spiPanel.setProfile(profile);
                    softphoneSessionManager.setActiveProfile(sessionId, profile);
                }
            }

            @Override
            public void onCloseSoftphone() {
                disposeSPIPanel();

                if (activeWindow instanceof ActiveCallWindow) {
                    activeWindow.close(Editor.WINDOW_CLOSE);
                    nextActivity = null;
                    nextTarget = null;
                }
            }
        };
    }

    protected CallTransferListener createCallTransferListener(){
        return new CallTransferListener() {
            @Override
            public void setTryingToTransfer(Boolean value, String destination) {
                tryingToTransfer = value;
            }
        };
    }

    protected int calculateQueueSize(){
        Set<CallCampaignTrgt> groups = new HashSet<>();
        for (Pair<CallActivity, CallCampaignTrgt> pair : activityQueue){
            CallCampaignTrgt trgt = pair.getSecond();
            groups.add(trgt.getIsGroup() ? trgt : trgt.getParent());
        }
        return groups.size();
    }

    public void addEvent(String name, Map<String, String> params){
        String msg = params != null ? String.format("%s params. %s", params.size(), getParamsInfo(params)) :"" ;
        log.info(name + " " + msg);
    }

    protected String getParamsInfo(Map<String, String> params){
        StringBuilder builder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            Set<String> paramKeys = params.keySet();
            for (String key : paramKeys) {
                String value = params.get(key);
                builder.append(key).append("=").append(value).append("; ");
            }
        }
        return builder.toString();
    }

    protected void openActiveCallWindow(CallActivity activity){
        Map<String, Object> params = new HashMap<>();
        params.put("currentActivity", activity);

        Window window = openWindow(SoftphoneConstants.ACTIVE_CALL_WINDOW_NAME, WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                isAutoDrop = actionId.equals(ActiveCallWindow.IS_DROP_CALL_DELAY);
                activeWindow = null;

            }
        });

        activeWindow = window;
        isAutoDrop = false;
    }

    protected String getEditorWindowId() {
        MetaClass metaClass = metadata.getClassNN(CallActivity.class);
        return windowConfig.getEditorScreenId(metaClass);
    }

    protected void commit(String callId) {
        if (StringUtils.isBlank(callId))
            throw new IllegalStateException("callId is null or is not specified");

        CallActivity currentActivity = getCurrentActivity(callId);
        if (currentActivity == null)
            throw new IllegalStateException("currentActivity is null");

        CommitContext context = new CommitContext();
        Set<Entity> toCommit = new HashSet<>();

        toCommit.add(currentActivity);
        context.getViews().put(currentActivity, getEntityEditView(currentActivity.getClass()));

        CallCampaignTrgt currentTarget = getCurrentTarget(callId);

        if (currentTarget != null) {
            toCommit.add(currentTarget);
            context.getViews().put(currentTarget, getEntityEditView(currentTarget.getClass()));
        }
        if (toCommit.isEmpty()) return;

        context.setCommitInstances(toCommit);
        Set<Entity> commited = getDsContext().getDataSupplier().commit(context);

        for (Entity item : commited){
            if (item instanceof CallActivity && currentActivity.getId().equals(item.getId())){
                currentActivity = (CallActivity) item;
                addCurrentActivity(currentActivity);
            }
            if (item instanceof CallCampaignTrgt && currentTarget != null && currentTarget.getId().equals(item.getId())){
                currentTarget = (CallCampaignTrgt) item;
                addCurrentTarget(callId, currentTarget);
            }
        }
    }

    protected void remove(@Nonnull Set<Communication> removed) {
        if (removed.isEmpty()) return;

        CommitContext context = new CommitContext();
        context.setRemoveInstances(removed);
        getDsContext().getDataSupplier().commit(context);
    }

    protected <T extends Entity> T commit(@Nonnull T entity) {
        return getDsContext().getDataSupplier().commit(entity, getEntityEditView(entity.getClass()));
    }

    protected <T extends Entity> T reload(@Nonnull T entity, String viewName) {
        return getDsContext().getDataSupplier().reload(entity, viewName);
    }

    protected void removeBadCompanyCommunications(String address, ExtCompany company){
        Set<Communication> removed = new HashSet<>();

        List<Communication> communications =  contactPersonService.findCommunication(CommunicationTypeEnum.phone, address);
        for (Communication comm : communications){
            if (comm.getContactPerson().getCompany().equals(company)){
                removed.add(comm);
            }
        }
        remove(removed);
    }

    @SuppressWarnings("SameParameterValue")
    @Nullable
    protected CallActivity reloadByCallId(String callId, String viewName){
        LoadContext context = new LoadContext(CallActivity.class).setView(viewName);
        context.setQueryString("select e from crm$CallActivity e where e.callId = :callId").setParameter("callId", callId);

        return getDsContext().getDataSupplier().load(context);
    }


    @SuppressWarnings("unchecked")
    protected View getEntityEditView(Class clazz){
        return metadata.getViewRepository().getView(clazz, "edit");
    }

    protected ActivityRes getResultByCode(@Nonnull ActivityResultEnum resultCode){
        ActivityRes result = activityService.getResultByCode(resultCode);

        if (result == null){
            log.error(String.format("ActivityResult with code %s not found", resultCode));
            throw new NullPointerException();
        }
        return result;
    }

    protected TreeSet<Communication> getCommunicationByAddress(String address){
        TreeSet<Communication> result = getTreeSet();
        result.addAll(contactPersonService.findCommunication(CommunicationTypeEnum.phone, address));
        return result;
    }

    @SuppressWarnings("Duplicates")
    protected TreeSet<Communication> getTreeSet(){
        return new TreeSet<>(new Comparator<Communication>() {
            @SuppressWarnings("Duplicates")
            public int compare(Communication o1, Communication o2) {
                //Поднимаем наверх объекты с мельшим приоритетом
                if (o1.getPref() == null) return 1;
                if (o2.getPref() == null) return -1;
                int sort1 = o1.getPref().compareTo(o2.getPref());
                if (sort1 == 0){
                    if (o1.getPriority() == null) return 1;
                    if (o2.getPriority() == null) return -1;
                    return o1.getPriority().compareTo(o2.getPriority());
                }
                return sort1;
            }
        });
    }
}
