/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|code")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COST")
@Entity(name = "crm$Cost")
@TrackEditScreenHistory
public class Cost extends StandardEntity {
    private static final long serialVersionUID = 803903351902919808L;

    @Column(name = "CODE", unique = true, length = 50)
    protected String code;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @Column(name = "COMMENT_EN", length = 1000)
    protected String comment_en;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;

    @Column(name = "PRIMARY_COST", nullable = false)
    protected BigDecimal primaryCost;

    @Column(name = "SECONDARY_COST")
    protected BigDecimal secondaryCost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRIMARY_CURRENCY_ID")
    protected Currency primaryCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECONDARY_CURRENCY_ID")
    protected Currency secondaryCurrency;

    @Column(name = "ID1C")
    protected String id1c;

    @Column(name = "COST_TYPE")
    protected Integer costType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @Column(name = "COMMENT2_RU", length = 1000)
    protected String comment2_ru;

    @Column(name = "TERNIARY_COST")
    protected BigDecimal terniaryCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERNIARY_CURRENCY_ID")
    protected Currency terniaryCurrency;

    @Column(name = "COMMENT3", length = 1000)
    protected String comment3;

    public void setComment3(String comment3) {
        this.comment3 = comment3;
    }

    public String getComment3() {
        return comment3;
    }


    public void setTerniaryCost(BigDecimal terniaryCost) {
        this.terniaryCost = terniaryCost;
    }

    public BigDecimal getTerniaryCost() {
        return terniaryCost;
    }

    public void setTerniaryCurrency(Currency terniaryCurrency) {
        this.terniaryCurrency = terniaryCurrency;
    }

    public Currency getTerniaryCurrency() {
        return terniaryCurrency;
    }


    public void setComment2_ru(String comment2_ru) {
        this.comment2_ru = comment2_ru;
    }

    public String getComment2_ru() {
        return comment2_ru;
    }


    public void setCostType(CostTypeEnum costType) {
        this.costType = costType == null ? null : costType.getId();
    }

    public CostTypeEnum getCostType() {
        return costType == null ? null : CostTypeEnum.fromId(costType);
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    public void setSecondaryCurrency(Currency secondaryCurrency) {
        this.secondaryCurrency = secondaryCurrency;
    }

    public Currency getSecondaryCurrency() {
        return secondaryCurrency;
    }


    public void setPrimaryCurrency(Currency primaryCurrency) {
        this.primaryCurrency = primaryCurrency;
    }

    public Currency getPrimaryCurrency() {
        return primaryCurrency;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_en() {
        return comment_en;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setPrimaryCost(BigDecimal primaryCost) {
        this.primaryCost = primaryCost;
    }

    public BigDecimal getPrimaryCost() {
        return primaryCost;
    }

    public void setSecondaryCost(BigDecimal secondaryCost) {
        this.secondaryCost = secondaryCost;
    }

    public BigDecimal getSecondaryCost() {
        return secondaryCost;
    }

    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


}