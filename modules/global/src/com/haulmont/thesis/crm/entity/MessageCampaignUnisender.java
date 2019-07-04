/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.Date;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamePattern("%s|name_ru")
@Table(name = "CRM_MESSAGE_CAMPAIGN_UNISENDER")
@Entity(name = "crm$MessageCampaignUnisender")
public class MessageCampaignUnisender extends BaseLookup {
    private static final long serialVersionUID = 6654046457543777868L;

/*
    public MessageCampaignUnisender(Integer id, String title) {
        this.code = id.toString();
        this.name_ru = title;
    }
*/
}