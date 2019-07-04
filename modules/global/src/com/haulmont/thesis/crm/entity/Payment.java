/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.thesis.core.entity.ContractorAccount;
import com.haulmont.thesis.core.entity.OrganizationAccount;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Lob;

/**
 * @author d.ivanov
 */
@NamePattern("%s|code")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_PAYMENT")
@Entity(name = "crm$Payment")
public class Payment extends BaseUuidEntity {
    private static final long serialVersionUID = 6397085998476013512L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Temporal(TemporalType.DATE)
    @Column(name = "OUTBOUND_DATE")
    protected Date outboundDate;

    @Column(name = "OPERATION", length = 250)
    protected String operation;

    @Column(name = "BANK_NUMBER", length = 50)
    protected String bankNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "BANK_DATE")
    protected Date bankDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @Lob
    @Column(name = "BANK_ACCOUNT")
    protected String bankAccount;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @Column(name = "NOTE", length = 1000)
    protected String note;

    @Column(name = "FULL_SUM", precision = 15, scale = 2)
    protected BigDecimal fullSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @Column(name = "ORGANIZATION")
    protected String organization;

    @Column(name = "BANK_ORGANIZATION")
    protected String bankOrganization;

    @Column(name = "POSTED")
    protected Boolean posted;

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    public void setBankOrganization(String bankOrganization) {
        this.bankOrganization = bankOrganization;
    }

    public String getBankOrganization() {
        return bankOrganization;
    }


    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }



    public void setBankDate(Date bankDate) {
        this.bankDate = bankDate;
    }

    public Date getBankDate() {
        return bankDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public Boolean getPosted() {
        return posted;
    }


    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getBankNumber() {
        return bankNumber;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }

    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }


}