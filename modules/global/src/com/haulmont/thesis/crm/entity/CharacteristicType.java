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
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CHARACTERISTIC_TYPE")
@Entity(name = "crm$CharacteristicType")
public class CharacteristicType extends BaseLookup {
    private static final long serialVersionUID = 754118885216774035L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECOND_UNIT_ID")
    protected Unit secondUnit;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    public void setSecondUnit(Unit secondUnit) {
        this.secondUnit = secondUnit;
    }

    public Unit getSecondUnit() {
        return secondUnit;
    }


    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


}