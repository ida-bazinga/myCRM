/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum RfNdsEnum implements EnumClass<Integer>{

    Common(1),
    Simplified(2),
    UTII(3),
    PSN(4),
    UAT(5);

    private Integer id;

    RfNdsEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static RfNdsEnum fromId(Integer id) {
        for (RfNdsEnum at : RfNdsEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}