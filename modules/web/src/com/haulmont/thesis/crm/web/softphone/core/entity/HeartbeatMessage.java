/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class HeartbeatMessage implements IMessage {

    @JsonProperty("sessionId")
    protected UUID sessionId;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("heartbeatParams")
    protected Map<String,String> messageParams;

    public HeartbeatMessage() {
        this.name = "heartbeat";
    }

    public HeartbeatMessage(UUID sessionId, Map<String,String> heartbeatParams) {
        this();
        this.sessionId = sessionId;
        this.messageParams = heartbeatParams;
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
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getMessageParams() {
        return messageParams;
    }

    @Override
    public void setMessageParams(Map<String, String> messageParams) {
        this.messageParams = messageParams;
    }
}
