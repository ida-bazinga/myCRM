/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_LINE_OF_BUSINESS_COMPANIES_FLAGS")
@Entity(name = "crm$LineOfBusinessCompaniesFlags")
public class LineOfBusinessCompaniesFlags extends BaseUuidEntity {
    private static final long serialVersionUID = 8974583155372085460L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LINE_OF_BUSINESS_ID")
    protected LineOfBusiness lineOfBusiness;

    @Column(name = "WHOLESALE")
    protected Boolean wholesale;

    @Column(name = "RETAIL")
    protected Boolean retail;

    @Column(name = "INTERNET_STORE")
    protected Boolean internetStore;

    @Column(name = "MANUFACTURER")
    protected Boolean manufacturer;

    @Column(name = "DEALER")
    protected Boolean dealer;

    @Column(name = "DISTRIBUTOR")
    protected Boolean distributor;

    @Column(name = "CHAIN_STORE")
    protected Boolean chainStore;

    @Column(name = "SERVICES")
    protected Boolean services;

    public Boolean getServices() {
        return services;
    }

    public void setServices(Boolean services) {
        this.services = services;
    }


    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setLineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public LineOfBusiness getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setWholesale(Boolean wholesale) {
        this.wholesale = wholesale;
    }

    public Boolean getWholesale() {
        return wholesale;
    }

    public void setRetail(Boolean retail) {
        this.retail = retail;
    }

    public Boolean getRetail() {
        return retail;
    }

    public void setInternetStore(Boolean internetStore) {
        this.internetStore = internetStore;
    }

    public Boolean getInternetStore() {
        return internetStore;
    }

    public void setManufacturer(Boolean manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getManufacturer() {
        return manufacturer;
    }

    public void setDealer(Boolean dealer) {
        this.dealer = dealer;
    }

    public Boolean getDealer() {
        return dealer;
    }

    public void setDistributor(Boolean distributor) {
        this.distributor = distributor;
    }

    public Boolean getDistributor() {
        return distributor;
    }

    public void setChainStore(Boolean chainStore) {
        this.chainStore = chainStore;
    }

    public Boolean getChainStore() {
        return chainStore;
    }


}