/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import javax.persistence.Column;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|fullName_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_REGION_DISTRICT")
@Entity(name = "crm$RegionDistrict")
@Listeners("crm_RegionDistrictListener")
public class RegionDistrict extends BaseLookup {
    private static final long serialVersionUID = -2644820371478099316L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGION_ID")
    protected Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_PART_TYPE_ID")
    protected AddressPartType addressPartType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected Country country;

    @Column(name = "FULL_NAME_RU", length = 1000)
    protected String fullName_ru;

    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
    }


    public void setRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setAddressPartType(AddressPartType addressPartType) {
        this.addressPartType = addressPartType;
    }

    public AddressPartType getAddressPartType() {
        return addressPartType;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }


}