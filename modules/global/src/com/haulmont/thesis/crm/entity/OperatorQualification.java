/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|operator")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_OPERATOR_QUALIFICATION")
@Entity(name = "crm$OperatorQualification")
public class OperatorQualification extends BaseUuidEntity {
    private static final long serialVersionUID = -904061422746140306L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_ID")
    protected Operator operator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUALIFICTION_ID")
    protected Qualification qualifiction;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setQualifiction(Qualification qualifiction) {
        this.qualifiction = qualifiction;
    }

    public Qualification getQualifiction() {
        return qualifiction;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }


}