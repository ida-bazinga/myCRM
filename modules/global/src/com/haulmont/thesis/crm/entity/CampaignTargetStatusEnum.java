/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
@Deprecated
public enum CampaignTargetStatusEnum implements EnumClass<String>{

    PLANED("PLANED"),
    INVOLVED("INVOLVED"),
    COMPLETED("COMPLETED");

    private String id;

    CampaignTargetStatusEnum (String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static CampaignTargetStatusEnum fromId(String id) {
        for (CampaignTargetStatusEnum at : CampaignTargetStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}