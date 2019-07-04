/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;

import java.util.Map;
import java.util.UUID;

public class CallbackMessage implements IActionMessage {

    private static final long serialVersionUID = -6576467743496759495L;

    @JsonProperty("sessionId")
    protected UUID sessionId;

    @JsonProperty("actionId")
    protected UUID actionId;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("callbackParams")
    protected Map<String,String> messageParams;

    @JsonProperty("error")
    protected String ErrorMessage;

    public CallbackMessage(){}

    protected CallbackMessage(String name, UUID sessionId, UUID actionId) {
        this.name = name;
        this.sessionId = sessionId;
        this.actionId = actionId;
    }

    public CallbackMessage(UUID sessionId, UUID actionId, Map<String,String> callbackParams) {
        this(SoftphoneConstants.SUCCESS_CALLBACK, sessionId, actionId);
        this.messageParams = callbackParams;
    }

    public CallbackMessage(UUID sessionId, UUID actionId, String errorMessage) {
        this(SoftphoneConstants.ERROR_CALLBACK, sessionId, actionId);
        this.ErrorMessage = errorMessage;
    }

    @Override
    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public UUID getActionId() {
        return actionId;
    }

    public void setActionId(UUID actionId) {
        this.actionId = actionId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String,String> getMessageParams() {
        return messageParams;
    }

    @Override
    public void setMessageParams(Map<String,String> messageParams) {
        this.messageParams = messageParams;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return getName().equals(SoftphoneConstants.SUCCESS_CALLBACK);
    }

    public boolean isError() {
        return getName().equals(SoftphoneConstants.ERROR_CALLBACK);
    }
}
