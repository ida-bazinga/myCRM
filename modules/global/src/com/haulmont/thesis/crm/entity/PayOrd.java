/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.core.entity.OrganizationAccount;
import com.haulmont.thesis.core.entity.TsCard;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author d.ivanov
 */
@NamePattern("%s %s|number,createTs")
@PrimaryKeyJoinColumn(name = "CARD_ID")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("2101")
@Table(name = "CRM_PAY_ORD")
@Entity(name = "crm$PayOrd")
public class PayOrd extends TsCard {
    private static final long serialVersionUID = 734843525656712265L;

    @Column(name = "TYPE_TRANSACTION", nullable = false)
    protected String typeTransaction;

    @Column(name = "NUMBER_", length = 11)
    protected String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ACCOUNT_ID")
    protected ExtContractorAccount companyAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ACCOUNT_ID")
    protected OrganizationAccount organizationAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACT_ID")
    protected Contract contract;

    @Column(name = "FULL_SUM", nullable = false, precision = 15, scale = 2)
    protected BigDecimal fullSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @Column(name = "SUM_TAX", nullable = false, precision = 15, scale = 2)
    protected BigDecimal sumTax;

    @Column(name = "PAYMENT_DESTINATION", nullable = false, length = 210)
    protected String paymentDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTEGRATION_RESOLVER_ID")
    protected IntegrationResolver integrationResolver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PAYMENT_DATE_FACT")
    protected Date paymentDateFact;





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INV_DOC_ID")
    protected InvDoc invDoc;

    public void setInvDoc(InvDoc invDoc) {
        this.invDoc = invDoc;
    }

    public InvDoc getInvDoc() {
        return invDoc;
    }


    public void setPaymentDateFact(Date paymentDateFact) {
        this.paymentDateFact = paymentDateFact;
    }

    public Date getPaymentDateFact() {
        return paymentDateFact;
    }


    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }


    public void setTypeTransaction(TypeTransactionDebiting typeTransaction) {
        this.typeTransaction = typeTransaction == null ? null : typeTransaction.getId();
    }

    public TypeTransactionDebiting getTypeTransaction() {
        return typeTransaction == null ? null : TypeTransactionDebiting.fromId(typeTransaction);
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
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

    public void setCompanyAccount(ExtContractorAccount companyAccount) {
        this.companyAccount = companyAccount;
    }

    public ExtContractorAccount getCompanyAccount() {
        return companyAccount;
    }

    public void setOrganizationAccount(OrganizationAccount organizationAccount) {
        this.organizationAccount = organizationAccount;
    }

    public OrganizationAccount getOrganizationAccount() {
        return organizationAccount;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return contract;
    }

    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public void setSumTax(BigDecimal sumTax) {
        this.sumTax = sumTax;
    }

    public BigDecimal getSumTax() {
        return sumTax;
    }

    public void setPaymentDestination(String paymentDestination) {
        this.paymentDestination = paymentDestination;
    }

    public String getPaymentDestination() {
        return paymentDestination;
    }

    public void setIntegrationResolver(IntegrationResolver integrationResolver) {
        this.integrationResolver = integrationResolver;
    }

    public IntegrationResolver getIntegrationResolver() {
        return integrationResolver;
    }




}