/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Kirill Khoroshilov
 */
@Listeners("crm_CallCampaignTargetEntityListener")
@NamePattern("%s|name")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CALL_CAMPAIGN_TRGT")
@Entity(name = "crm$CallCampaignTrgt")
public class CallCampaignTrgt extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = -3405553678215058046L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "STATE")
    protected String state;

    @Column(name = "COUNT_TRIES")
    protected Integer countTries = 0;

    @Column(name = "COUNT_FAILED_TRIES")
    protected Integer countFailedTries = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID")
    protected CallCampaign campaign;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMUNICATION_ID")
    protected Communication communication;

    @Column(name = "PRIORITY")
    protected Integer priority;

    @Column(name = "IS_GROUP", nullable = false)
    protected Boolean isGroup = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected CallCampaignTrgt parent;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_ACTIVITY_ID")
    protected CallActivity lastActivity;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXT_ACTIVITY_ID")
    protected CallActivity nextActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_ID")
    protected ExtEmployee operator;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;
    public ExtEmployee getOperator() {
        return operator;
    }

    public void setOperator(ExtEmployee operator) {
        this.operator = operator;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public ActivityStateEnum getState() {
        return state == null ? null : ActivityStateEnum.fromId(state);
    }

    public void setState(ActivityStateEnum state) {
        this.state = state == null ? null : state.getId();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setParent(CallCampaignTrgt parent) {
        this.parent = parent;
    }

    public CallCampaignTrgt getParent() {
        return parent;
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public Communication getCommunication() {
        return communication;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setCountTries(Integer countTries) {
        this.countTries = countTries;
    }

    public Integer getCountTries() {
        return countTries;
    }

    public void setCountFailedTries(Integer countFailedTries) {
        this.countFailedTries = countFailedTries;
    }

    public Integer getCountFailedTries() {
        return countFailedTries;
    }

    public void setCampaign(CallCampaign campaign) {
        this.campaign = campaign;
    }

    public CallCampaign getCampaign() {
        return campaign;
    }

    public void setLastActivity(CallActivity lastActivity) {
        this.lastActivity = lastActivity;
    }

    public CallActivity getLastActivity() {
        return lastActivity;
    }

    public void setNextActivity(CallActivity nextActivity) {
        this.nextActivity = nextActivity;
    }

    public CallActivity getNextActivity() {
        return nextActivity;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }
}