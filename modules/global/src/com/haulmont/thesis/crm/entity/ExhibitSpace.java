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
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;
import org.apache.openjpa.persistence.Persistent;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_EXHIBIT_SPACE")
@Entity(name = "crm$ExhibitSpace")
public class ExhibitSpace extends BaseLookup {
    private static final long serialVersionUID = 3178373426206728828L;

    @Column(name = "AREA", precision = 12, scale = 4)
    protected BigDecimal area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_ID")
    protected ExtCompany operator;

    @Column(name = "NAME_EN", length = 1000)
    protected String name_en;

    @Column(name = "COMMENT_EN", length = 4000)
    protected String comment_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_ID")
    protected Unit unit;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_ADDRESS_ID")
    protected Address extAddress;


    @Persistent
    @Column(name = "EVOTOR_STORE_ID")
    protected UUID evotorStoreId;

    public UUID getEvotorStoreId() {
        return evotorStoreId;
    }

    public void setEvotorStoreId(UUID evotorStoreId) {
        this.evotorStoreId = evotorStoreId;
    }



    public void setExtAddress(Address extAddress) {
        this.extAddress = extAddress;
    }

    public Address getExtAddress() {
        return extAddress;
    }


    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }


    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_en() {
        return comment_en;
    }


    public void setOperator(ExtCompany operator) {
        this.operator = operator;
    }

    public ExtCompany getOperator() {
        return operator;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }


    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getArea() {
        return area;
    }


}