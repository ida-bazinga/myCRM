/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Lob;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@MappedSuperclass
public abstract class BaseLookup extends BaseUuidEntity {
    private static final long serialVersionUID = -5709688424441323345L;

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    protected String code;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Lob
    @Column(name = "COMMENT_RU")
    protected String comment_ru;

    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}