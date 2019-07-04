/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum GeolocationTypeEnum implements EnumClass<String> {
    location("LOCATION"),
    map("MAP");

    private String id;

    GeolocationTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static GeolocationTypeEnum fromId(String id) {
        for (GeolocationTypeEnum at : GeolocationTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}