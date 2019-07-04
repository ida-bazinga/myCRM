/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum CallDirectionEnum implements EnumClass<Integer>{

    NONE(0),
    OUTBOUND(1),
    INBOUND(2),
    BOTH(3);

    private Integer id;

    CallDirectionEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CallDirectionEnum fromId(Integer id) {
        for (CallDirectionEnum at : CallDirectionEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}