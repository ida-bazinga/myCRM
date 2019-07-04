/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.Date;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;

/**
 * @author a.donskoy
 */
@PrimaryKeyJoinColumn(name = "id")
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("E")
@Table(name = "CRM_EQUIPMENT")
@Entity(name = "crm$Equipment")
public class Equipment extends Resource {
    private static final long serialVersionUID = -1789527719204512498L;

    @Column(name = "NUMBER_MTO", length = 50)
    protected String numberMto;

    @Column(name = "BARCODE_MTO", length = 50)
    protected String barcodeMto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected ExtEmployee owner;

    @Temporal(TemporalType.DATE)
    @Column(name = "WARRANTY_EXPIRATION_DATE")
    protected Date warrantyExpirationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "WRITE_OFFS_DATE")
    protected Date writeOffsDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @Column(name = "MODEL", length = 500)
    protected String model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKPLACE_ID")
    protected Workplace workplace;

    @Column(name = "PRICE")
    protected BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }


    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public Workplace getWorkplace() {
        return workplace;
    }


    public void setBarcodeMto(String barcodeMto) {
        this.barcodeMto = barcodeMto;
    }

    public String getBarcodeMto() {
        return barcodeMto;
    }

    public void setOwner(ExtEmployee owner) {
        this.owner = owner;
    }

    public ExtEmployee getOwner() {
        return owner;
    }

    public void setWarrantyExpirationDate(Date warrantyExpirationDate) {
        this.warrantyExpirationDate = warrantyExpirationDate;
    }

    public Date getWarrantyExpirationDate() {
        return warrantyExpirationDate;
    }

    public void setWriteOffsDate(Date writeOffsDate) {
        this.writeOffsDate = writeOffsDate;
    }

    public Date getWriteOffsDate() {
        return writeOffsDate;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }


    public void setNumberMto(String numberMto) {
        this.numberMto = numberMto;
    }

    public String getNumberMto() {
        return numberMto;
    }


}