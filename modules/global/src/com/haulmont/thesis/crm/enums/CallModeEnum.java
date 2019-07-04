/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum CallModeEnum implements EnumClass<String> {
    AUTOMATIC("automatic"),
    MANUAL("manual");

    private String id;

    CallModeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CallModeEnum fromId(String id) {
        for (CallModeEnum at : CallModeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}