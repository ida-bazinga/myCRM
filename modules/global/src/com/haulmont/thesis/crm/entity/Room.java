/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author a.donskoy
 */
@Listeners("crm_ResourceEntityListener")
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName ="ID")
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("R")
@Table(name = "CRM_ROOM")
@Entity(name = "crm$Room")
public class Room extends Resource {
    private static final long serialVersionUID = 5960574988909924670L;

    @Column(name = "CADASTRAL_NUMBER", length = 16)
    protected String cadastralNumber;

    @Column(name = "TOTAL_GROSS_AREA")
    protected BigDecimal totalGrossArea;

    @OneToMany(mappedBy = "room")
    protected List<Workplace> workplaces;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "LENGTH", nullable = false)
    protected Integer length;

    @Column(name = "WIDTH", nullable = false)
    protected Integer width;

    @Column(name = "IS_USE_CONFIGURATOR", nullable = false)
    protected Boolean isUseConfigurator = false;

    @OneToMany(mappedBy = "room")
    protected List<RoomException> conflictRooms;

    public void setConflictRooms(List<RoomException> conflictRooms) {
        this.conflictRooms = conflictRooms;
    }

    public List<RoomException> getConflictRooms() {
        return conflictRooms;
    }

    public void setIsUseConfigurator(Boolean isUseConfigurator) {
        this.isUseConfigurator = isUseConfigurator;
    }

    public Boolean getIsUseConfigurator() {
        return isUseConfigurator;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWorkplaces(List<Workplace> workplaces) {
        this.workplaces = workplaces;
    }

    public List<Workplace> getWorkplaces() {
        return workplaces;
    }

    public void setCadastralNumber(String cadastralNumber) {
        this.cadastralNumber = cadastralNumber;
    }

    public String getCadastralNumber() {
        return cadastralNumber;
    }

    public void setTotalGrossArea(BigDecimal totalGrossArea) {
        this.totalGrossArea = totalGrossArea;
    }

    public BigDecimal getTotalGrossArea() {
        return totalGrossArea;
    }

}