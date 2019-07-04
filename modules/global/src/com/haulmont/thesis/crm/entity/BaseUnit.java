/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@MappedSuperclass
public class BaseUnit extends BaseLookup {
    private static final long serialVersionUID = -5177557039715041818L;

    @Column(name = "FULL_NAME_RU", length = 100)
    protected String fullName_ru;

    @Column(name = "NAME_EN", length = 25)
    protected String name_en;

    @Column(name = "FULL_NAME_EN", length = 100)
    protected String fullName_en;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
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


    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


}