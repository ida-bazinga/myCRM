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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;

import javax.persistence.Transient;
import javax.persistence.Lob;

/**
 * @author k.khoroshilov
 */
@NamePattern("[%s] %s|code,name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CATALOG")
@Entity(name = "crm$Catalog")
@TrackEditScreenHistory
public class Catalog extends BaseLookup {
    private static final long serialVersionUID = 9137869531986376546L;

    @Column(name = "FULL_NAME_RU", length = 1000)
    protected String fullName_ru;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;

    @Column(name = "FULL_NAME_EN", length = 1000)
    protected String fullName_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATALOG_ID")
    protected Catalog parentCatalog;

    @Lob
    @Column(name = "COMMENT_EN")
    protected String comment_en;

    @Lob
    @Column(name = "COMMENT2_RU")
    protected String comment2_ru;

    @Lob
    @Column(name = "COMMENT2_EN")
    protected String comment2_en;

    @Column(name = "NOT_IN_USE")
    protected Boolean notInUse;

    public void setNotInUse(Boolean notInUse) {
        this.notInUse = notInUse;
    }

    public Boolean getNotInUse() {
        return notInUse;
    }


    public void setComment2_ru(String comment2_ru) {
        this.comment2_ru = comment2_ru;
    }

    public String getComment2_ru() {
        return comment2_ru;
    }

    public void setComment2_en(String comment2_en) {
        this.comment2_en = comment2_en;
    }

    public String getComment2_en() {
        return comment2_en;
    }


    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }


    public void setFullName_en(String fullName_en) {
        this.fullName_en = fullName_en;
    }


    
    public String getFullName_ru() {
        return String.format("[%s] %s", code, name_ru);
    }

    
    public String getFullName_en() {
        return String.format("[%s] %s", code, name_en);
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }


    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_en() {
        return comment_en;
    }


    public void setParentCatalog(Catalog parentCatalog) {
        this.parentCatalog = parentCatalog;
    }

    public Catalog getParentCatalog() {
        return parentCatalog;
    }



}