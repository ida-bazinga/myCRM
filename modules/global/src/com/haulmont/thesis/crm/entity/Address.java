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
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ADDRESS")
@Entity(name = "crm$Address")
@Listeners("crm_AddressListener")
public class Address extends BaseUuidEntity {
    private static final long serialVersionUID = 8766950162299714456L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGION_ID")
    protected Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGION_DISTRICT_ID")
    protected RegionDistrict regionDistrict;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID")
    protected City city;

    @Column(name = "ZIP", length = 50)
    protected String zip;

    @Column(name = "ADDRESS_RU", length = 1000)
    protected String address_ru;

    @Column(name = "NAME_RU", nullable = false, length = 4000)
    protected String name_ru;

    @Column(name = "OKATO", length = 20)
    protected String okato;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    @Column(name = "CODE", length = 50)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_OBJECT_ID")
    protected Address parentObject;

    @Column(name = "STREET")
    protected String street;

    @Column(name = "HOUSE", length = 10)
    protected String house;

    @Column(name = "BUILDING", length = 10)
    protected String building;

    @Column(name = "APARTMENT", length = 10)
    protected String apartment;

    @Column(name = "ADDRESS_FIRST_DOC", length = 1000)
    protected String addressFirstDoc;

    @Column(name = "GEO_LAT")
    protected Double geoLat;

    @Column(name = "GEO_LON")
    protected Double geoLon;

    public void setGeoLat(Double geoLat) {
        this.geoLat = geoLat;
    }

    public Double getGeoLat() {
        return geoLat;
    }

    public void setGeoLon(Double geoLon) {
        this.geoLon = geoLon;
    }

    public Double getGeoLon() {
        return geoLon;
    }


    public void setAddressFirstDoc(String addressFirstDoc) {
        this.addressFirstDoc = addressFirstDoc;
    }

    public String getAddressFirstDoc() {
        return addressFirstDoc;
    }


    public void setAddress_ru(String address_ru) {
        this.address_ru = address_ru;
    }

    public String getAddress_ru() {
        return address_ru;
    }


    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getHouse() {
        return house;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBuilding() {
        return building;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getApartment() {
        return apartment;
    }


    public void setParentObject(Address parentObject) {
        this.parentObject = parentObject;
    }

    public Address getParentObject() {
        return parentObject;
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

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegionDistrict(RegionDistrict regionDistrict) {
        this.regionDistrict = regionDistrict;
    }

    public RegionDistrict getRegionDistrict() {
        return regionDistrict;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

    public void setOkato(String okato) {
        this.okato = okato;
    }

    public String getOkato() {
        return okato;
    }


}