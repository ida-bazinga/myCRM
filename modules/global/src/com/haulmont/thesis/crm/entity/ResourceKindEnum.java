/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum ResourceKindEnum implements EnumClass<Integer>{

    Room(2),
    Equipment(1),
    Staff(4),
    Material(3),
    Ware(5);

    private Integer id;

    ResourceKindEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static ResourceKindEnum fromId(Integer id) {
        for (ResourceKindEnum at : ResourceKindEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}