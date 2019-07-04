/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.*;
import java.math.BigDecimal;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Updatable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.haulmont.cuba.core.entity.SoftDelete;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|product")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ORDER_DETAIL")
@Entity(name = "crm$OrderDetail")
public class OrderDetail extends BaseUuidEntity implements Updatable, SoftDelete {
    private static final long serialVersionUID = 6367419381045591910L;

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

    @Column(name = "MARGIN_PERCENT", precision = 15, scale = 2)
    protected BigDecimal marginPercent;

    @Column(name = "DISCOUNT_PERCENT", precision = 15, scale = 2)
    protected BigDecimal discountPercent;

    @Column(name = "MARGIN_SUM", precision = 15, scale = 2)
    protected BigDecimal marginSum;

    @Column(name = "DISCOUNT_SUM", precision = 15, scale = 2)
    protected BigDecimal discountSum;

    @Column(name = "DISCOUNT_TYPE")
    protected Integer discountType;

    @Column(name = "ALTERNATIVE_NAME_RU", length = 1000)
    protected String alternativeName_ru;

    @Column(name = "ALTERNATIVE_NAME_EN", length = 250)
    protected String alternativeName_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_DOC_ID")
    protected OrdDoc ordDoc;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPENDENT_DETAIL_ID")
    protected OrderDetail dependentDetail;

    @Column(name = "SECONDARY_AMOUNT", precision = 15, scale = 2)
    protected BigDecimal secondaryAmount;

    @Column(name = "COST_PER_PIECE", precision = 15, scale = 2)
    protected BigDecimal costPerPiece;

    @Column(name = "CLIENT_NAME")
    protected String clientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOTEL_ID")
    protected Guesthouse hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCURSION_ID")
    protected TourType excursion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_TYPE_ID")
    protected TransferType transferType;

    @OneToMany(mappedBy = "orderDetail")
    protected Set<OrdResource> ordResource;

    @Transient
    @MetaProperty
    protected UUID tempId;

    @Column(name = "ORDER_DETAIL_GROUP")
    protected String orderDetailGroup;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;


    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    public void setOrderDetailGroup(String orderDetailGroup) {
        this.orderDetailGroup = orderDetailGroup;
    }

    public String getOrderDetailGroup() {
        return orderDetailGroup;
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


    public void setOrdResource(Set<OrdResource> ordResource) {
        this.ordResource = ordResource;
    }

    public Set<OrdResource> getOrdResource() {
        return ordResource;
    }


    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setHotel(Guesthouse hotel) {
        this.hotel = hotel;
    }

    public Guesthouse getHotel() {
        return hotel;
    }

    public void setExcursion(TourType excursion) {
        this.excursion = excursion;
    }

    public TourType getExcursion() {
        return excursion;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public TransferType getTransferType() {
        return transferType;
    }


    public void setSecondaryAmount(BigDecimal secondaryAmount) {
        this.secondaryAmount = secondaryAmount;
    }

    public BigDecimal getSecondaryAmount() {
        return secondaryAmount;
    }

    public void setCostPerPiece(BigDecimal costPerPiece) {
        this.costPerPiece = costPerPiece;
    }

    public BigDecimal getCostPerPiece() {
        return costPerPiece;
    }


    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public Date getUpdateTs() {
        return updateTs;
    }


    public void setDependentDetail(OrderDetail dependentDetail) {
        this.dependentDetail = dependentDetail;
    }

    public OrderDetail getDependentDetail() {
        return dependentDetail;
    }


    public void setOrdDoc(OrdDoc ordDoc) {
        this.ordDoc = ordDoc;
    }

    public OrdDoc getOrdDoc() {
        return ordDoc;
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


    public void setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType == null ? null : discountType.getId();
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType == null ? null : DiscountTypeEnum.fromId(discountType);
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }



    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
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

    public void setMarginPercent(BigDecimal marginPercent) {
        this.marginPercent = marginPercent;
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setMarginSum(BigDecimal marginSum) {
        this.marginSum = marginSum;
    }

    public BigDecimal getMarginSum() {
        return marginSum;
    }

    public void setDiscountSum(BigDecimal discountSum) {
        this.discountSum = discountSum;
    }

    public BigDecimal getDiscountSum() {
        return discountSum;
    }

    public void setTempId(UUID tempId) {
        this.tempId = tempId;
    }

    public UUID getTempId() {
       return tempId;
    }


}