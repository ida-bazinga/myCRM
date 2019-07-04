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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.ContactPerson;
import javax.persistence.OneToOne;
import com.haulmont.cuba.core.entity.Updatable;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern(" %s|company")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CALL_CAMPAIGN_TARGET")
@Entity(name = "crm$CallCampaignTarget")
public class CallCampaignTarget extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = -3494098491436007295L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTBOUND_CAMPAIGN_ID")
    protected OutboundCampaign outboundCampaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @Column(name = "STATUS")
    protected String status;

    @Column(name = "NUMBER_OF_TRIES")
    protected Integer numberOfTries;

    @Column(name = "NUMBER_OF_FAILED_TRIES")
    protected Integer numberOfFailedTries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_CALL_OPERATOR_ID")
    protected ExtEmployee lastCallOperator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXT_CALL_OPERATOR_ID")
    protected ExtEmployee nextCallOperator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NEXT_CALL_DATE")
    protected Date nextCallDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIRST_CONTACT_ID")
    protected Communication firstContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECOND_CONTACT_ID")
    protected Communication secondContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THIRD_CONTACT_ID")
    protected Communication thirdContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXT_CALL_CONTACT_ID")
    protected ExtContactPerson nextCallContact;

    @Column(name = "NEXT_CALL_PHONE", length = 100)
    protected String nextCallPhone;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_ACTIVITY_ID")
    protected Activity lastActivity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_CALL_DATE")
    protected Date lastCallDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    protected ExtContactPerson contact;

    @Column(name = "LAST_CALL_PHONE", length = 100)
    protected String lastCallPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_CALL_RESULT_ID")
    protected ActivityResult lastCallResult;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setFirstContact(Communication firstContact) {
        this.firstContact = firstContact;
    }

    public Communication getFirstContact() {
        return firstContact;
    }

    public void setSecondContact(Communication secondContact) {
        this.secondContact = secondContact;
    }

    public Communication getSecondContact() {
        return secondContact;
    }

    public void setThirdContact(Communication thirdContact) {
        this.thirdContact = thirdContact;
    }

    public Communication getThirdContact() {
        return thirdContact;
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


    public void setLastActivity(Activity lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Activity getLastActivity() {
        return lastActivity;
    }



    public CampaignTargetStatusEnum getStatus() {
        return status == null ? null : CampaignTargetStatusEnum.fromId(status);
    }

    public void setStatus(CampaignTargetStatusEnum status) {
        this.status = status == null ? null : status.getId();
    }


    public ExtContactPerson getNextCallContact() {
        return nextCallContact;
    }

    public void setNextCallContact(ExtContactPerson nextCallContact) {
        this.nextCallContact = nextCallContact;
    }



    public void setContact(ExtContactPerson contact) {
        this.contact = contact;
    }

    public ExtContactPerson getContact() {
        return contact;
    }





    public void setOutboundCampaign(OutboundCampaign outboundCampaign) {
        this.outboundCampaign = outboundCampaign;
    }

    public OutboundCampaign getOutboundCampaign() {
        return outboundCampaign;
    }





    public void setLastCallResult(ActivityResult lastCallResult) {
        this.lastCallResult = lastCallResult;
    }

    public ActivityResult getLastCallResult() {
        return lastCallResult;
    }




    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setNumberOfTries(Integer numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    public Integer getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfFailedTries(Integer numberOfFailedTries) {
        this.numberOfFailedTries = numberOfFailedTries;
    }

    public Integer getNumberOfFailedTries() {
        return numberOfFailedTries;
    }

    public void setLastCallOperator(ExtEmployee lastCallOperator) {
        this.lastCallOperator = lastCallOperator;
    }

    public ExtEmployee getLastCallOperator() {
        return lastCallOperator;
    }

    public void setLastCallPhone(String lastCallPhone) {
        this.lastCallPhone = lastCallPhone;
    }

    public String getLastCallPhone() {
        return lastCallPhone;
    }

    public void setLastCallDate(Date lastCallDate) {
        this.lastCallDate = lastCallDate;
    }

    public Date getLastCallDate() {
        return lastCallDate;
    }

    public void setNextCallDate(Date nextCallDate) {
        this.nextCallDate = nextCallDate;
    }

    public Date getNextCallDate() {
        return nextCallDate;
    }

    public void setNextCallPhone(String nextCallPhone) {
        this.nextCallPhone = nextCallPhone;
    }

    public String getNextCallPhone() {
        return nextCallPhone;
    }

    public void setNextCallOperator(ExtEmployee nextCallOperator) {
        this.nextCallOperator = nextCallOperator;
    }

    public ExtEmployee getNextCallOperator() {
        return nextCallOperator;
    }


}