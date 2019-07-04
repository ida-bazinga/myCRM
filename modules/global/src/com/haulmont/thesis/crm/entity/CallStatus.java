/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.thesis.crm.enums.OriginatorType;

/**
 * @author k.khoroshilov
 */
@MetaClass(name = "crm$CallStatus")

public class CallStatus extends AbstractNotPersistentEntity implements IEncodable{
    private static final long serialVersionUID = -7326217388632966681L;

    @JsonProperty("callId")
    @MetaProperty
    protected String callId;

    @JsonProperty("isIncoming")
    @MetaProperty
    protected Boolean isIncoming;

    @JsonProperty("state")
    @MetaProperty
    protected Integer state;

    @JsonProperty("otherPartyNumber")
    @MetaProperty
    protected String otherPartyNumber;

    @JsonProperty("otherPartyName")
    @MetaProperty
    protected String otherPartyName;

    @JsonProperty("originatorType")
    @MetaProperty
    protected Integer originatorType;

    @JsonProperty("originator")
    @MetaProperty
    protected String originator;

    @JsonProperty("originatorName")
    @MetaProperty
    protected String originatorName;

    @JsonProperty("isHold")
    @MetaProperty
    protected Boolean isHold;

    @JsonProperty("isMuted")
    @MetaProperty
    protected Boolean isMuted;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public void setState(CallState state) {
        this.state = state == null ? null : state.getId();
    }

    public CallState getState() {
        return state == null ? null : CallState.fromId(state);
    }

    public void setOtherPartyNumber(String otherPartyNumber) {
        this.otherPartyNumber = otherPartyNumber;
    }

    public String getOtherPartyNumber() {
        return otherPartyNumber;
    }

    public void setOtherPartyName(String otherPartyName) {
        this.otherPartyName = otherPartyName;
    }

    public String getOtherPartyName() {
        return otherPartyName;
    }

    public void setOriginatorType(OriginatorType originatorType) {
        this.originatorType = originatorType == null ? null : originatorType.getId();
    }

    public OriginatorType getOriginatorType() {
        return originatorType == null ? null : OriginatorType.fromId(originatorType);
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setIsHold(Boolean isHold) {
        this.isHold = isHold;
    }

    public Boolean getIsHold() {
        return isHold;
    }

    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
    }

    public Boolean getIsMuted() {
        return isMuted;
    }

    public void setIsIncoming(Boolean isIncoming) {
        this.isIncoming = isIncoming;
    }

    public Boolean isIncoming() {
        return isIncoming;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CallStatus that = (CallStatus) o;

        return !(getCallId() != null ? !getCallId().equals(that.getCallId()) : that.getCallId() != null);
    }
}