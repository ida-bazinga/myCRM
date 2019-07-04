/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.Column;

/**
 * @author k.khoroshilov
 */
@NamePattern("[%s] %s|code,fullName_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CURRENCY")
@Entity(name = "crm$Currency")
public class Currency extends BaseUnit {
    private static final long serialVersionUID = -6413766659968289420L;

    @Column(name = "FULL_NAME_ONE", length = 100)
    protected String fullName_one;

    @Column(name = "FULL_NAME_TWO", length = 100)
    protected String fullName_two;

    @Column(name = "FULL_NAME_FIVE", length = 100)
    protected String fullName_five;

    @Column(name = "SUB_CURRENCY_NAME_RU", length = 100)
    protected String subCurrencyName_ru;

    @Column(name = "SUB_CURRENCY_NAME_EN", length = 100)
    protected String subCurrencyName_en;

    @Column(name = "SUB_CURRENCY_FULL_NAME_ONE", length = 100)
    protected String subCurrencyFullName_one;

    @Column(name = "SUB_CURRENCY_FULL_NAME_TWO", length = 100)
    protected String subCurrencyFullName_two;

    @Column(name = "SUB_CURRENCY_FULL_NAME_FIVE")
    protected String subCurrencyFullName_five;

    @Column(name = "COURSE_BY_RUB")
    protected Double courseByRUB;

    public void setFullName_one(String fullName_one) {
        this.fullName_one = fullName_one;
    }

    public String getFullName_one() {
        return fullName_one;
    }

    public void setFullName_two(String fullName_two) {
        this.fullName_two = fullName_two;
    }

    public String getFullName_two() {
        return fullName_two;
    }

    public void setFullName_five(String fullName_five) {
        this.fullName_five = fullName_five;
    }

    public String getFullName_five() {
        return fullName_five;
    }

    public void setSubCurrencyFullName_one(String subCurrencyFullName_one) {
        this.subCurrencyFullName_one = subCurrencyFullName_one;
    }

    public String getSubCurrencyFullName_one() {
        return subCurrencyFullName_one;
    }

    public void setSubCurrencyFullName_two(String subCurrencyFullName_two) {
        this.subCurrencyFullName_two = subCurrencyFullName_two;
    }

    public String getSubCurrencyFullName_two() {
        return subCurrencyFullName_two;
    }

    public void setSubCurrencyFullName_five(String subCurrencyFullName_five) {
        this.subCurrencyFullName_five = subCurrencyFullName_five;
    }

    public String getSubCurrencyFullName_five() {
        return subCurrencyFullName_five;
    }

    public void setCourseByRUB(Double courseByRUB) {
        this.courseByRUB = courseByRUB;
    }

    public Double getCourseByRUB() {
        return courseByRUB;
    }


    public void setSubCurrencyName_ru(String subCurrencyName_ru) {
        this.subCurrencyName_ru = subCurrencyName_ru;
    }

    public String getSubCurrencyName_ru() {
        return subCurrencyName_ru;
    }


    public void setSubCurrencyName_en(String subCurrencyName_en) {
        this.subCurrencyName_en = subCurrencyName_en;
    }

    public String getSubCurrencyName_en() {
        return subCurrencyName_en;
    }


}