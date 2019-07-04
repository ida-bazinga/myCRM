/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Versioned;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.crm.core.app.worker.CommunicationWorker;

import javax.persistence.*;
import java.util.Date;
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author k.khoroshilov
 */
@Listeners("crm_CommunicationEntityListener")
@NamePattern("%s|maskedAddress")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COMMUNICATION")
@Entity(name = "crm$Communication")
public class Communication extends BaseUuidEntity implements Versioned, SoftDelete, Updatable {
    private static final long serialVersionUID = 299414561237464748L;

    @Deprecated
    @Column(name = "ADDRESS", length = 100)
    protected String address;

    @Column(name = "MAIN_PART", length = 100)
    protected String mainPart;

    @Column(name = "ADDITIONAL_PART", length = 10)
    protected String additionalPart;

    @Column(name = "PREF")
    protected Boolean pref;

    @Column(name = "NOTE_RU")
    protected String note_ru;

    @Column(name = "PRIORITY")
    protected Integer priority;

    @Column(name = "MASK")
    protected String mask;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMM_KIND_ID")
    protected CommKind commKind;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONTACT_PERSON_ID")
    protected ExtContactPerson contactPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_STATUS_ID")
    protected EmailStatus emailStatus;

    @Version
    @Column(name = "VERSION")
    protected Integer version;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setCommKind(CommKind commKind) {
        this.commKind = commKind;
    }

    public CommKind getCommKind() {
        return commKind;
    }

    public void setEmailStatus(EmailStatus emailStatus) {
        this.emailStatus = emailStatus;
    }

    public EmailStatus getEmailStatus() {
        return emailStatus;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getMask() {
        return mask;
    }

    public void setMainPart(String mainPart) {
        this.mainPart = mainPart;
    }

    public String getMainPart() {
        return mainPart;
    }

    public void setAdditionalPart(String additionalPart) {
        this.additionalPart = additionalPart;
    }

    public String getAdditionalPart() {
        return additionalPart;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setNote_ru(String note_ru) {
        this.note_ru = note_ru;
    }

    public String getNote_ru() {
        return note_ru;
    }

    public void setContactPerson(ExtContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public ExtContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setPref(Boolean pref) {
        this.pref = pref;
    }

    public Boolean getPref() {
        return pref;
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

    public Integer getVersion() {
        return version;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @MetaProperty(related = {"mainPart", "additionalPart", "mask", "commKind"})
    public String getMaskedAddress() {
        CommunicationWorker worker = AppBeans.get(CommunicationWorker.NAME);
        return worker.getMaskedAddress(getMainPart(), getAdditionalPart(), getMask(), getCommKind());
    }
}