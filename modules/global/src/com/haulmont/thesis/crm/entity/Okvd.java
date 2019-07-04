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
import java.util.Set;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|fullName_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_OKVD")
@Entity(name = "crm$Okvd")
public class Okvd extends BaseLookup {
    private static final long serialVersionUID = 7346803179980294886L;

    @Column(name = "FULL_NAME_RU", length = 1000)
    protected String fullName_ru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_OKVD_ID")
    protected Okvd parentOkvd;


    @JoinTable(name = "CRM_COMPANY_OKVD_LINK",
        joinColumns = @JoinColumn(name = "OKVD_ID"),
        inverseJoinColumns = @JoinColumn(name = "COMPANY_ID"))
    @ManyToMany
    protected Set<ExtCompany> companies;

    public void setCompanies(Set<ExtCompany> companies) {
        this.companies = companies;
    }

    public Set<ExtCompany> getCompanies() {
        return companies;
    }


    public void setParentOkvd(Okvd parentOkvd) {
        this.parentOkvd = parentOkvd;
    }

    public Okvd getParentOkvd() {
        return parentOkvd;
    }


    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
    }


}