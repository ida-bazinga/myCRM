/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorValue;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Column;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.core.entity.Organization;
import java.math.BigDecimal;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author p.chizhikov
 */
@Deprecated
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ACT_DOC")
@Entity(name = "crm$ActDoc")
public class ActDoc extends BaseUuidEntity implements Versioned, SoftDelete, Updatable {
    private static final long serialVersionUID = 8855892754130272432L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_CALCULATION_ID")
    protected Currency currencyCalculation;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Temporal(TemporalType.DATE)
    @Column(name = "OUTBOUND_DATE")
    protected Date outboundDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected OrderDoc order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACT_ID")
    protected Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected Employee owner;

    @Column(name = "IS_NDS")
    protected Boolean isNds;

    @Column(name = "TOTAL_SUM", precision = 15, scale = 2)
    protected BigDecimal totalSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "actDoc")
    protected List<ActDetail> actDetails;

    @Column(name = "TAX_SUM", precision = 15, scale = 2)
    protected BigDecimal taxSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHIF_ACCOUNT_ID")
    protected ExtEmployee chifAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GENERAL_DIRECTOR_ID")
    protected ExtEmployee generalDirector;

    @Column(name = "IS_CARRIED")
    protected Boolean isCarried;

    @Column(name = "ADDITIONAL_DETAIL", length = 500)
    protected String additionalDetail;

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

    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public void setAdditionalDetail(String additionalDetail) {
        this.additionalDetail = additionalDetail;
    }

    public String getAdditionalDetail() {
        return additionalDetail;
    }


    public void setIsCarried(Boolean isCarried) {
        this.isCarried = isCarried;
    }

    public Boolean getIsCarried() {
        return isCarried;
    }


    public void setChifAccount(ExtEmployee chifAccount) {
        this.chifAccount = chifAccount;
    }

    public ExtEmployee getChifAccount() {
        return chifAccount;
    }

    public void setGeneralDirector(ExtEmployee generalDirector) {
        this.generalDirector = generalDirector;
    }

    public ExtEmployee getGeneralDirector() {
        return generalDirector;
    }


    public void setTaxSum(BigDecimal taxSum) {
        this.taxSum = taxSum;
    }

    public BigDecimal getTaxSum() {
        return taxSum;
    }


    public void setActDetails(List<ActDetail> actDetails) {
        this.actDetails = actDetails;
    }

    public List<ActDetail> getActDetails() {
        return actDetails;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }

    public void setOrder(OrderDoc order) {
        this.order = order;
    }

    public OrderDoc getOrder() {
        return order;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
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

    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    public Employee getOwner() {
        return owner;
    }

    public void setIsNds(Boolean isNds) {
        this.isNds = isNds;
    }

    public Boolean getIsNds() {
        return isNds;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
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


    public void setCurrencyCalculation(Currency currencyCalculation) {
        this.currencyCalculation = currencyCalculation;
    }

    public Currency getCurrencyCalculation() {
        return currencyCalculation;
    }


}