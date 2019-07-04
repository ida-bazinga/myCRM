
/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */

package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.thesis.core.entity.Company;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_EMAIL_ADDRESS")
@Entity(name = "crm$EmailAddress")
public class EmailAddress extends BaseUuidEntity {
    private static final long serialVersionUID = 2912331705924762494L;

    @Column(name = "REQUEST_IP", length = 20)
    protected String requestIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_TIME")
    protected Date requestTime;

    @Column(name = "DOUBLE_OPT_IN")
    protected Integer doubleOptIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONFIRM_TIME")
    protected Date confirmTime;

    @Column(name = "CONFIRM_IP", length = 20)
    protected String confirmIp;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }


    public ExtCompany getCompany() {
        return company;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }


    public void setConfirmIp(String confirmIp) {
        this.confirmIp = confirmIp;
    }

    public String getConfirmIp() {
        return confirmIp;
    }


    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setDoubleOptIn(Integer doubleOptIn) {
        this.doubleOptIn = doubleOptIn;
    }

    public Integer getDoubleOptIn() {
        return doubleOptIn;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }


}