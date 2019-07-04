/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum SeatingType implements EnumClass<Integer> {

    theatre(1),
    classroom(2),
    discussion(3),
    banquet(4),
    furshet(5),
    empty(6);

    private Integer id;

    SeatingType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static SeatingType fromId(Integer id) {
        for (SeatingType at : SeatingType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}