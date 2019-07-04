/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s|name_ru")
@Table(name = "CRM_LEARNED_ABOUT_EXHIBITION")
@Entity(name = "crm$LearnedAboutExhibition")
public class LearnedAboutExhibition extends BaseUuidEntity {
    private static final long serialVersionUID = -3607399274508138212L;

    @Column(name = "NAME_RU")
    protected String name_ru;


    @Column(name = "NAME_EN")
    protected String name_en;

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }


    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
    }


}