/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COMPANY_COM_TURNOVER_TYPE")
@Entity(name = "crm$CompanyComTurnoverType")
public class CompanyComTurnoverType extends BaseLookup {
    private static final long serialVersionUID = -4104783681786064599L;

    @JoinTable(name = "CRM_COMPANY_COM_TURNOVER_TYPE_EXT_COMPANY_LINK",
        joinColumns = @JoinColumn(name = "COMPANY_COM_TURNOVER_TYPE_ID"),
        inverseJoinColumns = @JoinColumn(name = "EXT_COMPANY_ID"))
    @ManyToMany
    protected List<ExtCompany> companies;

    public void setCompanies(List<ExtCompany> companies) {
        this.companies = companies;
    }

    public List<ExtCompany> getCompanies() {
        return companies;
    }


}