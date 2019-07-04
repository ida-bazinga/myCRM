/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.Contract;

/**
 * @author d.ivanov
 */
@NamePattern("%s|product")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ACT_DETAIL")
@Entity(name = "crm$ActDetail")
public class ActDetail extends BaseUuidEntity {
    private static final long serialVersionUID = 5751271319039118439L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;


    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

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
    @JoinColumn(name = "ORDER_DOC_ID")
    protected OrderDoc orderDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACT_DOC_ID")
    protected ActDoc actDoc;

    @Column(name = "ALTERNATIVE_NAME_RU", length = 1000)
    protected String alternativeName_ru;

    @Column(name = "ALTERNATIVE_NAME_EN", length = 250)
    protected String alternativeName_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AC_DOC_ID")
    protected AcDoc acDoc;

    @Column(name = "IS_AGENCY")
    protected Boolean isAgency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACT_ID")
    protected Contract contract;

    @Column(name = "SECONDARY_AMOUNT", precision = 15, scale = 2)
    protected BigDecimal secondaryAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_DETAIL_ID")
    protected OrderDetail ordDetail;

    public void setOrdDetail(OrderDetail ordDetail) {
        this.ordDetail = ordDetail;
    }

    public OrderDetail getOrdDetail() {
        return ordDetail;
    }


    public void setSecondaryAmount(BigDecimal secondaryAmount) {
        this.secondaryAmount = secondaryAmount;
    }

    public BigDecimal getSecondaryAmount() {
        return secondaryAmount;
    }


    public void setIsAgency(Boolean isAgency) {
        this.isAgency = isAgency;
    }

    public Boolean getIsAgency() {
        return isAgency;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return contract;
    }


    public void setAcDoc(AcDoc acDoc) {
        this.acDoc = acDoc;
    }

    public AcDoc getAcDoc() {
        return acDoc;
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


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public void setActDoc(ActDoc actDoc) {
        this.actDoc = actDoc;
    }

    public ActDoc getActDoc() {
        return actDoc;
    }


    public void setOrderDoc(OrderDoc orderDoc) {
        this.orderDoc = orderDoc;
    }

    public OrderDoc getOrderDoc() {
        return orderDoc;
    }


    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }


    public void setTaxSum(BigDecimal taxSum) {
        this.taxSum = taxSum;
    }

    public BigDecimal getTaxSum() {
        return taxSum;
    }


    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }


    public void setSumWithoutNds(BigDecimal sumWithoutNds) {
        this.sumWithoutNds = sumWithoutNds;
    }

    public BigDecimal getSumWithoutNds() {
        return sumWithoutNds;
    }


    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }



    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }



}