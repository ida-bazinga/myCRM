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
 * @author a.donskoy
 */
@NamePattern("%s %s|resource,comment_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ORD_RESOURCE")
@Entity(name = "crm$OrdResource")
public class OrdResource extends BaseUuidEntity {
    private static final long serialVersionUID = -2378275793578546535L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID")
    protected Resource resource;

    @Column(name = "QUANTITY")
    protected BigDecimal quantity;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_DETAIL_ID")
    protected OrderDetail orderDetail;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }


}