/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.phoneintegration.web;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.thesis.crm.entity.CallState;
import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.entity.SoftphoneProfileEnum;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import elemental.json.impl.JreJsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created by k.khoroshilov on 11.04.2016.
 */

@JavaScript({"vaadin://resources/js/call-integration-ws.js"})
public class CallIntegrationJavaScriptComponent extends AbstractJavaScriptComponent {

    Log log = LogFactory.getLog(CallIntegrationJavaScriptComponent.class);

    @Override
    protected CallIntegrationComponentState getState() {
        return (CallIntegrationComponentState)super.getState();
    }

    protected CallIntegrationWindowListener parent;
    protected String address;

    @SuppressWarnings("unchecked")
    public CallIntegrationJavaScriptComponent(final CallIntegrationWindowListener windowListener, @Nonnull String wsAddress){
        Preconditions.checkNotNullArgument(wsAddress);

        this.parent = windowListener;

        this.address = wsAddress;

        addFunction("setActiveProfile", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    String profileName = arguments.getString(0);

                    parent.setSetActiveProfile(SoftphoneProfileEnum.valueOf(profileName));
                }
            }
        });

        addFunction("exitFromQueues", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                parent.exitFromQueues();
            }
        });

        addFunction("setResult", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    String value = arguments.getString(0);
                    if (value != null) {
                        parent.addEvent(value,"");
                        //log.info(value);
                    }
                }
            }
        });

        addFunction("incomingCall", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                    Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    CallStatus status = getCallStatus(map);
                    parent.incomingCall(status);
                }
            }
        });

        addFunction("outgoingCall", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                    Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    CallStatus status = getCallStatus(map);
                    parent.outgoingCall(status);
                }
            }
        });

        addFunction("callEstablished", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                    Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    CallStatus status = getCallStatus(map);
                    parent.callEstablished(status);
                }
            }
        });

        addFunction("waitingForNewParty", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                    Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    CallStatus status = getCallStatus(map);
                    parent.waitingForNewParty(status);
                }
            }
        });

        addFunction("tryingToTransfer", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                    Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    CallStatus status = getCallStatus(map);
                    parent.tryingToTransfer(status);
                }
            }
        });

        addFunction("callEnded", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                    Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    CallStatus status = getCallStatus(map);
                    parent.callEnded(status);
                }
            }
        });

        addFunction("wsConnected", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                parent.wsConnected();
            }
        });

        addFunction("wsDisconnected", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                parent.wsDisconnected();
                unload();
            }
        });

        addFunction("error", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                parent.error();
                log.error("Softphone connection error");
            }
        });
    }

    public String getMessage(){
        return getState().message;
    }

    public void load(){
        callFunction("load", address);
    }

    public void unload(){
        callFunction("unload");
    }

    protected void setServiceCommand(String title, String param1, String param2){
        ServiceCommand command = new ServiceCommand();
        command.setTitle(title);
        command.setParam1(param1);
        command.setParam2(param2);

        getState().command = command;
    }

    @Override
    protected void callFunction(String name, Object... arguments) {
        super.callFunction(name, arguments);
    }

    public void invokeCommand(String title, String param1){
        invokeCommand(title, param1, "");
    }

    public void invokeCommand(String title, String param1, String param2){
        setServiceCommand(title, param1, param2);
        callFunction("invokeCommand", getState().command);
    }

    protected CallStatus getCallStatus(Map<String, Object> callInfo){
        CallStatus status = new CallStatus();

        Double stateId =(Double)callInfo.get("state");
        CallState state = CallState.fromId(stateId.intValue());

        status.setCallId(callInfo.get("callId").toString());
        status.setOtherPartyNumber(callInfo.get("otherPartyNumber").toString());
        status.setIsIncoming((Boolean)callInfo.get("isIncoming"));
        status.setOtherPartyName(callInfo.get("otherPartyName").toString());
        status.setState(state);

        return status;
    }
}


