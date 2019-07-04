/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Table(name = "CRM_TOUR_TYPE")
@Entity(name = "crm$TourType")
public class TourType extends BaseLookup {
    private static final long serialVersionUID = 7133920082146715137L;
    @Column(name = "NAME_EN")
    protected String name_en;

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }



}