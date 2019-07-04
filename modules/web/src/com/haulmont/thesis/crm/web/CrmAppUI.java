package com.haulmont.thesis.crm.web;

import com.google.common.collect.Lists;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.WebConfig;
import com.haulmont.thesis.crm.entity.CallState;
import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;
import com.haulmont.thesis.crm.web.app.mainwindow.CrmMainWindow;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneMessageBroadcaster;
import com.haulmont.thesis.crm.web.softphone.core.entity.*;
import com.haulmont.thesis.crm.web.softphone.core.listener.SoftPhoneListener;
import com.haulmont.thesis.crm.web.softphone.core.parser.CallStatusDecoder;
import com.haulmont.thesis.web.ThesisAppUI;
//import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
//import com.vaadin.ui.JavaScript;
//import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
//import elemental.json.JsonArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CrmAppUI  extends ThesisAppUI implements SoftPhoneMessageBroadcaster.SoftPhoneBroadcastListener {

    private static final long serialVersionUID = -3292974452203808480L;
    private final static Log log = LogFactory.getLog(CrmAppUI.class);

    protected SoftPhoneMessageBroadcaster softPhoneMessageBroadcaster;
    protected Map<UUID, List<SoftPhoneListener>> softPhoneListeners = new ConcurrentHashMap<>();

    public static CrmAppUI getCurrent() {
        return (CrmAppUI) UI.getCurrent();
    }

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        if (AppBeans.get(Configuration.class).getConfig(WebConfig.class).getPushEnabled()) {
            softPhoneMessageBroadcaster = AppBeans.get(SoftPhoneMessageBroadcaster.NAME);
            softPhoneMessageBroadcaster.register(this);
        }


        /*
        JavaScript.getCurrent().addFunction("closeUI", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                CrmAppUI.this.close();
            }
        });

        String func = "window.onbeforeunload = function handleWindowClose(event) {closeUI(); return null;}";
        Page.getCurrent().getJavaScript().execute(func);
        */
    }

    @Override
    public void detach() {
        if (softPhoneMessageBroadcaster != null)
            softPhoneMessageBroadcaster.unregister(this);

        super.detach();
    }

    public void addSoftPhoneListener(UUID id, SoftPhoneListener messageListener) {
        List<SoftPhoneListener> listeners = softPhoneListeners.get(id);
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.add(messageListener);
        } else {
            softPhoneListeners.put(id, Lists.newArrayList(messageListener));
        }
    }

    public void removeSoftPhoneListener(UUID id, SoftPhoneListener messageListener) {
        List<SoftPhoneListener> listeners = softPhoneListeners.get(id);
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.remove(messageListener);
        }
    }

    public void removeAllSoftphoneListeners() {
        softPhoneListeners.clear();
    }

    @Override
    public void receiveBroadcast(final IMessage message) {
        SecurityContext oldSecurityContext = AppContext.getSecurityContext();
        access(new Runnable() {
            @Override
            public void run() {
                try {
                    UserSession userSession = CrmAppUI.this.getSession().getAttribute(UserSession.class);
                    if (CrmAppUI.this.getAppWindow() == null) return;
                    CrmMainWindow mainWindow = (CrmMainWindow) CrmAppUI.this.getAppWindow().getMainWindow();
                    UUID softphoneSessionId = mainWindow.getSoftphoneSessionId();

                    if (userSession != null && softphoneSessionId != null) {
                        SecurityContext newSecurityContext = new SecurityContext(userSession);
                        AppContext.setSecurityContext(newSecurityContext);
                        List<SoftPhoneListener> listeners = softPhoneListeners.get(message.getSessionId());
                        if (CollectionUtils.isNotEmpty(listeners)) {
                            for (SoftPhoneListener listener : Lists.newArrayList(listeners)) {
                                if (message instanceof ActionMessage) {
                                    listener.onAction((ActionMessage) message);
                                } else if (message instanceof CallbackMessage) {
                                    listener.onCallback((CallbackMessage) message);
                                } else if (message instanceof EventMessage) {
                                    CallStatus callStatus = null;
                                    if (message.getMessageParams() != null && !message.getMessageParams().isEmpty())
                                        callStatus = CallStatusDecoder.decode(message.getMessageParams());

                                    // TODO: 31.10.2017 Enum or constant
                                    switch (message.getName()) {
                                        case "callStatusChange":
                                            if (callStatus == null) return;
                                            //"incomingCall"
                                            if (callStatus.isIncoming() && callStatus.getState().equals(CallState.Ringing)) {
                                                listener.onRinging(message.getSessionId(), callStatus);
                                                break;
                                            }
                                            if (!callStatus.isIncoming() && callStatus.getState().equals(CallState.Dialing)) {
                                                listener.onDialing(message.getSessionId(), callStatus);
                                                break;
                                            }
                                            if (callStatus.getState().equals(CallState.Connected)) {
                                                listener.onCallConnected(message.getSessionId(), callStatus);
                                                break;
                                            }
                                            if (callStatus.getState().equals(CallState.Ended)) {
                                                listener.onCallEnded(message.getSessionId(), callStatus.getCallId());
                                                break;
                                            }
                                            if (callStatus.getState().equals(CallState.TryingToTransfer)) {
                                                listener.TryingToTransfer(message.getSessionId(), callStatus);
                                            }
                                            break;
                                        case "callHoldChange":
                                            if (callStatus == null) return;
                                            listener.onHold(message.getSessionId(), callStatus.getCallId(), callStatus.getIsHold());
                                            break;
                                        case "callMuteChange":
                                            if (callStatus == null) return;
                                            listener.onMute(message.getSessionId(), callStatus.getCallId(), callStatus.getIsMuted());
                                            break;
                                        case "currentProfileChange":
                                            if (message.getMessageParams().containsKey("Name")) {
                                                SoftPhoneProfileName profile = SoftPhoneProfileName.fromId(message.getMessageParams().get("Name"));
                                                listener.onProfileChange(message.getSessionId(), profile);
                                            }
                                            break;
                                        case "closeSoftphone":
                                            listener.onCloseSoftphone();
                                            break;
                                        case "statusChange":
                                            //Not implemented
                                            break;
                                        case "extendedStatusChange":
                                            //Not implemented
                                            break;
                                        default:
                                            listener.onEvent((EventMessage) message);
                                            break;
                                    }
                                } else if (message instanceof HeartbeatMessage) {
                                    listener.onHeartbeat((HeartbeatMessage) message);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        });
        AppContext.setSecurityContext(oldSecurityContext);
    }
}