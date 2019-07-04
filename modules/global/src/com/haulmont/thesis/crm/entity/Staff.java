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
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.Contractor;
import com.haulmont.thesis.core.entity.TsCard;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author a.donskoy
 */
@PrimaryKeyJoinColumn(name = "id")
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("S")
@Table(name = "CRM_STAFF")
@Entity(name = "crm$Staff")
public class Staff extends Resource {
    private static final long serialVersionUID = -6781939499136868869L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACTOR_ID")
    protected Contractor contractor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID")
    protected TsCard card;

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setCard(TsCard card) {
        this.card = card;
    }

    public TsCard getCard() {
        return card;
    }


}