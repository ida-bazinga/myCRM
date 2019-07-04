/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum SoftPhoneStatus implements EnumClass<Integer> {
    NoConnection(0),
    LoggedIn(1),
    LoggedOut(2);

    private Integer id;

    SoftPhoneStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static SoftPhoneStatus fromId(Integer id) {
        for (SoftPhoneStatus at : SoftPhoneStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}