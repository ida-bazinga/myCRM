/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|callId")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CDR_LOG")
@Entity(name = "crm$CdrLog")
public class CdrLog extends BaseUuidEntity {
    private static final long serialVersionUID = -70265298372119265L;

    @Column(name = "CALL_ID", length = 100)
    protected String callId;

    @Temporal(TemporalType.TIME)
    @Column(name = "DURATION")
    protected Date duration;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    protected Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ANSWERED_TIME")
    protected Date answeredTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    protected Date endTime;

    @Column(name = "REASON_TERMINATED", length = 250)
    protected String reasonTerminated;

    @Column(name = "FROM_NUMBER", length = 100)
    protected String fromNumber;

    @Column(name = "TO_NUMBER", length = 100)
    protected String toNumber;

    @Column(name = "FROM_DN", length = 100)
    protected String fromDn;

    @Column(name = "TO_DN", length = 100)
    protected String toDn;

    @Column(name = "DIAL_NUMBER", length = 100)
    protected String dialNumber;

    @Column(name = "REASON_CHANGED", length = 500)
    protected String reasonChanged;

    @Column(name = "FINAL_NUMBER", length = 100)
    protected String finalNumber;

    @Column(name = "FINAL_DN", length = 100)
    protected String finalDn;

    @Column(name = "CHAIN", length = 500)
    protected String chain;

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public Date getDuration() {
        return duration;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setAnsweredTime(Date answeredTime) {
        this.answeredTime = answeredTime;
    }

    public Date getAnsweredTime() {
        return answeredTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setReasonTerminated(String reasonTerminated) {
        this.reasonTerminated = reasonTerminated;
    }

    public String getReasonTerminated() {
        return reasonTerminated;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setFromDn(String fromDn) {
        this.fromDn = fromDn;
    }

    public String getFromDn() {
        return fromDn;
    }

    public void setToDn(String toDn) {
        this.toDn = toDn;
    }

    public String getToDn() {
        return toDn;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setReasonChanged(String reasonChanged) {
        this.reasonChanged = reasonChanged;
    }

    public String getReasonChanged() {
        return reasonChanged;
    }

    public void setFinalNumber(String finalNumber) {
        this.finalNumber = finalNumber;
    }

    public String getFinalNumber() {
        return finalNumber;
    }

    public void setFinalDn(String finalDn) {
        this.finalDn = finalDn;
    }

    public String getFinalDn() {
        return finalDn;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getChain() {
        return chain;
    }


    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }


}