/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.thesis.core.entity.TsCard;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|comment_ru")
@Table(name = "CRM_RESOURCE_HISTORY")
@Entity(name = "crm$ResourceHistory")
public class ResourceHistory extends StandardEntity {
    private static final long serialVersionUID = -7330461774122370314L;

    @Column(name = "TYPE_")
    protected Integer type;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected ExtEmployee owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID")
    protected ExtEmployee sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESSEE_ID")
    protected ExtEmployee addressee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_DESTINATION_ID")
    protected Room sourceDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESTINATION_ID")
    protected Room destination;

    @Temporal(TemporalType.DATE)
    @Column(name = "INFORMATION_DATE")
    protected Date informationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "TRANSFER_DATE")
    protected Date transferDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID")
    protected TsCard card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID")
    protected Resource resource;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }


    public void setSourceDestination(Room sourceDestination) {
        this.sourceDestination = sourceDestination;
    }

    public Room getSourceDestination() {
        return sourceDestination;
    }


    public void setType(EventTypeEnum type) {
        this.type = type == null ? null : type.getId();
    }

    public EventTypeEnum getType() {
        return type == null ? null : EventTypeEnum.fromId(type);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setOwner(ExtEmployee owner) {
        this.owner = owner;
    }

    public ExtEmployee getOwner() {
        return owner;
    }

    public void setSender(ExtEmployee sender) {
        this.sender = sender;
    }

    public ExtEmployee getSender() {
        return sender;
    }

    public void setAddressee(ExtEmployee addressee) {
        this.addressee = addressee;
    }

    public ExtEmployee getAddressee() {
        return addressee;
    }

    public void setDestination(Room destination) {
        this.destination = destination;
    }

    public Room getDestination() {
        return destination;
    }

    public void setInformationDate(Date informationDate) {
        this.informationDate = informationDate;
    }

    public Date getInformationDate() {
        return informationDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setCard(TsCard card) {
        this.card = card;
    }

    public TsCard getCard() {
        return card;
    }


}