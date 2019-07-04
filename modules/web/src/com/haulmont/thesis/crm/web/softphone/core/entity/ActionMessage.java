/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.cuba.core.global.UuidProvider;

import java.util.Map;
import java.util.UUID;

public class ActionMessage implements IActionMessage {

    private static final long serialVersionUID = 2386690348814196974L;

    @JsonProperty("sessionId")
    protected UUID sessionId;

    @JsonProperty("actionId")
    protected UUID actionId;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("actionParams")
    protected Map<String,String> messageParams;

    public ActionMessage() {}

    public ActionMessage(UUID sessionId, String name, Map<String,String> actionParams) {
        this.sessionId = sessionId;
        this.actionId = UuidProvider.createUuid();
        this.name = name;
        this.messageParams = actionParams;
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
}
