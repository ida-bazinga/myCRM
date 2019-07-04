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
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author k.khoroshilov
 */
@Deprecated
@NamePattern("%s|title")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_OPERATOR_SESSION_EVENTS")
@Entity(name = "crm$OperatorSessionEvents")
public class OperatorSessionEvents extends BaseUuidEntity {
    private static final long serialVersionUID = -4552582444305355960L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_STAMP")
    protected Date timeStamp;

    @Column(name = "TITLE")
    protected String title;

    @Column(name = "DESCRIPTION", length = 4000)
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_SESSION_ID")
    protected OperatorSession operatorSession;

    public void setOperatorSession(OperatorSession operatorSession) {
        this.operatorSession = operatorSession;
    }

    public OperatorSession getOperatorSession() {
        return operatorSession;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }


}