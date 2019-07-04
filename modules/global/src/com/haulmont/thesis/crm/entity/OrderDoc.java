/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.thesis.core.entity.Contract;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.core.entity.Organization;

/**
 * @author p.chizhikov
 */
@Deprecated
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name = "crm$OrderDoc")
@Table(name = "CRM_ORDER_DOC")
public class OrderDoc extends BaseUuidEntity implements Versioned, SoftDelete, Updatable {
    private static final long serialVersionUID = 6599748888221635531L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "ORDER_TYPE")
    protected Integer orderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACT_ID")
    protected Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @Column(name = "IS_NDS")
    protected Boolean isNds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected OrderStatus status;

    @Column(name = "REASON_FOR_REJECTION", length = 2000)
    protected String reasonForRejection;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REFUSAL_DATE")
    protected Date refusalDate;

    @Column(name = "FULL_SUM", precision = 15, scale = 2)
    protected BigDecimal fullSum;

    @Column(name = "TAX_SUM", precision = 15, scale = 2)
    protected BigDecimal taxSum;

    @Column(name = "RF_NDS")
    protected Integer rfNds;

    @Column(name = "ADDITIONAL_INFORMATION", length = 4000)
    protected String additionalInformation;

    @Column(name = "ID1_C", length = 50)
    protected String id1c;

    @Temporal(TemporalType.DATE)
    @Column(name = "OUTBOUND_DATE")
    protected Date outboundDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected Employee owner;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @Column(name = "NOTE", length = 1000)
    protected String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORDER_ID")
    protected OrderDoc parentOrder;

    @Column(name = "IS_CARRIED")
    protected Boolean isCarried;

    @Version
    @Column(name = "VERSION")
    protected Integer version;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setIsCarried(Boolean isCarried) {
        this.isCarried = isCarried;
    }

    public Boolean getIsCarried() {
        return isCarried;
    }


    public void setParentOrder(OrderDoc parentOrder) {
        this.parentOrder = parentOrder;
    }

    public OrderDoc getParentOrder() {
        return parentOrder;
    }


    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }



    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    public Employee getOwner() {
        return owner;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }


    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }


    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
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


    public Integer getVersion() {
        return version;
    }


    public ExtCompany getCompany() {
        return company;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }


    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }



    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setOrderType(OrdersTypeEnum orderType) {
        this.orderType = orderType == null ? null : orderType.getId();
    }

    public OrdersTypeEnum getOrderType() {
        return orderType == null ? null : OrdersTypeEnum.fromId(orderType);
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return contract;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setIsNds(Boolean isNds) {
        this.isNds = isNds;
    }

    public Boolean getIsNds() {
        return isNds;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setRefusalDate(Date refusalDate) {
        this.refusalDate = refusalDate;
    }

    public Date getRefusalDate() {
        return refusalDate;
    }

    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }

    public void setTaxSum(BigDecimal taxSum) {
        this.taxSum = taxSum;
    }

    public BigDecimal getTaxSum() {
        return taxSum;
    }

    public void setRfNds(RfNdsEnum rfNds) {
        this.rfNds = rfNds == null ? null : rfNds.getId();
    }

    public RfNdsEnum getRfNds() {
        return rfNds == null ? null : RfNdsEnum.fromId(rfNds);
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }


}