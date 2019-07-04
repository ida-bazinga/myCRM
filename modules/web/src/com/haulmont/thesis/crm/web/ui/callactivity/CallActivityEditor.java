package com.haulmont.thesis.crm.web.ui.callactivity;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.ActivityResultEnum;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import com.haulmont.thesis.crm.gui.components.CTIButtonsPanel;
import com.haulmont.thesis.crm.gui.processaction.activity.CallActivityAccessData;
import com.haulmont.thesis.crm.gui.processaction.activity.DefaultActivityAccessData;
import com.haulmont.thesis.crm.web.CrmAppUI;
import com.haulmont.thesis.crm.web.softphone.actions.*;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneSessionManager;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;
import com.haulmont.thesis.crm.web.softphone.core.listener.AbstractSoftphoneListener;
import com.haulmont.thesis.crm.web.softphone.core.listener.CallTransferListener;
import com.haulmont.thesis.crm.web.softphone.core.listener.SoftPhoneListener;
import com.haulmont.thesis.crm.web.ui.basic.edit.AbstractActivityCardEditor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

public class CallActivityEditor<T extends  CallActivity> extends AbstractActivityCardEditor<T> implements CallStatusHolder {

    protected final Log log = LogFactory.getLog(getClass());

    private boolean isContentVisible;

    protected BoxLayout header,contentBox;
    protected CTIButtonsPanel ctiButtonsPanel;
    protected SearchPickerField companyField;
    protected Label infoStatusLabel;
    protected DateField timeToRecallField;
    protected LookupPickerField campaignField, contactPersonField, contactToRecallField, phoneToRecallField;
    protected Action commitAction, windowClose;

    protected Set<Entity> toCommit = new HashSet<>();
    protected SoftPhoneListener softphoneListener;
    protected Boolean tryingToTransfer = false;
    protected SoftPhoneSession softphoneSession;
    protected CallStatus activeCall;

    protected Map<UUID, CallbackListener> callbackListeners = new HashMap<>();

    protected CollectionDatasource<CallCampaign, UUID> allCampaignsDs;
    protected CollectionDatasource<Communication, UUID> recallCommunicationsDs, communicationsDs;
    protected CollectionDatasource<ExtContactPerson, UUID> contactPersonsDs;

    @Inject
    protected Metadata metadata;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected SoftPhoneSessionManager softphoneSessionManager;

    @WindowParam(name ="mainWindowCallTransferListener")
    protected CallTransferListener mainWindowCallTransferListener;

    @Override
    protected DefaultActivityAccessData<T> createAccessData() {
        return AppBeans.getPrototype(CallActivityAccessData.NAME, getContext().getParams());
    }

    @Override
    protected CallActivityAccessData<T> getAccessData() {
        return (CallActivityAccessData<T>) accessData;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        UserSession userSession = userSessionSource.getUserSession();
        UUID softphoneSessionId = userSession.getAttribute(SoftphoneConstants.SESSION_ID_ATTR_NAME);

        softphoneSession = softphoneSessionManager.getSession(softphoneSessionId);

//      TODO: 27.02.2018 Add check user role
//      userSessionSource.getUserSession().getRoles()
//      if (roles) return;

        initRecallFieldsValueListener();
        initCtiPanel();

        PickerField.LookupAction companyLookupAction = (PickerField.LookupAction)companyField.getActionNN(PickerField.LookupAction.NAME);
        companyLookupAction.setLookupScreen("df$Company.browse");
        companyLookupAction.setLookupScreenDialogParams(getDialogParams().setWidth(1000).setHeight(700).setResizable(true));

        commitAction = getActionNN("windowCommit");
        windowClose = getActionNN("windowClose");

        if (softphoneSession != null) {
            softphoneListener = createSoftphoneListener();
            CrmAppUI.getCurrent().addSoftPhoneListener(softphoneSession.getId(), softphoneListener);
        }
        initCloseListener(softphoneSession);
    }

    protected void initCloseListener( final SoftPhoneSession session) {
        ((Window) getFrame()).addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if(session == null || softphoneListener == null) return;
                CrmAppUI.getCurrent().removeSoftPhoneListener(session.getId(), softphoneListener);
            }
        });
    }

    @Override
    public void addCallbackListener(UUID actionId, CallbackListener listener){
        callbackListeners.put(actionId, listener);
    }

    @Override
    public void removeCallbackListener(UUID actionId){
        if (!callbackListeners.isEmpty()) {
            callbackListeners.remove(actionId);
        }
    }

    @Override
    protected void findStandardComponents() {
        super.findStandardComponents();

        allCampaignsDs = getDsContext().getNN("allCampaignsDs");
        contactPersonsDs = getDsContext().getNN("contactPersonsDs");
        recallCommunicationsDs = getDsContext().getNN("recallCommunicationsDs");
        communicationsDs = getDsContext().getNN("communicationsDs");

        companyField = getComponentNN("company");
        contentBox = getComponent("contentBox");
        ctiButtonsPanel = getComponent("ctiButtonsPanel");
        infoStatusLabel = getComponentNN("infoStatusLabel");
        header = getComponentNN("header");
        campaignField = getComponentNN("campaign");
        timeToRecallField = getComponent("timeToRecall");
        contactToRecallField = getComponent("contactToRecall");
        phoneToRecallField = getComponent("phoneToRecall");
        contactPersonField = getComponentNN("contactPerson");
    }

    @Override
    protected void initDsListeners(){
        super.initDsListeners();

        getDsContext().addListener(new DsContext.CommitListener() {
            @Override
            public void beforeCommit(CommitContext context) {
                context.getCommitInstances().addAll(toCommit);
            }

            @Override
            public void afterCommit(CommitContext context, Set<Entity> result) {
            }
        });

        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                if ("result".equals(property)) {
                    applyAccessData(true);
                    ActivityRes result = (ActivityRes) value;
                    if (value == null) {
                        initFields();
                        setRequiredGridRow("details", false);
                    } else if (result.getCode().equals(ActivityResultEnum.RECALL_LATER.getId())){
                        timeToRecallField.setValue(DateUtils.addMinutes(new Date(), 30));
                        setVisibleGridRow(timeToRecallField.getId(), true);
                        setRequiredGridRow(timeToRecallField.getId(), true);

                        contactToRecallField.setValue(getItem().getContactPerson());
                        setLookupContactActions(getItem().getCompany());
                        setVisibleGridRow(contactToRecallField.getId(), true);
                        setRequiredGridRow(contactToRecallField.getId(), true);
                        refreshRecallCommunicationsDs(getItem().getContactPerson());

                        // TODO Переделать на communication
                        //phoneToRecallField.setValue(item.getCurrentPhoneNumber());
                        setVisibleGridRow(phoneToRecallField.getId(), true);
                        setRequiredGridRow(phoneToRecallField.getId(), true);
                    } else if (result.getCode().equals(ActivityResultEnum.RECALL_NOW.getId())) {
                        timeToRecallField.setValue(new Date());
                        setVisibleGridRow(timeToRecallField.getId(), false);

                        contactToRecallField.setValue(null);
                        setLookupContactActions(getItem().getCompany());
                        setVisibleGridRow(contactToRecallField.getId(), true);
                        setRequiredGridRow(contactToRecallField.getId(), true);

                        //// TODO: 25.08.2016 Переделать на communication
                        //phoneToRecallField.setValue(item.getCurrentPhoneNumber());
                        setVisibleGridRow(phoneToRecallField.getId(), true);
                        setRequiredGridRow(phoneToRecallField.getId(), true);
                    }else if (result.getCode().equals(ActivityResultEnum.ERROR.getId())) {
                        initFields();
                        setRequiredGridRow("project", false);
                        setRequiredGridRow("company", false);
                        setRequiredGridRow("contactPerson", false);
                        setRequiredGridRow("result", true);
                        setRequiredGridRow("details", true);
                        commitAction.setEnabled(true);
                    } else {
                        initFields();
                        setRequiredGridRow("details", result.getIsNeedDetails());
                    }
                }
                if ("company".equals(property)) {
                    contactPersonsDs.refresh();
                    source.setContactPerson(findContactPersonByAddress(source.getAddress()));
                    setLookupContactActions((ExtCompany) value);

                    communicationsDs.refresh();
                    source.setCommunication(findCommunicationByAddress(communicationsDs.getItems(),source.getAddress()));
                    refreshRecallCommunicationsDs(source.getContactPerson());
                }
                if ("contactPerson".equals(property)) {
                    communicationsDs.refresh();
                    source.setCommunication(findCommunicationByAddress(communicationsDs.getItems(),source.getAddress()));
                    refreshRecallCommunicationsDs(source.getContactPerson());
                }
            }
        });
    }

    protected void initRecallFieldsValueListener(){
        contactToRecallField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                if (value == null) {
                    phoneToRecallField.setValue(null);
                    phoneToRecallField.removeAction("add");
                    return;
                }
                ExtContactPerson oldValue = (ExtContactPerson) prevValue;
                ExtContactPerson currValue = (ExtContactPerson) value;
                if (currValue.equals(oldValue)) return;
                refreshRecallCommunicationsDs(currValue);
                phoneToRecallField.addAction(createAddCommunicationAction(currValue, null, false));
            }
        });
    }

    protected void refreshRecallCommunicationsDs(ExtContactPerson contactPerson){
        Map<String, Object> refreshParams = new HashMap<>();
        refreshParams.put("contactForRecall", contactPerson);
        recallCommunicationsDs.refresh(refreshParams);
    }

    @Override
    protected void postInit() {
        super.postInit();
        T item = getItem();
        if (isNewItem(item)) createActions();

        isContentVisible = (item.getCampaign() != null && StringUtils.isNotBlank(item.getCampaign().getContent())
                && item.getState().equals(ActivityStateEnum.IN_WORK.getId())
        );

        initContentBox();
        initLastActivitiesSplit(item);
        initDialogParams();
        header.setVisible(StringUtils.isNotBlank(item.getCallId()));
        initFields();
        initFooter(item);

        Button windowCloseBtn =  getComponent("windowActions.windowClose");
        if (windowCloseBtn != null) {
            windowCloseBtn.setVisible(!isContentVisible);
        }
    }

    protected void createActions(){
    }

    protected Action createAddCommunicationAction(final ExtContactPerson contact, final String address, final boolean updateCommField){
        return new BaseAction("add") {
            @Override
            public void actionPerform(Component component) {
                addNewCommunication(contact, address, updateCommField);
            }

            @Override
            public String getIcon() {
                return "icons/plus-btn.png";
            }

            @Override
            public String getCaption() {
                return null;
            }
        };
    }

    protected void addNewCommunication(final ExtContactPerson contact, String address, final boolean updateCommField){
        if (contact == null) {
            showNotification("Выберите контакт", NotificationType.HUMANIZED);
            return;
        }

        final Communication newComm = createCommunication(contact, address);

        Window editor = openEditor("crm$Communication.edit", newComm, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (updateCommField){
                    communicationsDs.refresh();
                    getItem().setCommunication(newComm);
                }else {
                    refreshRecallCommunicationsDs(contact);
                    phoneToRecallField.setValue(newComm);
                }
            }
        });
        
        phoneToRecallField.setValue(newComm);
    }

    protected void setLookupContactActions(final ExtCompany company){
        contactPersonField.removeAllActions();
        if (company != null) {

            PickerField.LookupAction recallContactLookupAction = new PickerField.LookupAction(contactToRecallField) {

                @Override
                public void afterCloseLookup(String actionId) {
                    refreshRecallCommunicationsDs((ExtContactPerson) pickerField.getValue());
                }
            };

            Map<String, Object> params = new HashMap<>();
            params.put("company", company);
            recallContactLookupAction.setLookupScreenParams(params);
            recallContactLookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
            contactToRecallField.addAction(recallContactLookupAction);
        }
        contactPersonField.addOpenAction();
    }

    @Override
    protected boolean getMainContainerEditable() {
        return getAccessData().getGlobalEditable();
    }

    @Override
    protected void activateDs(Datasource datasource){
        if (datasource.equals(allCampaignsDs)) {
            refreshCampaignsDs(getItem());
        }else{
            super.activateDs(datasource);
        }
    }

    @Override
    protected boolean preCommit() {
        T item = getItem();
        if (item.getResult() != null && (item.getResult().getCode().equals(ActivityResultEnum.RECALL_LATER.getId()) ||
                item.getResult().getCode().equals(ActivityResultEnum.RECALL_NOW.getId()))) {

            CallActivity newActivity = activityService.copyActivity(item, ActivityStateEnum.SCHEDULED, item.getOwner());
            newActivity.setEndTimePlan((Date) timeToRecallField.getValue());
            Communication comm = phoneToRecallField.getValue();
            newActivity.setAddress(comm.getMainPart());
            newActivity.setCommunication(comm);
            newActivity.setContactPerson(comm.getContactPerson());
            newActivity.setCompany((ExtCompany) newActivity.getContactPerson().getCompany());

            newActivity.setParentCard(item);
            toCommit.add(newActivity);
        }

        //TODO при звонке же новая активность будет создана???
        if (item.getKind().getCode().equals("scheduled-outgoing-call") && isNewItem(item)) {
            String newKindCode = "outgoing-call";
            ActivityKind newKind = activityService.getKindByCode(newKindCode);
            if (newKind == null)
                throw new NullPointerException(String.format("ActivityKind with code %s not found", newKindCode));

            item.setKind(newKind);
            item.setState(ActivityStateEnum.SCHEDULED);

            return true;
        }

        if (item.getCommunication() == null && item.getAddress() != null) {
            Communication newComm = findCommunicationByAddress(communicationsDs.getItems(), item.getAddress());
            if (newComm == null && getItem().getContactPerson() != null) {
                newComm = createCommunication(getItem().getContactPerson(), getItem().getAddress());
                toCommit.add(newComm);
                item.setCommunication(newComm);
            }
        }

        if (item.getKind().getCode().equals("campaign-scheduled-target")) {
            return true;
        }

        item.setState(ActivityStateEnum.COMPLETED);
        if (item.getEndTimeFact() == null) item.setEndTimeFact(timeSource.currentTimestamp());

        return true;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            toCommit.clear();
        }
        return super.postCommit(committed, close);
    }

    protected void refreshCampaignsDs(T item){
        Map<String, Object> refreshParams = new HashMap<>();
        final String INCOMING_CALL_ACTIVITY_KIND_CODE = "incoming-call";
        final String OUTGOING_CALL_ACTIVITY_KIND_CODE = "outgoing-call";
        final String INCOMING_CALL_CAMPAIGN_CODE = "IncomingCallCampaign";

        if (item.getKind().getCode().equals(INCOMING_CALL_ACTIVITY_KIND_CODE)){
            refreshParams.put("includeCode", INCOMING_CALL_CAMPAIGN_CODE);
        }

        if (item.getKind().getCode().equals(OUTGOING_CALL_ACTIVITY_KIND_CODE)){
            refreshParams.put("excludeCode", INCOMING_CALL_CAMPAIGN_CODE);
        }
        allCampaignsDs.refresh(refreshParams);
    }

    protected void initFooter(T item){
        StringBuilder sb = new StringBuilder()
                .append("<b>")
                .append(item.getLocState())
                .append("</b>");

        if (item.getOwner() != null){
            sb.append("&ensp;|&ensp;")
                    .append("<b>")
                    .append(item.getOwner().getName())
                    .append("</b>");
        }
        if (StringUtils.isNotBlank(item.getCallId())){
            sb.append("&ensp;|&ensp;")
                    .append(item.getCallId());
        }
        infoStatusLabel.setValue(sb.toString());
    }

    protected void initCtiPanel() {
        ctiButtonsPanel.init(CallActivityEditor.this);
    }

    protected void initContentBox(){
        if (contentBox == null) return;

        contentBox.setVisible(getContentVisibility());
        if (getContentVisibility()){
            getMainContainer().setWidth("490px");
        }else{
            getMainContainer().setWidth("100%");
        }
    }

    protected void initLastActivitiesSplit(T item){
        SplitPanel gridPane = getComponent("gridPane");
        BoxLayout lastActivityBox = getComponent("lastActivityBox");

        if (gridPane != null && lastActivityBox != null) {
            boolean isNewItem = isNewItem(item);

            lastActivityBox.setVisible(isNewItem);
            if (isNewItem) {
                gridPane.setLocked(false);
                gridPane.setSplitPosition(80);
            } else {
                gridPane.setSplitPosition(100);
                gridPane.setLocked(true);
            }
        }
    }

    protected void initFields(){
        if (getContentVisibility()){
            campaignField.setEditable(false);
        } else {
            campaignField.setEditable(isNewItem(getItem()));
            refreshCampaignsDs(getItem());
        }

        setVisibleGridRow("parentCard", (getItem().getParentCard() != null));

        if (isNewItem(getItem())){
            if (getItem().getCommunication() != null){
                setVisibleGridRow("communication", true);
                setRequiredGridRow("communication", true);
                setVisibleGridRow("address", false);
            } else {
                setVisibleGridRow("communication", false);
                setRequiredGridRow("communication", false);
                setVisibleGridRow("address", true);
            }
        } else {
            setVisibleGridRow("address", true);
            setVisibleGridRow("communication", false);
            setRequiredGridRow("communication", false);
        }

        timeToRecallField.setValue(null);
        setVisibleGridRow(timeToRecallField.getId(), false);

        contactToRecallField.setValue(null);
        setVisibleGridRow(contactToRecallField.getId(), false);

        phoneToRecallField.setValue(null);
        setVisibleGridRow(phoneToRecallField.getId(), false);
    }

    protected void initDialogParams(){
        if (getContentVisibility()){
            getDialogParams().setWidth(1000).setHeight(680).setResizable(true).setCloseable(false);
        }else{
            getDialogParams().setWidth(600).setHeight(680).setResizable(false).setCloseable(false);
        }
    }

    public void setContentVisibility(boolean value){
        this.isContentVisible = value;
    }

    public boolean getContentVisibility(){
        return this.isContentVisible;
    }

    @Override
    public void setCallStatus(CallStatus callStatus) {
        activeCall = callStatus;
        if (callStatus!= null && callStatus.getState().equals(CallState.Connected)){
            commitAction.setEnabled(false);
            windowClose.setEnabled(false);

            addSoftphoneAction(
                    new DropCallAction(callStatus.getCallId(), softphoneSession.getId()), true, false
            );

            Set<CallTransferListener> transferListeners = new HashSet<>();
            transferListeners.add(createCallTransferListener());
            if (mainWindowCallTransferListener != null)
                transferListeners.add(mainWindowCallTransferListener);


            addSoftphoneAction(
                    new TransferCallAction(callStatus.getCallId(), softphoneSession.getId(), this, transferListeners)
            );
            if (!callStatus.isIncoming())
                addSoftphoneAction(new DtmfAction(callStatus.getCallId(), softphoneSession.getId()));
            addSoftphoneAction(new HoldAction(callStatus.getCallId(), softphoneSession.getId(), this));
            addSoftphoneAction(new MuteAction(callStatus.getCallId(), softphoneSession.getId(), this), false, true);
        }
    }

    @Nullable
    @Override
    public CallStatus getCallStatus() {
        return activeCall;
    }

    protected void addSoftphoneAction(AbstractCallControlAction callAction) {
        addSoftphoneAction(callAction, false, false);
    }

    @SuppressWarnings("Duplicates")
    protected void addSoftphoneAction(final AbstractCallControlAction callAction, boolean clearActions, boolean refreshComponent) {
        ctiButtonsPanel.addSoftphoneAction(callAction, clearActions, refreshComponent);
        /*addCallbackListener(callAction.getActionUuid(), new CallbackListener() {
            public void invoke(CallbackMessage message){
                if (message.isSuccess()){
                    callAction.onSuccess(message.getMessageParams());
                }

                if (message.isError()){
                    log.debug(String.format("%s %s. %s", SoftphoneConstants.ERROR_CALLBACK, callAction.getId(), message.getErrorMessage()));
                    ctiButtonsPanel.clearActions();
                    ctiButtonsPanel.refreshComponent();
                    callAction.onError();
                }
            }
        });*/
    }

    protected SoftPhoneListener createSoftphoneListener() {
        return new AbstractSoftphoneListener() {

            @Override
            public void onCallback(CallbackMessage message) {
                if (softphoneSession != null && softphoneSession.getId().equals(message.getSessionId())) {
                    CallbackListener listeners = callbackListeners.get(message.getActionId());
                    if (listeners != null) {
                        listeners.invoke(message);
                    }
                    removeCallbackListener(message.getActionId());
                }
            }

            @Override
            public void onCallEnded(UUID sessionId, String callId) {
                if (tryingToTransfer) {
                    tryingToTransfer = false;
                    if (!callId.equals(activeCall.getCallId()))
                        return;
                }
                getItem().setConnectionEndTime(timeSource.currentTimestamp());

                ctiButtonsPanel.setEnabled(false);
                activeCall = null;
                softphoneSession.setActiveCall(null);
                softphoneSession.removeActiveCall(callId);
                ctiButtonsPanel.clearActions();
                ctiButtonsPanel.refreshComponent();

                commitAction.setEnabled(true);
                windowClose.setEnabled(true);
            }

            @Override
            public void onMute(UUID sessionId, String callId, Boolean isMuted) {
                if (activeCall != null && activeCall.getCallId().equals(callId) && isMuted != null) {
                    activeCall.setIsMuted(isMuted);
                }
            }

            @Override
            public void onHold(UUID sessionId, String callId, Boolean isHold) {
                if (activeCall != null && isHold != null){
                    activeCall.setIsHold(isHold);
                }
            }

            @Override
            public void onCloseSoftphone() {
                ctiButtonsPanel.setEnabled(false);
                activeCall = null;
            }
        };
    }

    protected CallTransferListener createCallTransferListener(){
        return new CallTransferListener() {
            @Override
            public void setTryingToTransfer(Boolean value, String destination) {
                commitAction.setEnabled(true);
                windowClose.setEnabled(true);

                tryingToTransfer = value;
                if (StringUtils.isNotBlank(destination)){
                    getItem().setResult(getResultByCode(ActivityResultEnum.CALL_TRANSFER));
                    getItem().setDetails(formatMessage("transferTo", destination));
                }
            }
        };
    }

    protected ActivityRes getResultByCode(@Nonnull ActivityResultEnum resultCode){
        ActivityRes result = activityService.getResultByCode(resultCode);

        if (result == null){
            log.error(String.format("ActivityResult with code %s not found", resultCode));
            throw new NullPointerException();
        }
        return result;
    }

    @Nullable
    protected ExtContactPerson findContactPersonByAddress(String address){
        if (StringUtils.isBlank(address)) return null;
        for(ExtContactPerson contact : contactPersonsDs.getItems()){
            if (findCommunicationByAddress(contact.getCommunications(), address) != null)
                return contact;
        }
        return null;
    }

    @Nullable
    protected Communication findCommunicationByAddress(Collection<Communication> communications, String address){
        if (StringUtils.isBlank(address)) return null;
        address = address.replaceAll("\\D", "");
        for (Communication comm : communications){
            if (comm.getCommKind().getCommunicationType().equals(CommunicationTypeEnum.phone) && StringUtils.isNotBlank(comm.getMainPart())
                    && comm.getMainPart().equals(address))
                return comm;
        }
        return null;
    }

    protected Communication createCommunication(ExtContactPerson contact, String address){
        Communication newComm = metadata.create(Communication.class);
        newComm.setContactPerson(contact);
        newComm.setMainPart(address);
        newComm.setCommKind(getCommKind());
        newComm.setPref(true);
        newComm.setMask("#(###)###-##-##");
        return newComm;
    }

    private CommKind getCommKind() {
        LoadContext loadContext = new LoadContext(CommKind.class).setView("_local");
        loadContext.setQueryString("select e from crm$CommKind e where e.code=:code").setParameter("code", "work");
        return getDsContext().getDataSupplier().load(loadContext);
    }
}