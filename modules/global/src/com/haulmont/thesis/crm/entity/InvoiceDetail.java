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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|product")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_INVOICE_DETAIL")
@Entity(name = "crm$InvoiceDetail")
public class InvoiceDetail extends BaseUuidEntity {
    private static final long serialVersionUID = 4235880737554410613L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;

    @Column(name = "AMOUNT", precision = 15, scale = 3)
    protected BigDecimal amount;

    @Column(name = "COST", precision = 15, scale = 2)
    protected BigDecimal cost;

    @Column(name = "SUM_WITHOUT_NDS", precision = 15, scale = 2)
    protected BigDecimal sumWithoutNds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @Column(name = "TAX_SUM", precision = 15, scale = 2)
    protected BigDecimal taxSum;

    @Column(name = "TOTAL_SUM", precision = 15, scale = 2)
    protected BigDecimal totalSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_DOC_ID")
    protected InvoiceDoc invoiceDoc;

    @Column(name = "ALTERNATIVE_NAME_RU", length = 1000)
    protected String alternativeName_ru;

    @Column(name = "ALTERNATIVE_NAME_EN", length = 250)
    protected String alternativeName_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INV_DOC_ID")
    protected InvDoc invDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_DETAIL_ID")
    protected OrderDetail ordDetail;

    public void setOrdDetail(OrderDetail ordDetail) {
        this.ordDetail = ordDetail;
    }

    public OrderDetail getOrdDetail() {
        return ordDetail;
    }


    public void setInvDoc(InvDoc invDoc) {
        this.invDoc = invDoc;
    }

    public InvDoc getInvDoc() {
        return invDoc;
    }


    public void setAlternativeName_ru(String alternativeName_ru) {
        this.alternativeName_ru = alternativeName_ru;
    }

    public String getAlternativeName_ru() {
        return alternativeName_ru;
    }

    public void setAlternativeName_en(String alternativeName_en) {
        this.alternativeName_en = alternativeName_en;
    }

    public String getAlternativeName_en() {
        return alternativeName_en;
    }


    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setSumWithoutNds(BigDecimal sumWithoutNds) {
        this.sumWithoutNds = sumWithoutNds;
    }

    public BigDecimal getSumWithoutNds() {
        return sumWithoutNds;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTaxSum(BigDecimal taxSum) {
        this.taxSum = taxSum;
    }

    public BigDecimal getTaxSum() {
        return taxSum;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public void setInvoiceDoc(InvoiceDoc invoiceDoc) {
        this.invoiceDoc = invoiceDoc;
    }

    public InvoiceDoc getInvoiceDoc() {
        return invoiceDoc;
    }


}