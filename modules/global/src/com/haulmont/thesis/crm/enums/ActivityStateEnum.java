/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum ActivityStateEnum implements EnumClass<String> {
    NEW(",New,"),
    IN_WORK(",InWork,"),
    COMPLETED(",Completed,"),
    SCHEDULED(",Scheduled,"),
    CANCELED(",Canceled,"),
    LOCKED(",Locked,");

    private String id;

    ActivityStateEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ActivityStateEnum fromId(String id) {
        for (ActivityStateEnum at : ActivityStateEnum.values()) {
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