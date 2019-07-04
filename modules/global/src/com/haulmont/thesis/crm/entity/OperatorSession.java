/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;

/**
 * @author k.khoroshilov
 */
@Deprecated
@NamePattern("%s %s %s|createdBy,createTs,isActive")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_OPERATOR_SESSION")
@Entity(name = "crm$OperatorSession")
public class OperatorSession extends BaseUuidEntity {
    private static final long serialVersionUID = -2057091556132890710L;

    @Column(name = "IS_ACTIVE")
    protected Boolean isActive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    protected Date endTime;

    @Column(name = "SOFT_PHONE_PROFILE")
    protected String softphoneProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_ID")
    protected Operator operator;

    @OneToMany(mappedBy = "operatorSession")
    protected List<Activity> activities;

    @Composition
    @OneToMany(mappedBy = "operatorSession")
    protected List<OperatorSessionEvents> events;

    @Transient
    @MetaProperty(mandatory = true)
    protected Boolean softphoneConnected = false;

    @Transient
    @MetaProperty
    protected Integer countTargets;

    @Transient
    @MetaProperty
    protected String activeCallId;

    @Transient
    @MetaProperty
    protected String currentPhoneNumber;

    @Transient
    @MetaProperty
    protected Boolean isCallEstablished;

    public void setCurrentPhoneNumber(String currentPhoneNumber) {
        this.currentPhoneNumber = currentPhoneNumber;
    }

    public String getCurrentPhoneNumber() {
        return currentPhoneNumber;
    }

    public void setCountTargets(Integer countTargets) {
        this.countTargets = countTargets;
    }

    public Integer getCountTargets() {
        return countTargets;
    }

    public void setIsCallEstablished(Boolean isCallEstablished) {
        this.isCallEstablished = isCallEstablished;
    }

    public Boolean getIsCallEstablished() {
        return isCallEstablished;
    }

    public void setSoftphoneConnected(Boolean softphoneConnected) {
        this.softphoneConnected = softphoneConnected;
    }

    public Boolean getSoftphoneConnected() {
        return softphoneConnected;
    }

    public void setEvents(List<OperatorSessionEvents> events) {
        this.events = events;
    }

    public List<OperatorSessionEvents> getEvents() {
        return events;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActiveCallId(String activeCallId) {
        this.activeCallId = activeCallId;
    }

    public String getActiveCallId() {
        return activeCallId;
    }

    public void setSoftphoneProfile(SoftphoneProfileEnum softphoneProfile) {
        this.softphoneProfile = softphoneProfile == null ? null : softphoneProfile.getId();
    }

    public SoftphoneProfileEnum getSoftphoneProfile() {
        return softphoneProfile == null ? null : SoftphoneProfileEnum.fromId(softphoneProfile);
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

}