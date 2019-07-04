/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.google.gson.annotations.SerializedName;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.core.entity.Project;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name")
@MetaClass(name = "crm$CashMachineProduct")
public class CashMachineProduct extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 6205681449968845824L;

    @MetaProperty
    protected String productId;

    @MetaProperty
    protected String code;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected Double price;

    @MetaProperty
    protected Double quantity;

    @MetaProperty
    protected Double costPrice;

    @MetaProperty
    protected String measureName;

    @MetaProperty
    protected String taxCode;

    @MetaProperty
    protected Boolean allowToSell;

    @MetaProperty
    protected String description;

    @MetaProperty
    protected String articleNumber;

    @MetaProperty
    protected Boolean isGroup;

    @MetaProperty
    protected String type;

    @MetaProperty
    protected Double alcoholByVolume;

    @MetaProperty
    protected Integer alcoholProductKindCode;

    @MetaProperty
    protected Double tareVolume;

    @MetaProperty
    protected String parentUuid;

    @MetaProperty
    protected UUID storeId;

    @MetaProperty
    protected String barCodes;

    @MetaProperty
    protected String alcoCodes;

    @MetaProperty
    protected ExtProject project;

    @MetaProperty
    protected ExhibitSpace exhibitSpace;

    @MetaProperty
    protected Unit unit;

    @MetaProperty
    protected Tax tax;

    public void setAlcoCodes(String alcoCodes) {
        this.alcoCodes = alcoCodes;
    }

    public String getAlcoCodes() {
        return alcoCodes;
    }

    @PostConstruct
    protected void init() {
        setType("NORMAL");
        setAlcoholByVolume(0d);
        setAlcoholProductKindCode(0);
        setTareVolume(0d);
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public void setExhibitSpace(ExhibitSpace exhibitSpace) {
        this.exhibitSpace = exhibitSpace;
    }

    public ExhibitSpace getExhibitSpace() {
        return exhibitSpace;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getBarCodes() {
        return barCodes;
    }

    public void setBarCodes(String barCodes) {
        this.barCodes = barCodes;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setAlcoholByVolume(Double alcoholByVolume) {
        this.alcoholByVolume = alcoholByVolume;
    }

    public Double getAlcoholByVolume() {
        return alcoholByVolume;
    }

    public void setAlcoholProductKindCode(Integer alcoholProductKindCode) {
        this.alcoholProductKindCode = alcoholProductKindCode;
    }

    public Integer getAlcoholProductKindCode() {
        return alcoholProductKindCode;
    }

    public void setTareVolume(Double tareVolume) {
        this.tareVolume = tareVolume;
    }

    public Double getTareVolume() {
        return tareVolume;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setAllowToSell(Boolean allowToSell) {
        this.allowToSell = allowToSell;
    }

    public Boolean getAllowToSell() {
        return allowToSell;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getArticleNumber() {
        return articleNumber;
    }
    
}