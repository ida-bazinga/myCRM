/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.crm.enums.CallModeEnum;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author Kirill Khoroshilov
 */
@Listeners({"crm_CallCampaignEntityListener"})
@NamePattern("%s|description")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("200")
@Table(name = "CRM_CALL_CAMPAIGN")
@Entity(name = "crm$CallCampaign")
public class CallCampaign extends BaseCampaign {
    private static final long serialVersionUID = -2269269749862947508L;

    @Column(name = "CALL_MODE")
    protected String callMode;

    @Column(name = "MAX_ATTEMPT_COUNT")
    protected Integer maxAttemptCount = 3;

    @Column(name = "MAX_SIZE_OF_QUEUE")
    protected Integer maxSizeOfQueue = 10;

    @Column(name = "MIN_SIZE_OF_QUEUE")
    protected Integer minSizeOfQueue = 2;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "campaign")
    protected List<CallCampaignTrgt> targets;

    @JoinTable(name = "CRM_CALL_CAMPAIGN_EMPLOYEE_LINK",
            joinColumns = @JoinColumn(name = "CALL_CAMPAIGN_ID"),
            inverseJoinColumns = @JoinColumn(name = "EMPLOYEE_ID"))
    @ManyToMany
    protected Set<ExtEmployee> operators;

    public void setTargets(List<CallCampaignTrgt> targets) {
        this.targets = targets;
    }

    public List<CallCampaignTrgt> getTargets() {
        return targets;
    }

    public void setOperators(Set<ExtEmployee> operators) {
        this.operators = operators;
    }

    public Set<ExtEmployee> getOperators() {
        return operators;
    }

    public void setCallMode(CallModeEnum callMode) {
        this.callMode = callMode == null ? null : callMode.getId();
    }

    public CallModeEnum getCallMode() {
        return callMode == null ? null : CallModeEnum.fromId(callMode);
    }

    public void setMaxAttemptCount(Integer maxAttemptCount) {
        this.maxAttemptCount = maxAttemptCount;
    }

    public Integer getMaxAttemptCount() {
        return maxAttemptCount;
    }

    public void setMaxSizeOfQueue(Integer maxSizeOfQueue) {
        this.maxSizeOfQueue = maxSizeOfQueue;
    }

    public Integer getMaxSizeOfQueue() {
        return maxSizeOfQueue;
    }

    public void setMinSizeOfQueue(Integer minSizeOfQueue) {
        this.minSizeOfQueue = minSizeOfQueue;
    }

    public Integer getMinSizeOfQueue() {
        return minSizeOfQueue;
    }

    @PostConstruct
    protected void init() {
        setCallMode(CallModeEnum.AUTOMATIC);
    }
}