/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
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
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|fullName_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_REGION")
@Entity(name = "crm$Region")
@Listeners("crm_RegionListener")
public class Region extends BaseLookup {
    private static final long serialVersionUID = 2213564703192202142L;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;
//comm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_PART_TYPE_ID")
    protected AddressPartType addressPartType;

    @Column(name = "FULL_NAME_RU", length = 1000)
    protected String fullName_ru;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FEDERAL_DISTRICT_ID")
    protected FederalDistrict federalDistrict;

    public void setFederalDistrict(FederalDistrict federalDistrict) {
        this.federalDistrict = federalDistrict;
    }

    public FederalDistrict getFederalDistrict() {
        return federalDistrict;
    }


    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
    }


    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setAddressPartType(AddressPartType addressPartType) {
        this.addressPartType = addressPartType;
    }

    public AddressPartType getAddressPartType() {
        return addressPartType;
    }


}