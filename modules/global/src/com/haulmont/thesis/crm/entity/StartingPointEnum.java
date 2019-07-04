/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum StartingPointEnum implements EnumClass<String> {

    FROMSTARTDATE("fromstartdate"),
    FROMSTARTDATEINSTALLATION("fromstartdateinstallation"),
    FROMENDDATEDEINSTALLATION("fromenddatedeinstallation"),
    FROMSTARTDATEPROJECT("fromstartdateproject");

    private String id;

    StartingPointEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StartingPointEnum fromId(String id) {
        for (StartingPointEnum at : StartingPointEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}