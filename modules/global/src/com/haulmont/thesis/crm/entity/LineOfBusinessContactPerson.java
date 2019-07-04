/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;

/**
 * @author d.ivanov
 */
@NamePattern("%s |lineOfBusiness")
@Table(name = "CRM_LINE_OF_BUSINESS_CONTACT_PERSON")
@Entity(name = "crm$LineOfBusinessContactPerson")
public class LineOfBusinessContactPerson extends BaseUuidEntity {
    private static final long serialVersionUID = -2507708279529078290L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONTACT_PERSON_ID")
    protected ExtContactPerson contactPerson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LINE_OF_BUSINESS_ID")
    protected LineOfBusiness lineOfBusiness;

    @Column(name = "IS_INTERESTINGLY")
    protected Boolean is_interestingly;




    @Column(name = "SERVICES")
    protected Boolean services;

    @Column(name = "DISTRIBUTOR")
    protected Boolean distributor;

    @Column(name = "RETAIL")
    protected Boolean retail;

    @Column(name = "MANUFACTURER")
    protected Boolean manufacturer;

    @Column(name = "CHAIN_STORE")
    protected Boolean chainStore;

    @Column(name = "WHOLESALE")
    protected Boolean wholesale;

    @Column(name = "INTERNET_STORE")
    protected Boolean internetStore;

    @Column(name = "DEALER")
    protected Boolean dealer;

    public void setServices(Boolean services) {
        this.services = services;
    }

    public Boolean getServices() {
        return services;
    }

    public void setDistributor(Boolean distributor) {
        this.distributor = distributor;
    }

    public Boolean getDistributor() {
        return distributor;
    }

    public void setRetail(Boolean retail) {
        this.retail = retail;
    }

    public Boolean getRetail() {
        return retail;
    }

    public void setManufacturer(Boolean manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getManufacturer() {
        return manufacturer;
    }

    public void setChainStore(Boolean chainStore) {
        this.chainStore = chainStore;
    }

    public Boolean getChainStore() {
        return chainStore;
    }

    public void setWholesale(Boolean wholesale) {
        this.wholesale = wholesale;
    }

    public Boolean getWholesale() {
        return wholesale;
    }

    public void setInternetStore(Boolean internetStore) {
        this.internetStore = internetStore;
    }

    public Boolean getInternetStore() {
        return internetStore;
    }

    public void setDealer(Boolean dealer) {
        this.dealer = dealer;
    }

    public Boolean getDealer() {
        return dealer;
    }


    public void setContactPerson(ExtContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public ExtContactPerson getContactPerson() {
        return contactPerson;
    }


    public void setLineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public LineOfBusiness getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setIs_interestingly(Boolean is_interestingly) {
        this.is_interestingly = is_interestingly;
    }

    public Boolean getIs_interestingly() {
        return is_interestingly;
    }





}