/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Column;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.thesis.core.entity.Company;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_DUPLICATE_COMPANY_DETAIL")
@Entity(name = "crm$DuplicateCompanyDetail")
public class DuplicateCompanyDetail extends BaseUuidEntity {
    private static final long serialVersionUID = 7105491839292568539L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DUPLICATE_COMPANY_ID")
    protected DuplicateCompany duplicateCompany;

    public void setDuplicateCompany(DuplicateCompany duplicateCompany) {
        this.duplicateCompany = duplicateCompany;
    }

    public DuplicateCompany getDuplicateCompany() {
        return duplicateCompany;
    }


    public void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }





}