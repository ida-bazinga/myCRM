/*
 * Copyright (c) 2018 com.haulmont.thesis.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Kirill Khoroshilov
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_SOFT_PHONE_EVENT")
@Entity(name = "crm$SoftPhoneEvent")
public class SoftPhoneEvent extends BaseUuidEntity {
    private static final long serialVersionUID = -5023145613203558036L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "DESCRIPTION", length = 4000)
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID")
    protected SoftPhoneSession session;

    public void setSession(SoftPhoneSession session) {
        this.session = session;
    }

    public SoftPhoneSession getSession() {
        return session;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}