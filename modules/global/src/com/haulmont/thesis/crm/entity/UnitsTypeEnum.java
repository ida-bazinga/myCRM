/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum UnitsTypeEnum implements EnumClass<Integer>{

    time(1),
    length(2),
    square(3),
    volume(5),
    mass(4),
    technical(6),
    economic(7);

    private Integer id;

    UnitsTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static UnitsTypeEnum fromId(Integer id) {
        for (UnitsTypeEnum at : UnitsTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}