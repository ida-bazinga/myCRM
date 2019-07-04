/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.apache.openjpa.persistence.Persistent;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;

import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Transient;

/**
 * @author k.khoroshilov
 */
@NamePattern("[%s] %s (%s %s)|commodityCode,commodityName,quantity,measureName")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CASH_DOCUMENT_POSITION")
@Entity(name = "crm$CashDocumentPosition")
public class CashDocumentPosition extends BaseUuidEntity implements SoftDelete {
    private static final long serialVersionUID = 2889178737566483178L;

    @Column(name = "POS_ID", length = 50)
    protected String posId;

    @Column(name = "BARCODE", length = 100)
    protected String barcode;

    @Column(name = "COMMODITY_CODE", length = 50)
    protected String commodityCode;

    @Persistent
    @Column(name = "COMMODITY_UUID")
    protected UUID commodityUuid;

    @Column(name = "COMMODITY_NAME")
    protected String commodityName;

    @Column(name = "QUANTITY")
    protected Double quantity;

    @Column(name = "MEASURE_NAME", length = 50)
    protected String measureName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_ID")
    protected Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_ID")
    protected Cost cost;

    @Column(name = "COST_PRICE")
    protected Double costPrice;

    @Column(name = "PRICE")
    protected Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @Column(name = "RESULT_PRICE")
    protected Double resultPrice;

    @Column(name = "RESULT_SUM")
    protected Double resultSum;

    @Column(name = "POS_SUM")
    protected Double posSum;

    @Column(name = "RESULT_TAX_SUM")
    protected Double resultTaxSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @Column(name = "TAX_PERCENT")
    protected Double taxPercent;

    @Column(name = "TAX_SUM")
    protected Double taxSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CASH_DOCUMENT_ID")
    protected CashDocument cashDocument;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Double getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(Double taxPercent) {
        this.taxPercent = taxPercent;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCashDocument(CashDocument cashDocument) {
        this.cashDocument = cashDocument;
    }

    public CashDocument getCashDocument() {
        return cashDocument;
    }

    public Boolean isDeleted() {
        return deleteTs != null;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    public Date getDeleteTs() {
        return deleteTs;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getPosId() {
        return posId;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityUuid(UUID commodityUuid) {
        this.commodityUuid = commodityUuid;
    }

    public UUID getCommodityUuid() {
        return commodityUuid;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityName() {
        return commodityName;
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

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setResultPrice(Double resultPrice) {
        this.resultPrice = resultPrice;
    }

    public Double getResultPrice() {
        return resultPrice;
    }

    public void setResultSum(Double resultSum) {
        this.resultSum = resultSum;
    }

    public Double getResultSum() {
        return resultSum;
    }

    public void setPosSum(Double posSum) {
        this.posSum = posSum;
    }

    public Double getPosSum() {
        return posSum;
    }

    public void setResultTaxSum(Double resultTaxSum) {
        this.resultTaxSum = resultTaxSum;
    }

    public Double getResultTaxSum() {
        return resultTaxSum;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTaxSum(Double taxSum) {
        this.taxSum = taxSum;
    }

    public Double getTaxSum() {
        return taxSum;
    }

}