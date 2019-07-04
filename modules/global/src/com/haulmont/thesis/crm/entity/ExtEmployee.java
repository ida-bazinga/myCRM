/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name")
@Extends(Employee.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Y")
@Entity(name = "crm$Employee")
public class ExtEmployee extends Employee {
    private static final long serialVersionUID = 3023398926141214804L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DISMISSAL_DATE")
    protected Date dismissalDate;

    @Column(name = "LOCATION", length = 100)
    protected String location;

    @Column(name = "PHONE_NUM_EXTNESION", length = 5)
    protected String phoneNumExtnesion;

    @Column(name = "GEN_FIRST_NAME")
    protected String genFirstName;

    @Column(name = "DAT_FIRST_NAME")
    protected String datFirstName;

    @Column(name = "GEN_MIDDLE_NAME")
    protected String genMiddleName;

    @Column(name = "DAT_MIDDLE_NAME")
    protected String datMiddleName;

    @Column(name = "GEN_LAST_NAME")
    protected String genLastName;

    @Column(name = "DAT_LAST_NAME")
    protected String datLastName;

    @Column(name = "FIRST_NAME_LAT")
    protected String firstNameLat;

    @Column(name = "LAST_NAME_LAT")
    protected String lastNameLat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;
    @Column(name = "ACCESS_CARD", length = 50)
    protected String accessCard;

    public void setAccessCard(String accessCard) {
        this.accessCard = accessCard;
    }

    public String getAccessCard() {
        return accessCard;
    }


    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setFirstNameLat(String firstNameLat) {
        this.firstNameLat = firstNameLat;
    }

    public String getFirstNameLat() {
        return firstNameLat;
    }

    public void setLastNameLat(String lastNameLat) {
        this.lastNameLat = lastNameLat;
    }

    public String getLastNameLat() {
        return lastNameLat;
    }

    public void setDismissalDate(Date dismissalDate) {
        this.dismissalDate = dismissalDate;
    }

    public Date getDismissalDate() {
        return dismissalDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setPhoneNumExtnesion(String phoneNumExtnesion) {
        this.phoneNumExtnesion = phoneNumExtnesion;
    }

    public String getPhoneNumExtnesion() {
        return phoneNumExtnesion;
    }

    public void setGenFirstName(String genFirstName) {
        this.genFirstName = genFirstName;
    }

    public String getGenFirstName() {
        return genFirstName;
    }

    public void setDatFirstName(String datFirstName) {
        this.datFirstName = datFirstName;
    }

    public String getDatFirstName() {
        return datFirstName;
    }

    public void setGenMiddleName(String genMiddleName) {
        this.genMiddleName = genMiddleName;
    }

    public String getGenMiddleName() {
        return genMiddleName;
    }

    public void setDatMiddleName(String datMiddleName) {
        this.datMiddleName = datMiddleName;
    }

    public String getDatMiddleName() {
        return datMiddleName;
    }

    public void setGenLastName(String genLastName) {
        this.genLastName = genLastName;
    }

    public String getGenLastName() {
        return genLastName;
    }

    public void setDatLastName(String datLastName) {
        this.datLastName = datLastName;
    }

    public String getDatLastName() {
        return datLastName;
    }
}