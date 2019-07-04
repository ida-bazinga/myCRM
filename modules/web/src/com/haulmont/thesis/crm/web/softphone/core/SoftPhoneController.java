package com.haulmont.thesis.crm.web.softphone.core;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UuidProvider;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.LoginService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;
import com.haulmont.thesis.crm.web.softphone.core.entity.EventMessage;
import com.haulmont.thesis.crm.web.softphone.core.entity.IMessage;
import com.haulmont.thesis.crm.web.softphone.core.listener.SoftphoneActionListener;
import com.haulmont.thesis.crm.web.softphone.core.parser.*;
import com.haulmont.thesis.web.sys.security.WebAnonymousSessionHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atmosphere.config.service.*;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.BroadcasterFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.atmosphere.cpr.ApplicationConfig.HEARTBEAT_PADDING_CHAR;

@org.atmosphere.config.service.Singleton
@ManagedService(path = SoftPhoneController.PATH, atmosphereConfig = HEARTBEAT_PADDING_CHAR + "=" + SoftPhoneController.PADDING )
public class SoftPhoneController implements SoftphoneActionListener {

    final static String PATH = "/softphone";
    final static String PADDING = "thEsiS";

    private final static Log log = LogFactory.getLog(SoftPhoneController.class);
    private static BroadcasterFactory factory;
    private static AtmosphereResourceFactory resourceFactory;

    protected SoftPhoneSessionManager softPhoneSessionManager;
    protected SoftPhoneActionManager softPhoneActionManager;
    protected SoftPhoneMessageBroadcaster softPhoneMessageBroadcaster;

    @PostConstruct
    public void init() {
        try {
            softPhoneSessionManager = AppBeans.get(SoftPhoneSessionManager.class);
            softPhoneMessageBroadcaster = AppBeans.get(SoftPhoneMessageBroadcaster.class);
            softPhoneActionManager = AppBeans.get(SoftPhoneActionManager.class);
            softPhoneActionManager.setListener(this);
            log.info("SoftPhoneController ready");
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
    }

    @PreDestroy
    public void destroy(){
        softPhoneActionManager.removeListener();
        log.info("SoftPhoneController finalize");
    }

    @Ready
    @DeliverTo(DeliverTo.DELIVER_TO.RESOURCE)
    public String onReady(AtmosphereResource r) {
        factory = r.getAtmosphereConfig().getBroadcasterFactory();
        resourceFactory = r.getAtmosphereConfig().resourcesFactory();

        log.info("Client " + r.uuid() + " connected. BroadcasterFactory used " + factory.getClass().getName() +
                "Broadcaster injected " + r.getBroadcaster().getID());

        //emulate Heartbeat
        return String.format("%d|%s", PADDING.length(), PADDING);
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            log.info("Browser "+ event.getResource().uuid() +" unexpectedly disconnected");
        } else if (event.isClosedByClient()) {
            log.info("Browser " + event.getResource().uuid() + " closed the connection");
        }
        try {
            AppContext.setSecurityContext(new SecurityContext(getAnonymousSession()));
            softPhoneMessageBroadcaster.broadcast(new EventMessage(UuidProvider.fromString(event.getResource().uuid()), SoftphoneConstants.CLOSE_SP));
            softPhoneSessionManager.removeSession(event.getResource().uuid());
        } finally {
            closeAnonymousSession();
            AppContext.setSecurityContext(null);
        }
    }

    @Message(encoders = {MessageEncoder.class},
            decoders = {ActionMessageDecoder.class, CallbackMessageDecoder.class, HeartbeatMessageDecoder.class})
    public void onMessage(IMessage message) throws IOException {
        int lastDot = message.getClass().getName().lastIndexOf(".");
        log.debug("SessionId: " + message.getSessionId() +", Type: " + message.getClass().getName().substring(lastDot+1) + ", Name: " + message.getName());
        softPhoneMessageBroadcaster.broadcast(message);
    }

    @Message(decoders = {EventMessageDecoder.class})
    public void onEvent(EventMessage message) throws IOException {
        log.debug("SessionId: " + message.getSessionId() + ", EventName: " + message.getName());
        if (message.getName().equals("initSoftPhoneParam")){
            createSoftphoneSession(message.getSessionId(),  message.getMessageParams());
            return;
        }
        softPhoneMessageBroadcaster.broadcast(message);
    }

    protected void createSoftphoneSession(UUID sessionId, Map<String, String> messageParams) throws IOException {
        AtmosphereResource r = getResource(sessionId);
        if (r == null){
            log.error("Atmosphere resource with id " + sessionId + " not found");
            return;
        }

        try {
            AppContext.setSecurityContext(new SecurityContext(getAnonymousSession()));
            softPhoneSessionManager.addSession(sessionId, messageParams);
        }catch (Exception ex){
            log.error(ex.getMessage());
            r.resume();
            r.close();
        } finally {
            closeAnonymousSession();
            AppContext.setSecurityContext(null);
        }
    }

    @Override
    public void invokeAction(ActionMessage message) {
        AtmosphereResource r = getResource(message.getSessionId());
        if (r == null){
            log.error("Atmosphere resource with id " + message.getSessionId() + " not found");
            return;
        }

        MessageEncoder parser = new MessageEncoder();
        String json = parser.encode(message);

        r.getBroadcaster().broadcast(json);
        log.debug("Receive message. " + json);
    }

    protected AtmosphereResource getResource(UUID sessionId){
        if (resourceFactory == null) {
            log.error("ResourceFactory is null");
            return null;
        }

        return resourceFactory.find(sessionId.toString());
    }

    protected UserSession getAnonymousSession(){
        WebAnonymousSessionHolder webAnonSessionHolder = AppBeans.get(WebAnonymousSessionHolder.class);
        return webAnonSessionHolder.getSession();
    }

    protected void closeAnonymousSession(){
        LoginService loginService = AppBeans.get(LoginService.class);
        loginService.logout();
    }

}