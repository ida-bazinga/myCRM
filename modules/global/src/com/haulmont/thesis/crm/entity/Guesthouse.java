/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Table(name = "CRM_GUESTHOUSE")
@Entity(name = "crm$Guesthouse")
public class Guesthouse extends BaseLookup {
    private static final long serialVersionUID = 2200102335349658732L;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @Temporal(TemporalType.TIME)
    @Column(name = "CHECK_OUT_HOUR")
    protected Date checkOutHour;

    @Temporal(TemporalType.TIME)
    @Column(name = "LATE_CHECK_OUT_TIME")
    protected Date lateCheckOutTime;

    @Column(name = "LATE_CHECK_OUT_MARKUP")
    protected BigDecimal lateCheckOutMarkup;

    @Column(name = "AFTER_CHECK_OUT_MARKUP")
    protected BigDecimal afterCheckOutMarkup;

    public void setCheckOutHour(Date checkOutHour) {
        this.checkOutHour = checkOutHour;
    }

    public Date getCheckOutHour() {
        return checkOutHour;
    }

    public void setLateCheckOutTime(Date lateCheckOutTime) {
        this.lateCheckOutTime = lateCheckOutTime;
    }

    public Date getLateCheckOutTime() {
        return lateCheckOutTime;
    }

    public void setLateCheckOutMarkup(BigDecimal lateCheckOutMarkup) {
        this.lateCheckOutMarkup = lateCheckOutMarkup;
    }

    public BigDecimal getLateCheckOutMarkup() {
        return lateCheckOutMarkup;
    }

    public void setAfterCheckOutMarkup(BigDecimal afterCheckOutMarkup) {
        this.afterCheckOutMarkup = afterCheckOutMarkup;
    }

    public BigDecimal getAfterCheckOutMarkup() {
        return afterCheckOutMarkup;
    }


    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }


    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }


}