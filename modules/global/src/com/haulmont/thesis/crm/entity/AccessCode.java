/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum AccessCode implements EnumClass<Integer> {

    allowed(1),
    unknown(2),
    invalidAccess(3);

    private Integer id;

    AccessCode(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static AccessCode fromId(Integer id) {
        for (AccessCode at : AccessCode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}