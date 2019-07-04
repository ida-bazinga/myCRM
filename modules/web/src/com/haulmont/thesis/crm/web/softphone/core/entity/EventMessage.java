/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class EventMessage implements IMessage {

    private static final long serialVersionUID = 463269978949961603L;

    @JsonProperty("sessionId")
    protected UUID sessionId;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("eventParams")
    protected Map<String,String> messageParams;

    public EventMessage() {}

    public EventMessage(UUID sessionId, String name, Map<String,String> eventParams) {
        this.sessionId = sessionId;
        this.name = name;
        this.messageParams = eventParams;
    }

    public EventMessage(UUID sessionId, String name) {
        this.name = name;
        this.sessionId = sessionId;
        this.messageParams = Collections.<String, String>emptyMap();
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

    public void setName(String name) {
        this.name = name;
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
