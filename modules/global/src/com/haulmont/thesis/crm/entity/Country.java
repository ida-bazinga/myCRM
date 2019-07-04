/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COUNTRY")
@Entity(name = "crm$Country")
public class Country extends BaseLookup {
    private static final long serialVersionUID = 1268926510341184047L;

    @Column(name = "NAME_EN", length = 250)
    protected String name_en;

    @Column(name = "FULL_NAME_EN", length = 500)
    protected String fullName_en;

    @Column(name = "FULL_NAME_RU", length = 500)
    protected String fullName_ru;

    @Column(name = "ALPHA_2", length = 2)
    protected String alpha_2;

    @Column(name = "ALPHA_3", length = 3)
    protected String alpha_3;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }

    public void setFullName_en(String fullName_en) {
        this.fullName_en = fullName_en;
    }

    public String getFullName_en() {
        return fullName_en;
    }

    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
    }

    public void setAlpha_2(String alpha_2) {
        this.alpha_2 = alpha_2;
    }

    public String getAlpha_2() {
        return alpha_2;
    }

    public void setAlpha_3(String alpha_3) {
        this.alpha_3 = alpha_3;
    }

    public String getAlpha_3() {
        return alpha_3;
    }


}