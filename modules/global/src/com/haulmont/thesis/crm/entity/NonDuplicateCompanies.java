/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import java.util.UUID;
import javax.persistence.Column;
import org.apache.openjpa.persistence.Persistent;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_NON_DUPLICATE_COMPANIES")
@Entity(name = "crm$NonDuplicateCompanies")
public class NonDuplicateCompanies extends BaseUuidEntity {
    private static final long serialVersionUID = -115288184519847527L;

    @Persistent
    @Column(name = "FIRST_COMPANY")
    protected UUID firstCompany;

    @Persistent
    @Column(name = "SECOND_COMPANY")
    protected UUID secondCompany;

    public void setFirstCompany(UUID firstCompany) {
        this.firstCompany = firstCompany;
    }

    public UUID getFirstCompany() {
        return firstCompany;
    }

    public void setSecondCompany(UUID secondCompany) {
        this.secondCompany = secondCompany;
    }

    public UUID getSecondCompany() {
        return secondCompany;
    }


}