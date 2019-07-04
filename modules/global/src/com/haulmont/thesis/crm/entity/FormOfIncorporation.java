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
 * @author p.chizhikov
 */
@NamePattern("[%s] %s|shortName,name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_FORM_OF_INCORPORATION")
@Entity(name = "crm$FormOfIncorporation")
public class FormOfIncorporation extends BaseLookup {
    private static final long serialVersionUID = -8190349984570623487L;

    @Column(name = "SHORT_NAME", length = 20)
    protected String shortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected Country country;

    @Column(name = "NOT_APPLY")
    protected Boolean notApply;

    public void setNotApply(Boolean notApply) {
        this.notApply = notApply;
    }

    public Boolean getNotApply() {
        return notApply;
    }


    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }


}