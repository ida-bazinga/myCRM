/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CALL_CAMPAIGN_STATUS")
@Entity(name = "crm$CallCampaignStatus")
public class CallCampaignStatus extends BaseLookup {
    private static final long serialVersionUID = 7359681844332396518L;

}