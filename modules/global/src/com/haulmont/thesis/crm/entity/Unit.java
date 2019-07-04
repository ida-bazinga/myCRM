/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_UNIT")
@Entity(name = "crm$Unit")
public class Unit extends BaseUnit {
    private static final long serialVersionUID = -232207690296074597L;

    @Column(name = "UNIT_TYPE", nullable = false)
    protected Integer unitType;

    public void setUnitType(UnitsTypeEnum unitType) {
        this.unitType = unitType == null ? null : unitType.getId();
    }

    public UnitsTypeEnum getUnitType() {
        return unitType == null ? null : UnitsTypeEnum.fromId(unitType);
    }


}