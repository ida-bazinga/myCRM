/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s|code")
@Table(name = "CRM_PAYMENT_DETAIL")
@Entity(name = "crm$PaymentDetail")
public class PaymentDetail extends BaseUuidEntity {
    private static final long serialVersionUID = 2780824067399622655L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID")
    protected Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INV_DOC_ID")
    protected InvDoc invDoc;

    @Column(name = "OPERATION")
    protected String operation;

    @Column(name = "FULL_SUM", precision = 15, scale = 2)
    protected BigDecimal fullSum;


    public void setInvDoc(InvDoc invDoc) {
        this.invDoc = invDoc;
    }

    public InvDoc getInvDoc() {
        return invDoc;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }



    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }


    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }





    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }


}