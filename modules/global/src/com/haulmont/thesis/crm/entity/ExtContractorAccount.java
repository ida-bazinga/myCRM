/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.Column;
import com.haulmont.thesis.core.entity.ContractorAccount;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s (%s)|no,currency")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("E")
@Extends(ContractorAccount.class)
@Entity(name = "crm$ExtContractorAccount")
public class ExtContractorAccount extends ContractorAccount {
    private static final long serialVersionUID = 8515340811420834395L;

    @Column(name = "BIK")
    protected String bik;

    @Column(name = "SWIFT")
    protected String swift;

    @Column(name = "ADRESS", length = 4000)
    protected String adress;

    @Column(name = "REGION", length = 200)
    protected String region;

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }


    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getBik() {
        return bik;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getSwift() {
        return swift;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getAdress() {
        return adress;
    }


}