/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum PaymentsTypeEnum implements EnumClass<Integer>{

    cash(1),
    cashless(2);

    private Integer id;

    PaymentsTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static PaymentsTypeEnum fromId(Integer id) {
        for (PaymentsTypeEnum at : PaymentsTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}