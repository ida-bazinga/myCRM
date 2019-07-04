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
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_WORKPLACE")
@Entity(name = "crm$Workplace")
public class Workplace extends BaseUuidEntity {
    private static final long serialVersionUID = 7652836187377488837L;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @Column(name = "IS_PRIMARY")
    protected Boolean isPrimary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected ExtEmployee employee;

    @Column(name = "SOCKET_NUMBER", length = 50)
    protected String socketNumber;

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

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setEmployee(ExtEmployee employee) {
        this.employee = employee;
    }

    public ExtEmployee getEmployee() {
        return employee;
    }

    public void setSocketNumber(String socketNumber) {
        this.socketNumber = socketNumber;
    }

    public String getSocketNumber() {
        return socketNumber;
    }


}