/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum SoftphoneProfileEnum implements EnumClass<String>{
    available("10"),
    away("20"),
    outofoffice("30"),
    custom1("40"),
    custom2("50");

    private String id;

    SoftphoneProfileEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static SoftphoneProfileEnum fromId(String id) {
        for (SoftphoneProfileEnum at : SoftphoneProfileEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}