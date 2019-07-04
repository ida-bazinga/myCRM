package com.haulmont.thesis.crm.web.operatorSession;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.thesis.core.entity.Company;
import com.haulmont.thesis.crm.core.app.contactcenter.OperatorQueueService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.phoneintegration.web.CallIntegrationJavaScriptComponent;
import com.haulmont.thesis.crm.phoneintegration.web.CallIntegrationWindowListener;
import com.haulmont.thesis.web.DocflowApp;
import com.haulmont.workflow.core.app.WorkCalendarService;
import com.haulmont.workflow.core.global.TimeUnit;
import com.vaadin.ui.AbstractOrderedLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class OperatorSessionWindow extends AbstractWindow implements CallIntegrationWindowListener {

    private boolean isTest;
    private boolean isInitialized = false;
    private Date connectionStartTime, connectionEndTime;
    private boolean isWaiting;
    private boolean inQueue;
    private boolean addOperatorToQueueService = true;

    private CallIntegrationJavaScriptComponent callIntegrationComponent;
    private Map<String,CallStatus> callStatues;

    //// TODO: 25.08.2016 Неиспользую почему.
    //private CallEndedStateEnum callEndedState;

    protected OperatorSession item;
    private CallCampaignTarget target;

    private Iterator<CallCampaignTarget> targetIterator;
    private Iterator<Map<String, ExtContactPerson>> phonesIterator;

    @Named("mainDs")
    protected Datasource<OperatorSession> mainDs;
    @Named("activityDs")
    protected Datasource<Activity> activityDs;
    @Named("targetsDs")
    protected CollectionDatasource<CallCampaignTarget, UUID> targetsDs;
    @Named("contactPersonsDs")
    protected CollectionDatasource<ExtContactPerson, UUID> contactPersonsDs;
    @Named("resultsDs")
    protected CollectionDatasource<ActivityResult, UUID> resultsDs;

    @Named("phoneToRecall")
    protected LookupField phoneToRecall ;
    @Named("nextContact")
    protected LookupPickerField nextContact;
    @Named("targetsTimer")
    protected Timer targetsTimer ;
    @Named("dropCallTimer")
    protected Timer dropCallTimer ;
    @Named("activityResult")
    protected LookupField activityResult;
    @Named("infoLabel")
    protected Label infoLabel;
    @Named("timeToRecall")
    protected DateField timeToRecall;
    @Named("resultDetails")
    protected TextArea resultDetails;

    private MainWindow mainWindow = DocflowApp.getInstance().getAppWindow().getMainWindow();
    //@Named("softPhoneProfileFld")
    //protected LookupField softPhoneProfileFld;

    @Inject
    protected Button makeCallBtn, nextCallBtn, answerCallBtn, dropCallBtn, holdCallBtn, transferCallBtn, commitActionBtn;
    @Inject
    protected BoxLayout campaignInfo,campaignNameBox, postProcessingBox, timeToRecallBox, contactToRecallBox, phoneToRecallBox;

    @Inject
    protected UserSession userSession;
    @Inject
    protected WorkCalendarService workCalendarService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected OperatorQueueService operatorQueueService;
    @Inject
    protected CrmConfig config;

    @Override
    public void init(Map<String, Object> params) {
        if (!isInitialized) {

            callStatues = new HashMap<>();

            initCallIntegration();
            initListeners();
        }
        isTest = false;
    }

    private void initCallIntegration(){
        if (callIntegrationComponent == null) {
            callIntegrationComponent = new CallIntegrationJavaScriptComponent(this, config.getServiceAddress());
            ((AbstractOrderedLayout) WebComponentsHelper.unwrap(mainWindow)).addComponent(callIntegrationComponent);
        }
    }

    private void initListeners(){

        addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (!actionId.equals("noOperator")) {
                    targetsTimer.stop();
                    operatorQueueService.removeOperator(item.getOperator());

                    targetsDs.refresh();
                    for (CallCampaignTarget trgt : targetsDs.getItems()) {
                        trgt.setNextCallOperator(null);
                        trgt.setStatus(null);
                    }
                    targetsDs.commit();

                    item.setEndTime(new Date());
                    item.setIsActive(Boolean.FALSE);
                    item.setSoftphoneConnected(false);

                    mainDs.commit();
                }

                target = null;
                ((AbstractOrderedLayout) WebComponentsHelper.unwrap(mainWindow)).removeComponent(callIntegrationComponent);
                callIntegrationComponent = null;
                item = null;
                callStatues = null;
            }
        });

        mainDs.addListener(new DsListenerAdapter<OperatorSession>() {
            @Override
            public void valueChanged(OperatorSession source, String property, Object prevValue, Object value) {

               /*
               if ("state".equals(property)) {
                    switch((OperatorSessionStateEnum) value) {
                        case pausing:
                            break;
                        case holding:
                            break;
                        case transfering:
                            break;
                    }
                }
                */
            }
        });

        activityResult.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                ActivityResult newValue = (ActivityResult)value;
                if (newValue == null) {
                    timeToRecall.setValue(null);
                    timeToRecallBox.setVisible(false);

                    nextContact.setValue(null);
                    contactToRecallBox.setVisible(false);

                    phoneToRecall.setValue(null);
                    phoneToRecallBox.setVisible(false);

                    resultDetails.setRequired(true);
                    return;
                }

                resultDetails.setRequired(newValue.getIsNeedDetails());

                switch(newValue.getResultType()) {
                    case CALLBACKLATER:
                        timeToRecall.setValue(DateUtils.addMinutes(new Date(), 30));
                        timeToRecall.setRequired(true);
                        timeToRecallBox.setVisible(true);

                        nextContact.setValue(target.getContact());
                        nextContact.setRequired(true);
                        contactToRecallBox.setVisible(true);

                        //// TODO: 25.08.2016 Переделать на communication
                        //phoneToRecall.setValue(item.getCurrentPhoneNumber());
                        phoneToRecall.setRequired(true);
                        phoneToRecallBox.setVisible(true);

                        break;
                    case CALLBACKNOW:
                        timeToRecall.setValue(new Date());
                        timeToRecallBox.setVisible(false);

                        nextContact.setValue(null);
                        nextContact.setRequired(true);
                        contactToRecallBox.setVisible(true);

                        //// TODO: 25.08.2016 Переделать на communication
                        //phoneToRecall.setValue(item.getCurrentPhoneNumber());
                        phoneToRecall.setRequired(true);
                        phoneToRecallBox.setVisible(true);

                        break;
                    case NORESPONSE:
                        timeToRecall.setValue(null);
                        timeToRecallBox.setVisible(false);

                        nextContact.setValue(null);
                        contactToRecallBox.setVisible(false);

                        phoneToRecall.setValue(null);
                        phoneToRecallBox.setVisible(false);
                        break;

                    default:
                        timeToRecall.setValue(null);
                        timeToRecallBox.setVisible(false);

                        nextContact.setValue(null);
                        contactToRecallBox.setVisible(false);

                        phoneToRecall.setValue(null);
                        phoneToRecallBox.setVisible(false);

                        resultDetails.setRequired(true);
                        break;
                }
            }
        });

        /*softPhoneProfileFld.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                callIntegrationComponent.invokeCommand("SetActiveProfile",value.toString());
            }
        });
        */
    }

    @Override
    public void ready() {
        if (!isInitialized){
            isInitialized = true;
            inQueue = false;

            Operator operator = getOperator();

            if (operator == null || operator.getEmployee() == null) {
                //todo messages.getMessage(getClass(), "numString")
                showMessageDialog("Ошибка", "Оператор не добавлен в исходящие кампании.", MessageType.WARNING);
                this.close("noOperator");
                return;
            }

            createSession(operator);
            item = mainDs.getItem();
            item.setCountTargets(0);
            item.setIsCallEstablished(false);

            callIntegrationComponent.load();

            ToWaiting();
        }
    }

    private void createSession(Operator operator){
        OperatorSession session = metadata.create(OperatorSession.class);
        session.setOperator(operator);
        session.setIsActive(true);
        session = dataManager.commit(session);
        mainDs.setItem(session);
    }

    private void ToWaiting(){
        nextCallBtn.setEnabled(true);

        postProcessingBox.setVisible(false);
        commitActionBtn.setEnabled(false);
        campaignInfo.setVisible(false);
        campaignNameBox.setVisible(false);

        isWaiting = true;
        queuesLogin();

        item.setCurrentPhoneNumber(null);
    }

    private void queuesLogin(){
        if (item.getSoftphoneConnected() && !inQueue){
            callIntegrationComponent.invokeCommand("loginToQueues", "");
            inQueue = true;
        }
    }

    private void ToCalling(){
        nextCallBtn.setEnabled(false);
        answerCallBtn.setEnabled(false);
        commitActionBtn.setEnabled(false);

        campaignInfo.setVisible(true);
        campaignNameBox.setVisible(true);
    }

    private void ToDialing(){
        dropCallBtn.setEnabled(true);

        postProcessingBox.setVisible(true);
    }

    public void dropCallByTimer(Timer timer){
        DropCall(CallEndedStateEnum.AUTODROP);
    }

    public void NextCall(){
        if (!item.getSoftphoneConnected()) {
            showNotification(getMessage("noSoftPhoneConn"), NotificationType.HUMANIZED);
            ToWaiting();
            return;
        }
        if (targetIterator == null || !targetIterator.hasNext()) {
            showNotification(getMessage("noTargets"), NotificationType.HUMANIZED);
            ToWaiting();
            return;
        }

        isWaiting = false;
        target = targetIterator.next();
        item.setCountTargets((item.getCountTargets() < 1) ? 0 : item.getCountTargets() - 1);

        if (inQueue) {
            callIntegrationComponent.invokeCommand("exitFromQueues", "");
            return;
        }

        getNextTarget();
    }

    @Override
    public void exitFromQueues(){
        inQueue = false;
        getNextTarget();
    }

    private void getNextTarget() {
        Company company = target.getCompany();
        String comment;
        //// TODO: 14.08.2016 переделать на контакты/ Вынести в код подбора контагентов в сервисе
        if (company != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("company", company);
            contactPersonsDs.refresh(params);

            PickerField.LookupAction nextContactLookupAction = (PickerField.LookupAction)nextContact.getActionNN("lookup");
            nextContactLookupAction.setLookupScreenParams(params);

            if (target.getStatus().equals(CampaignTargetStatusEnum.PLANED) && target.getContact() != null && target.getNextCallPhone() != null) {
                LinkedHashSet<Map<String, ExtContactPerson>> phones = getPhones(target.getNextCallContact(), target.getNextCallPhone());
                phonesIterator = phones.iterator();
                getNextPhone();
                return;
            }

            if (contactPersonsDs.size() > 0) {
                LinkedHashSet<Map<String, ExtContactPerson>> phones = getPhones();
                if (phones.size() > 0) {
                    phonesIterator = phones.iterator();
                    getNextPhone();
                    return;
                } else { comment = "Отсутствуют доступные средства связи для вызова"; }
            } else { comment ="Ошибка. У ЦА нет контактов для вызова"; }
        } else { comment ="Ошибка. У ЦА не заполнено поле 'Контрагент'"; }

        showNotification(comment, NotificationType.TRAY);
        saveTarget(comment, "error");
        NextCall();
    }

    private LinkedHashSet<Map<String, ExtContactPerson>> getPhones() {
        LinkedHashSet<Map<String, ExtContactPerson>> phones = new LinkedHashSet<>();
        Collection<ExtContactPerson> contacts = contactPersonsDs.getItems();

        for(int c = 1; c < 4 && phones.size() < 4 ; c++){
            Iterator<ExtContactPerson> contactIterator = contacts.iterator();
            for(int p = 1; p < 4 && contactIterator.hasNext(); p++){
                ExtContactPerson contact = contactIterator.next();
                String num = getContactPhone(contact, c);
                HashMap<String, ExtContactPerson> commMap = getPhoneMap(contact, num);
                if (commMap != null && !commMap.isEmpty()) phones.add(commMap);
            }
        }
        return phones;
    }

    private LinkedHashSet<Map<String, ExtContactPerson>> getPhones(ExtContactPerson contact, String number) {
        LinkedHashSet<Map<String, ExtContactPerson>> phones = new LinkedHashSet<>();
        HashMap<String, ExtContactPerson> commMap = getPhoneMap(contact, number);
        if (commMap != null && !commMap.isEmpty()) phones.add(commMap);
        return phones;
    }

    private HashMap<String, ExtContactPerson> getPhoneMap(ExtContactPerson contact, String number) {
        HashMap<String, ExtContactPerson> map = new HashMap<>();
        if (StringUtils.isNotBlank(number)) map.put(number, contact);
        return map;
    }

    private String getContactPhone(ExtContactPerson person,int seq) {
        if (person == null) return null;

        String result = person.getPhone3();
        if (seq == 1) result =  person.getPhone1();
        if (seq == 2)  result = person.getPhone2();

        result = StringUtils.remove(result, "(");
        result = StringUtils.remove(result, ")");
        result = StringUtils.remove(result, "-");

        return   result;
    }

    private void saveTarget(String comment, String activityResultCode){
        target.setComment_ru(comment);
        target.setNextCallDate(null);
        target.setLastCallResult(getActivityResult(activityResultCode));
        saveTarget(CampaignTargetStatusEnum.COMPLETED, false);
    }

    private void getNextPhone() {
        if (!phonesIterator.hasNext()){
            //прибавляем 1 рабочий час если из всех средст это последнее
            Date nextTime = workCalendarService.addInterval(new Date(), 1, TimeUnit.HOUR);
            saveTarget(nextTime, getActivityResult("noresponse"),false);
            NextCall();
            return;
        }

        Map<String, ExtContactPerson> communication =  phonesIterator.next();
        String phoneNum = communication.keySet().iterator().next();
        ExtContactPerson contact = communication.get(phoneNum);

        createActivity(contact, phoneNum, CallDirectionEnum.OUTBOUND);
        Activity lastActivity = activityDs.getItem();
        if (lastActivity != null) target.setLastActivity(lastActivity);
        saveTarget(contact, phoneNum);

        fireOutgoingCall();
    }

    private void saveTarget(ExtContactPerson contact, String phone){
        target.setContact(contact);
        target.setNextCallPhone(phone);
        saveTarget(CampaignTargetStatusEnum.INVOLVED, true);
    }

    private void fireOutgoingCall() {
        String phone = target.getNextCallPhone();

        ToCalling();

        if (isTest){
            outgoingCall(getTestCallStatus(phone));
        } else {
            callIntegrationComponent.invokeCommand("MakeCall", phone);
        }
    }

    private void createActivity(ExtContactPerson contact, String phoneNumber, CallDirectionEnum callDirection){
        Activity activity = metadata.create(Activity.class);
        activity.setDirection(callDirection);
        //todo ПРОВЕРКА какой номер планировали набрать и какой вернул 3CX в активности 2 поля plan-fact???
        activity.setPhone(phoneNumber);
        activity.setOperatorSession(item);
        activity.setOwner(item.getOperator().getEmployee());
        activity.setCompany(target.getCompany());
        activity.setContact(contact);
        activity.setCampaign(target.getOutboundCampaign());
        activity.setProject((ExtProject)target.getOutboundCampaign().getProject());

        saveActivity(activity);
    }

    private void saveTarget(CampaignTargetStatusEnum state, Boolean clearComment){
        target.setStatus(state);
        target.setLastCallOperator(item.getOperator().getEmployee());
        target.setNextCallOperator(null);
        target.setLastCallDate(new Date());
        if (clearComment) target.setComment_ru("");

        dataManager.commit(target);
    }

    @Override
    public void outgoingCall(CallStatus status) {
        callStatues.put(status.getCallId(), status);

        ToDialing();
        dropCallTimer.start();

        item.setActiveCallId(status.getCallId());
        item.setCurrentPhoneNumber(status.getOtherPartyNumber());

        saveActivity(status);

        if (isTest) callEstablished(status);
    }

    private void saveActivity(CallStatus status) {
        Activity activity = activityDs.getItem();
        if (activity != null) {
            activity.setPhone(status.getOtherPartyNumber());
            activity.setCallId(status.getCallId());
            saveActivity(activity);
        }
    }

    public void ActivateCall(){
        callIntegrationComponent.invokeCommand("Activate", item.getActiveCallId());
    }

    @Override
    public void incomingCall(CallStatus status) {
        //showNotification(String.format("Ringing. From %s.", status.getOtherPartyNumber()),NotificationType.TRAY);
        //// Если в режиме ожидания то принимаем звонок
        if (!isWaiting) return;

        inQueue = false;
        isWaiting = false;
        callStatues.put(status.getCallId(), status);
        ToRinging();

        item.setActiveCallId(status.getCallId());
        item.setCurrentPhoneNumber(status.getOtherPartyNumber());

        createActivity(status, CallDirectionEnum.INBOUND);

        // Для тестирования
        //if (isTest) callEstablished(status);
    }

    private void ToRinging() {
        answerCallBtn.setEnabled(true);
        nextCallBtn.setEnabled(false);
        dropCallBtn.setEnabled(true);

        campaignInfo.setVisible(true);
        campaignNameBox.setVisible(true);
        postProcessingBox.setVisible(true);
    }

    private void createActivity(CallStatus status, CallDirectionEnum callDirection){
        Activity activity = metadata.create(Activity.class);
        activity.setDirection(callDirection);
        activity.setPhone(status.getOtherPartyNumber());
        activity.setOperatorSession(item);
        activity.setOwner(item.getOperator().getEmployee());
        activity.setCampaign(getMainIncomingCampaign());

        activity.setCallId(status.getCallId());

        //todo ???  определитель
        //activity.setCompany(target.getCompany());
        //// TODO: 15.08.2016 Можно заполнить поле или из входящей по пректу
        //activity.setProject((ExtProject)campaignsDs.getItem().getProject());

        saveActivity(activity);
    }

    @Override
    public void callEstablished(CallStatus status){
        if (!item.getActiveCallId().equals(status.getCallId())) return;

        item.setIsCallEstablished(true);
        dropCallTimer.stop();
        ToConnected();
        //callEndedState = CallEndedStateEnum.NORMALDROP;
        connectionStartTime = new Date();
    }

    private void ToConnected(){
        answerCallBtn.setEnabled(false);

        //holdCallBtn.setEnabled(true);
        //transferCallBtn.setEnabled(true);
    }

    public void MakeCall(){
        //callIntegrationComponent.invokeCommand("MakeCall",item.getCurrentPhoneNumber());
    }

    public void DropCall(){
        DropCall(CallEndedStateEnum.MANUALDROP);
    }

    private void DropCall(CallEndedStateEnum state){
        dropCallTimer.stop();

        // Для тестирования
        if (isTest) {
            callEnded(callStatues.get(item.getActiveCallId()));
        } else {
            //callEndedState = state;
            callIntegrationComponent.invokeCommand("DropCall",item.getActiveCallId());
        }
        //callStatues.remove(item.getActiveCallId());
    }

    @Override
    public void callEnded(CallStatus status) {
        if (!item.getActiveCallId().equals(status.getCallId())) return;

        callStatues.remove(status.getCallId());

        if (!status.isIncoming()) dropCallTimer.stop();

        ToCallEnded();

        if (item.getIsCallEstablished()){
            item.setIsCallEstablished(false);
            connectionEndTime = new Date();
            return;
        }  // edit call results & invoke void saveActivity by button commitActionBtn

        saveActivity(getActivityResult("noresponse"));
        activityDs.setItem(null);

        //// TODO: 28.08.2016  сбои при входящих тут можно обработать 
        if (status.isIncoming()) {
            ToWaiting();
        } else {
            getNextPhone();
        }
    }

    private void ToCallEnded() {
        dropCallBtn.setEnabled(false);
        answerCallBtn.setEnabled(false);

        //holdCallBtn.setEnabled(false);
        //transferCallBtn.setEnabled(false);

        campaignInfo.setVisible(false);
        campaignNameBox.setVisible(false);
        if (item.getIsCallEstablished()){
            commitActionBtn.setEnabled(true);
            postProcessingBox.setVisible(true);
        }

        item.setActiveCallId("");
    }

    private void saveActivity(ActivityResult result) {
        Activity activity = activityDs.getItem();
        if (activity != null) {
            Date now = new Date();
            activity.setConnectionStartTime(now);
            activity.setConnectionEndTime(now);
            activity.setResult(result);
            saveActivity(activity);
        }
    }

    public void saveActivity() {
        Activity activity = activityDs.getItem();
        if (activity != null){

            ActivityResult result = activity.getResult();
            if (result == null) {
                showNotification(getMessage("Необходимо выбрать результат звонка"), NotificationType.TRAY);
                return;
            }

            if (result.getIsNeedDetails() && StringUtils.isBlank(activity.getResultDetails())){
                showNotification(getMessage("Необходимо заполнить заполнить поле результат подробно"),NotificationType.TRAY);
                return;
            }

            boolean isCallBack = result.getResultType().equals(ActivityResultTypeEnum.CALLBACKNOW) || result.getResultType().equals(ActivityResultTypeEnum.CALLBACKLATER);
            if (isCallBack && (nextContact.getValue() == null || phoneToRecall.getValue() == null)){
                showNotification(getMessage("Необходимо указать контакное лицо и номер телефона"),NotificationType.TRAY);
                return;
            }

            //only for outbound call campaign
            if (activity.getDirection().equals(CallDirectionEnum.OUTBOUND)) saveTarget((Date)timeToRecall.getValue(), result,false);

            activity.setConnectionStartTime(connectionStartTime);
            activity.setConnectionEndTime(connectionEndTime);
            saveActivity(activity);
            activityDs.setItem(null);

            if (result.getResultType().equals(ActivityResultTypeEnum.CALLBACKNOW)) {
                getNextPhone();
            } else {
                ToWaiting();
            }
        }
    }

    private void saveTarget(Date dt, ActivityResult result, boolean isFailed){
        target.setLastCallDate(new Date());
        target.setLastCallPhone(target.getNextCallPhone());
        target.setLastCallResult(result);

        //group next... don't move before group last...
        target.setNextCallPhone(null);
        target.setNextCallDate(dt);
        target.setNextCallContact(null);
        target.setNextCallOperator(null);

        //if (lastActivity != null) target.setLastActivity(lastActivity);
        
        target.setNumberOfTries(target.getNumberOfTries() + 1);
        target.setNumberOfFailedTries(target.getNumberOfFailedTries() + ((isFailed) ? 1: 0));

        if (result.getResultType().equals(ActivityResultTypeEnum.CALLBACKNOW) || result.getResultType().equals(ActivityResultTypeEnum.CALLBACKLATER)){
            ExtContactPerson contact = nextContact.getValue();
            Communication number = phoneToRecall.getValue();

            target.setNextCallContact(contact);
            target.setNextCallPhone(number.getMainPart());
            target.setStatus(CampaignTargetStatusEnum.PLANED);
            target.setNumberOfTries(0);
        }

        if (result.getResultType().equals(ActivityResultTypeEnum.CALLBACKNOW)){
            target.setNextCallOperator(item.getOperator().getEmployee());
            LinkedHashSet<Map<String, ExtContactPerson>> phones = getPhones(target.getNextCallContact(), target.getNextCallPhone());
            phonesIterator = phones.iterator();
        }

        if (result.getResultType().equals(ActivityResultTypeEnum.NORESPONSE)){
            target.setStatus(CampaignTargetStatusEnum.PLANED);
        }

        // не переносить выше target.setNumberOfTries
        if (result.getResultType().equals(ActivityResultTypeEnum.SUCCESSFUL) || result.getResultType().equals(ActivityResultTypeEnum.UNSUCCESSFUL)
                || target.getNumberOfTries() >= target.getOutboundCampaign().getMaxAttemptCount()
           ){
            target.setStatus(CampaignTargetStatusEnum.COMPLETED);
            //target.setNextCallOperator(null);
        }

        dataManager.commit(target);
    }

    private void saveActivity(Activity activity){
        View view = metadata.getViewRepository().getView(Activity.class, "edit");

        activity.setEndTime(new Date());
        activity = dataManager.commit(activity, view);
        activityDs.setItem(activity);
    }

    @Override
    public void setSetActiveProfile(SoftphoneProfileEnum profile) {
        item.setSoftphoneProfile(profile);
    }

    public void refreshTargets(Timer timer){
        if (item != null && item.getSoftphoneConnected() && item.getCountTargets() <= config.getMinOperatorQueue() && addOperatorToQueueService){
            operatorQueueService.addOperator(item.getOperator());
            addOperatorToQueueService = false;
        }
        if (isWaiting && item.getCountTargets() < 1) {
            targetsDs.refresh();
            targetIterator = getTargetsIterator();
            addOperatorToQueueService = true;
        }
    }

    private Iterator<CallCampaignTarget> getTargetsIterator() {
        item.setCountTargets(targetsDs.size());
        return targetsDs.getItems().iterator();
    }

    public void refreshSoftPhoneConnection(Timer timer){}

    private Operator getOperator() {
        LoadContext loadContext = new LoadContext(Operator.class).setView("browse");
        loadContext.setQueryString("select e from crm$Operator e where e.employee.user.id = :user")
                .setParameter("user", userSession.getCurrentOrSubstitutedUser());
        return dataManager.load(loadContext);
    }

    private OutboundCampaign getMainIncomingCampaign() {
        LoadContext loadContext = new LoadContext(OutboundCampaign.class).setView("edit");
        loadContext.setQueryString("select e from crm$OutboundCampaign e where e.docKind.code = 'ICC' and e.docCategory.code = :code")
                .setParameter("code", config.getMainInboundCampaignCode());
        return dataManager.load(loadContext);
    }

    private ActivityResult getActivityResult(@Nonnull String code) {
        Preconditions.checkNotNullArgument(code);

        LoadContext loadContext = new LoadContext(ActivityResult.class).setView("_local");
        loadContext.setQueryString("select e from crm$ActivityResult e where e.code = :code")
                .setParameter("code", code);
        return dataManager.load(loadContext);
    }

/*
    private ActivityResult getNoresponseResult() {
        LoadContext loadContext = new LoadContext(ActivityResult.class).setView("_local");
        loadContext.setQueryString("select e from crm$ActivityResult e where e.code = :code")
                .setParameter("code", "noresponse");  //add to config - config.getMainInboundCampaignCode());
        return dataManager.load(loadContext);
    }
*/

    @Override
    public void error() {
        item.setSoftphoneConnected(false);
        addEvent("Softphone connection error", "");
    }

    public void wsConnected(){
        addEvent("3CX softphone connected", "");
        targetsTimer.start();
        item.setSoftphoneConnected(true);
        queuesLogin();
    }

    public void wsDisconnected(){
        addEvent("3CX softphone disconnected", "");
        item.setSoftphoneConnected(false);

        //Если было соединение то окрываем постобработку
        if (!isWaiting && item.getIsCallEstablished()){
            commitActionBtn.setEnabled(true);
            postProcessingBox.setVisible(true);
        }
        if (callIntegrationComponent != null) {
            addEvent("Softphone connection closing...", "");
            callIntegrationComponent.unload();

            //что тут делать???
        }
    }

    @Override
    public void waitingForNewParty(CallStatus status){
    }

    @Override
    public void tryingToTransfer(CallStatus status){
    }

    @Override
    public void addEvent(String value, String description){
        OperatorSessionEvents ev = metadata.create(OperatorSessionEvents.class);
        ev.setTimeStamp(new Date());
        ev.setOperatorSession(item);
        ev.setTitle(value);
        ev.setDescription(description);
        dataManager.commit(ev);
        getDsContext().get("eventsDs").refresh();
    }

    //// Для тестирования
    public void fireIncomingCall(){
        incomingCall(getTestCallStatus("+78122404040"));
    }

    ////Для тестирования
    private CallStatus getTestCallStatus(String phoneNum){
        final Random random = new Random();
        CallStatus callStatus = metadata.create(CallStatus.class);
        callStatus.setOtherPartyNumber(phoneNum);
        callStatus.setIsIncoming(false);
        callStatus.setCallId(Integer.toString(random.nextInt(300)+1)+":1");
        return callStatus;
    }

}