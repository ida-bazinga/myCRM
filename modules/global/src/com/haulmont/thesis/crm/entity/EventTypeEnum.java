/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum EventTypeEnum implements EnumClass<Integer>{
    TRANSFER(1),
    INVENTORIZATION(2),
    MAINTENANCE(3),
    BOUGHT(4),
    SOLD(5),
    WRITEOFF(6),
    SALVAGING(7),
    REPAIRS(8),
    COMMISSIONING(9);

    private Integer id;

    EventTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static EventTypeEnum fromId(Integer id) {
        for (EventTypeEnum at : EventTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}