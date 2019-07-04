/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_ACCESS_CONTROL_LOG")
@Entity(name = "crm$AccessControlLog")
public class AccessControlLog extends BaseUuidEntity {
    private static final long serialVersionUID = -9101400846297751651L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ARRIVAL", nullable = false)
    protected Date dateArrival;

    @Column(name = "ACCESS_CARD", nullable = false, length = 50)
    protected String accessCard;

    @Column(name = "MESSAGE_CODE", nullable = false)
    protected Integer messageCode;

    @Column(name = "SOURCE", nullable = false, length = 250)
    protected String source;

    public void setMessageCode(AccessCode messageCode) {
        this.messageCode = messageCode == null ? null : messageCode.getId();
    }

    public AccessCode getMessageCode() {
        return messageCode == null ? null : AccessCode.fromId(messageCode);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }


    public void setDateArrival(Date dateArrival) {
        this.dateArrival = dateArrival;
    }

    public Date getDateArrival() {
        return dateArrival;
    }

    public void setAccessCard(String accessCard) {
        this.accessCard = accessCard;
    }

    public String getAccessCard() {
        return accessCard;
    }


}