/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import javax.persistence.Column;

/**
 * @author a.donskoy
 */
@NamePattern("%s|bugetItem")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_INV_DOC_BUGET_DETAIL")
@Entity(name = "crm$InvDocBugetDetail")
public class InvDocBugetDetail extends BaseUuidEntity {
    private static final long serialVersionUID = -4693043816008587892L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUGET_ITEM_ID")
    protected BugetItem bugetItem;

    @Column(name = "FULL_SUM")
    protected BigDecimal fullSum;


    @Column(name = "FACT_SUM")
    protected BigDecimal factSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INV_DOC_ID")
    protected InvDoc invDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    protected ExtDepartment department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    public void setDepartment(ExtDepartment department) {
        this.department = department;
    }

    public ExtDepartment getDepartment() {
        return department;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }


    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }


    public void setFactSum(BigDecimal factSum) {
        this.factSum = factSum;
    }

    public BigDecimal getFactSum() {
        return factSum;
    }


    public void setInvDoc(InvDoc invDoc) {
        this.invDoc = invDoc;
    }

    public InvDoc getInvDoc() {
        return invDoc;
    }


    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }



    public void setBugetItem(BugetItem bugetItem) {
        this.bugetItem = bugetItem;
    }

    public BugetItem getBugetItem() {
        return bugetItem;
    }


}