/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum CampaignState implements EnumClass<String>{
    New(",New,"),
    InWork(",InWork,"),
    Completed(",Completed,"),
    NotCompleted(",NotCompleted,"),
    Canceled(",Canceled,"),
    DelayedStart(",DelayedStart,");

    private String id;

    CampaignState (String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static CampaignState fromId(String id) {
        for (CampaignState at : CampaignState.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public String getName() {
        return id.substring(1, id.length() - 1);
    }
}