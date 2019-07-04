/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum ActivityDirectionEnum implements EnumClass<Integer> {
    NONE(0),
    OUTBOUND(1),
    INBOUND(2),
    BOTH(3),
    INTERNAL(5);

    private Integer id;

    ActivityDirectionEnum(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ActivityDirectionEnum fromId(Integer id) {
        for (ActivityDirectionEnum at : ActivityDirectionEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}