/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;

import javax.persistence.UniqueConstraint;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|title")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_INFORMATION_SOURCE")
@Entity(name = "crm$InformationSource")
@Listeners("crm_InformationSourceListener")
public class InformationSource extends BaseUuidEntity {
    private static final long serialVersionUID = 4195788261929974965L;

    @Column(name = "INFO_TYPE", nullable = false)
    protected Integer infoType;

    @Column(name = "TITLE", length = 500)
    protected String title;

    @Column(name = "NOTE", length = 1000)
    protected String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEB_ADDRESS_ID")
    protected WebAddress webAddress;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public void setWebAddress(WebAddress webAddress) {
        this.webAddress = webAddress;
    }

    public WebAddress getWebAddress() {
        return webAddress;
    }


    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }


    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }


    public void setInfoType(InfoSourceTypeEnum infoType) {
        this.infoType = infoType == null ? null : infoType.getId();
    }

    public InfoSourceTypeEnum getInfoType() {
        return infoType == null ? null : InfoSourceTypeEnum.fromId(infoType);
    }


}