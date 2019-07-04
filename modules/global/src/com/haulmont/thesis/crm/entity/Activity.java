/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.annotation.Listeners;

import javax.persistence.*;
import java.util.Date;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern("%s|name")
@Listeners("crm_ActivityListener")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ACTIVITY")
@Entity(name = "crm$Activity")
public class Activity extends BaseUuidEntity implements SoftDelete, Updatable {
    private static final long serialVersionUID = -3168502921025219003L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    protected Date endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected ExtEmployee owner;

    @Transient
    @MetaProperty(mandatory = true)
    protected String name;

    @Column(name = "DIRECTION")
    protected Integer direction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESULT_ID")
    protected ActivityResult result;

    @Column(name = "RESULT_DETAILS", length = 4000)
    protected String resultDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID")
    protected OutboundCampaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    protected ExtContactPerson contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMUNICATION_ID")
    protected Communication communication;

    @Column(name = "PHONE", length = 100)
    protected String phone;

    @Column(name = "REDIRECTED_TO_NUMBER", length = 100)
    protected String redirectedToNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REDIRECTED_TO_ID")
    protected ExtEmployee redirectedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_SESSION_ID")
    protected OperatorSession operatorSession;

    @Column(name = "CALL_ID", length = 20)
    protected String callId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONNECTION_START_TIME")
    protected Date connectionStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONNECTION_END_TIME")
    protected Date connectionEndTime;

    @Transient
    @MetaProperty
    protected Date preparationSeconds;

    @Transient
    @MetaProperty
    protected Date connectingSeconds;

    @Transient
    @MetaProperty
    protected Date editingSeconds;

    @Transient
    @MetaProperty
    protected Date totalSeconds;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public Date getPreparationSeconds() {
        return preparationSeconds;
    }

    public void setPreparationSeconds(Date preparationSeconds) {
        this.preparationSeconds = preparationSeconds;
    }

    public Date getConnectingSeconds() {
        return connectingSeconds;
    }

    public void setConnectingSeconds(Date connectingSeconds) {
        this.connectingSeconds = connectingSeconds;
    }

    public Date getEditingSeconds() {
        return editingSeconds;
    }

    public void setEditingSeconds(Date editingSeconds) {
        this.editingSeconds = editingSeconds;
    }

    public Date getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Date totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public void setResultDetails(String resultDetails) {
        this.resultDetails = resultDetails;
    }

    public String getResultDetails() {
        return resultDetails;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public Date getUpdateTs() {
        return updateTs;
    }

    public Boolean isDeleted() {
        return deleteTs != null;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    public Date getDeleteTs() {
        return deleteTs;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setRedirectedTo(ExtEmployee redirectedTo) {
        this.redirectedTo = redirectedTo;
    }

    public ExtEmployee getRedirectedTo() {
        return redirectedTo;
    }

    public void setRedirectedToNumber(String redirectedToNumber) {
        this.redirectedToNumber = redirectedToNumber;
    }

    public String getRedirectedToNumber() {
        return redirectedToNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public Communication getCommunication() {
        return communication;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setResult(ActivityResult result) {
        this.result = result;
    }

    public ActivityResult getResult() {
        return result;
    }

    public void setContact(ExtContactPerson contact) {
        this.contact = contact;
    }

    public ExtContactPerson getContact() {
        return contact;
    }

    public void setConnectionEndTime(Date connectionEndTime) {
        this.connectionEndTime = connectionEndTime;
    }

    public Date getConnectionEndTime() {
        return connectionEndTime;
    }

    public void setCampaign(OutboundCampaign campaign) {
        this.campaign = campaign;
    }

    public OutboundCampaign getCampaign() {
        return campaign;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public void setOperatorSession(OperatorSession operatorSession) {
        this.operatorSession = operatorSession;
    }

    public OperatorSession getOperatorSession() {
        return operatorSession;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setDirection(CallDirectionEnum direction) {
        this.direction = direction == null ? null : direction.getId();
    }

    public CallDirectionEnum getDirection() {
        return direction == null ? null : CallDirectionEnum.fromId(direction);
    }
    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public void setOwner(ExtEmployee owner) {
        this.owner = owner;
    }

    public ExtEmployee getOwner() {
        return owner;
    }

    public void setConnectionStartTime(Date connectionStartTime) {
        this.connectionStartTime = connectionStartTime;
    }

    public Date getConnectionStartTime() {
        return connectionStartTime;
    }

}