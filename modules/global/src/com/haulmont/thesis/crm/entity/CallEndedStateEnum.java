/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
@Deprecated
public enum CallEndedStateEnum implements EnumClass<Integer>{
    FAILED(0),
    AUTODROP(1),
    MANUALDROP(2),
    NORMALDROP(3);

    private Integer id;

    CallEndedStateEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CallEndedStateEnum fromId(Integer id) {
        for (CallEndedStateEnum at : CallEndedStateEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}