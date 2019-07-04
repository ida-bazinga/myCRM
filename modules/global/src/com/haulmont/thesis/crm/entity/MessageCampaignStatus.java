/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;

import javax.persistence.*;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamePattern("%s|name_ru")
@Table(name = "CRM_MESSAGE_CAMPAIGN_STATUS")
@Entity(name = "crm$MessageCampaignStatus")
public class MessageCampaignStatus extends BaseLookup {
    private static final long serialVersionUID = -8762693219440209931L;

    @Column(name = "REQUEST_MESSAGES_STATUSES")
    protected Boolean requestMessagesStatuses;

    public void setRequestMessagesStatuses(Boolean requestMessagesStatuses) {
        this.requestMessagesStatuses = requestMessagesStatuses;
    }

    public Boolean getRequestMessagesStatuses() {
        return requestMessagesStatuses;
    }


}