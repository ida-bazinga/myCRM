/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
@Deprecated
public enum OutboundCampaignTypeEnum implements EnumClass<String>{

    automatic("AUTO"),
    manual("MANUAL");

    private String id;

    OutboundCampaignTypeEnum (String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static OutboundCampaignTypeEnum fromId(String id) {
        for (OutboundCampaignTypeEnum at : OutboundCampaignTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}