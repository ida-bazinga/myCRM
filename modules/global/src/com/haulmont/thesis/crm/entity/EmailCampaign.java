/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.List;

/**
 * @author Redcu
 */
@NamePattern("%s|name")
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("250")
@Table(name = "CRM_EMAIL_CAMPAIGN")
@Entity(name = "crm$EmailCampaign")
public class EmailCampaign extends BaseCampaign {
    private static final long serialVersionUID = 7899246032206179600L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "campaign")
    protected List<EmailCampaignTarget> targets;

    @Column(name = "LIST_ID", length = 50)
    protected String list_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_STATUS_ID")
    protected MessageCampaignStatus campaignStatus;

    @Column(name = "NAME_EXPANSION", length = 150)
    protected String nameExpansion;

    @Transient
    @MetaProperty
    protected String anyTxt;

    public void setNameExpansion(String nameExpansion) {
        this.nameExpansion = nameExpansion;
    }

    public String getNameExpansion() {
        return nameExpansion;
    }


    public void setCampaignStatus(MessageCampaignStatus campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public MessageCampaignStatus getCampaignStatus() {
        return campaignStatus;
    }


    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getList_id() {
        return list_id;
    }


    public void setTargets(List<EmailCampaignTarget> targets) {
        this.targets = targets;
    }

    public List<EmailCampaignTarget> getTargets() {
        return targets;
    }

    public void setAnyTxt(String anyTxt) {
        this.anyTxt = anyTxt;
    }

    public String getAnyTxt() {
        return anyTxt;
    }


}