/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Date;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_HISTORY_COMPANY_CELLS")
@Entity(name = "crm$HistoryCompanyCells")
public class HistoryCompanyCells extends BaseUuidEntity {
    private static final long serialVersionUID = 3211412126901146680L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @Column(name = "COMPANY_CELLS", nullable = false)
    protected String companyCells;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Column(name = "ITEM", nullable = false, length = 1000)
    protected String item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID")
    protected Address address;

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }


    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setCompanyCells(CompanyCellsEnum companyCells) {
        this.companyCells = companyCells == null ? null : companyCells.getId();
    }

    public CompanyCellsEnum getCompanyCells() {
        return companyCells == null ? null : CompanyCellsEnum.fromId(companyCells);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }


}