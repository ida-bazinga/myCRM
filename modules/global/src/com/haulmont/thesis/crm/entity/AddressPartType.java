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
 * @author p.chizhikov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ADDRESS_PART_TYPE")
@Entity(name = "crm$AddressPartType")
public class AddressPartType extends BaseLookup {
    private static final long serialVersionUID = -4106347264054129833L;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;

    @Column(name = "SHORT_NAME_RU", length = 20)
    protected String shortName_ru;

    @Column(name = "SHORT_NAME_EN", length = 20)
    protected String shortName_en;

    @Column(name = "LAYER")
    protected Integer layer;

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }

    public void setShortName_ru(String shortName_ru) {
        this.shortName_ru = shortName_ru;
    }

    public String getShortName_ru() {
        return shortName_ru;
    }

    public void setShortName_en(String shortName_en) {
        this.shortName_en = shortName_en;
    }

    public String getShortName_en() {
        return shortName_en;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public Integer getLayer() {
        return layer;
    }


}