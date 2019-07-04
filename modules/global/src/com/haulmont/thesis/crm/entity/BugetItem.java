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
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author p.chizhikov
 */
@Listeners("crm_BudgetItem")
@NamePattern(" %s|fullName_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_BUGET_ITEM")
@Entity(name = "crm$BugetItem")
public class BugetItem extends BaseUuidEntity {
    private static final long serialVersionUID = -4928175532285605459L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CFC_ITEM_ID")
    protected CfcItem cfcItem;

    @Column(name = "BUGET_ITEM_TYPE")
    protected Integer bugetItemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_BUDGET_ITEM_ID")
    protected BugetItem parentBudgetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    protected ExtDepartment department;

    @Column(name = "FULL_NAME_RU", length = 1050)
    protected String fullName_ru;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "NAME_RU", nullable = false, length = 500)
    protected String name_ru;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

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


    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
    }


    public void setDepartment(ExtDepartment department) {
        this.department = department;
    }

    public ExtDepartment getDepartment() {
        return department;
    }


    public void setParentBudgetItem(BugetItem parentBudgetItem) {
        this.parentBudgetItem = parentBudgetItem;
    }

    public BugetItem getParentBudgetItem() {
        return parentBudgetItem;
    }


    public void setCfcItem(CfcItem cfcItem) {
        this.cfcItem = cfcItem;
    }

    public CfcItem getCfcItem() {
        return cfcItem;
    }

    public void setBugetItemType(BugetItemsTypeEnum bugetItemType) {
        this.bugetItemType = bugetItemType == null ? null : bugetItemType.getId();
    }

    public BugetItemsTypeEnum getBugetItemType() {
        return bugetItemType == null ? null : BugetItemsTypeEnum.fromId(bugetItemType);
    }


}