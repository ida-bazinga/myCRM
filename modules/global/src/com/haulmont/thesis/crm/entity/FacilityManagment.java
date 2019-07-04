/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.thesis.core.entity.Employee;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s|name")
@Table(name = "CRM_FACILITY_MANAGMENT")
@Entity(name = "crm$FacilityManagment")
public class FacilityManagment extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = 6294553057319136663L;

    @Column(name = "NAME", nullable = false, length = 500)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected Employee employee;

    @Column(name = "FACILITY_MANAGMENT_TYPE", nullable = false)
    protected String facilityManagmentType;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setFacilityManagmentType(FacilityManagmentTypeEnum facilityManagmentType) {
        this.facilityManagmentType = facilityManagmentType == null ? null : facilityManagmentType.getId();
    }

    public FacilityManagmentTypeEnum getFacilityManagmentType() {
        return facilityManagmentType == null ? null : FacilityManagmentTypeEnum.fromId(facilityManagmentType);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}