/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;

import com.haulmont.thesis.core.entity.Organization;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author p.chizhikov
 */
@Deprecated
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_VAT_INVOICE_DOC")
@Entity(name = "crm$VatInvoiceDoc")
public class VatInvoiceDoc extends BaseUuidEntity implements Versioned, SoftDelete, Updatable {
    private static final long serialVersionUID = -8409048802934539514L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Temporal(TemporalType.DATE)
    @Column(name = "OUTBOUND_DATE")
    protected Date outboundDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @Column(name = "TOTAL_SUM", precision = 15, scale = 2)
    protected BigDecimal totalSum;

    @Column(name = "SUM_NDS", precision = 15, scale = 2)
    protected BigDecimal sumNds;

    @Column(name = "COMMENT_RU", length = 1024)
    protected String comment_ru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GENERAL_DIRECTOR_ID")
    protected ExtEmployee generalDirector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHIF_ACCOUNT_ID")
    protected ExtEmployee chifAccount;

    @Column(name = "ID1_C", length = 50)
    protected String id1c;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACT_DOC_ID")
    protected ActDoc actDoc;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "IS_CARRIED")
    protected Boolean isCarried;

    @Column(name = "PRINT_SINGLE_LINE")
    protected Boolean printSingleLine;

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

    public void setPrintSingleLine(Boolean printSingleLine) {
        this.printSingleLine = printSingleLine;
    }

    public Boolean getPrintSingleLine() {
        return printSingleLine;
    }


    public void setIsCarried(Boolean isCarried) {
        this.isCarried = isCarried;
    }

    public Boolean getIsCarried() {
        return isCarried;
    }


    public ExtEmployee getGeneralDirector() {
        return generalDirector;
    }

    public void setGeneralDirector(ExtEmployee generalDirector) {
        this.generalDirector = generalDirector;
    }


    public ExtEmployee getChifAccount() {
        return chifAccount;
    }

    public void setChifAccount(ExtEmployee chifAccount) {
        this.chifAccount = chifAccount;
    }


    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
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


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public void setSumNds(BigDecimal sumNds) {
        this.sumNds = sumNds;
    }

    public BigDecimal getSumNds() {
        return sumNds;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }

    public void setActDoc(ActDoc actDoc) {
        this.actDoc = actDoc;
    }

    public ActDoc getActDoc() {
        return actDoc;
    }


}