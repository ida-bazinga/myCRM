/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.Column;
import com.haulmont.thesis.core.entity.Department;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author a.donskoy
 */
@Extends(Department.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("X")
@Entity(name = "crm$Department")
public class ExtDepartment extends Department {
    private static final long serialVersionUID = -3894008798133143787L;

    @Column(name = "GEN_NAME")
    protected String genName;

    @Column(name = "DAT_NAME")
    protected String datName;

    @Column(name = "DEPARTMENT_TYPE")
    protected Integer departmentType;

    @Column(name = "NOT_WORKING", nullable = false)
    protected Boolean notWorking = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ECONOMIST_RESPONSIBLE_ID")
    protected ExtEmployee economistResponsible;

    public void setEconomistResponsible(ExtEmployee economistResponsible) {
        this.economistResponsible = economistResponsible;
    }

    public ExtEmployee getEconomistResponsible() {
        return economistResponsible;
    }


    public void setNotWorking(Boolean notWorking) {
        this.notWorking = notWorking;
    }

    public Boolean getNotWorking() {
        return notWorking;
    }


    public void setDepartmentType(CompanySubdivisionEnum departmentType) {
        this.departmentType = departmentType == null ? null : departmentType.getId();
    }

    public CompanySubdivisionEnum getDepartmentType() {
        return departmentType == null ? null : CompanySubdivisionEnum.fromId(departmentType);
    }


    public void setGenName(String genName) {
        this.genName = genName;
    }

    public String getGenName() {
        return genName;
    }

    public void setDatName(String datName) {
        this.datName = datName;
    }

    public String getDatName() {
        return datName;
    }


}