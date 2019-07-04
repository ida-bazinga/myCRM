/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Contractor;
import com.haulmont.thesis.core.entity.Correspondent;

/**
 * @author k.khoroshilov
 */
@MetaClass(name = "crm$SaleDocsHistoryInfo")
public class SaleDocsHistoryInfo extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 5670590458967686176L;

    @MetaProperty
    protected Contractor customer;

    @MetaProperty
    protected Correspondent payer;

    @MetaProperty
    protected ExtProject project;

    @MetaProperty
    protected Contract contract;

    @MetaProperty
    protected OrdDoc salesOrder;


    @MetaProperty
    protected Currency currency;

    @MetaProperty
    protected String orderFullSum;

    @MetaProperty
    protected String sumActFullSum;

    @MetaProperty
    protected String invoices;

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }


    public String getOrderFullSum() {
        return orderFullSum;
    }

    public void setOrderFullSum(String orderFullSum) {
        this.orderFullSum = orderFullSum;
    }


    public String getSumActFullSum() {
        return sumActFullSum;
    }

    public void setSumActFullSum(String sumActFullSum) {
        this.sumActFullSum = sumActFullSum;
    }


    public void setInvoices(String invoices) {
        this.invoices = invoices;
    }

    public String getInvoices() {
        return invoices;
    }



    public void setPayer(Correspondent payer) {
        this.payer = payer;
    }

    public Correspondent getPayer() {
        return payer;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return contract;
    }

    public void setSalesOrder(OrdDoc salesOrder) {
        this.salesOrder = salesOrder;
    }

    public OrdDoc getSalesOrder() {
        return salesOrder;
    }


    public Contractor getCustomer() {
        return customer;
    }

    public void setCustomer(Contractor customer) {
        this.customer = customer;
    }



}