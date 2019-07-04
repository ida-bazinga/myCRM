/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */



package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.thesis.core.entity.Company;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern("%s (%s)|title,link")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_WEB_ADDRESS")
@Entity(name = "crm$WebAddress")
public class WebAddress extends BaseUuidEntity {
    private static final long serialVersionUID = 3710431011693017181L;

    @Column(name = "TITLE", nullable = false)
    protected String title;

    @Column(name = "LINK", length = 500)
    protected String link;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;











    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }


    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }


    public ExtCompany getCompany() {
        return company;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }



}