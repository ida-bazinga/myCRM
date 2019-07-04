/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import java.math.BigDecimal;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_TAX")
@Entity(name = "crm$Tax")
public class Tax extends BaseLookup {
    private static final long serialVersionUID = 6594870313649832656L;

    @Column(name = "RATE", nullable = false, precision = 4, scale = 2)
    protected BigDecimal rate;

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

}