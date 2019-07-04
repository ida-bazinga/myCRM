/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum InfoSourceTypeEnum implements EnumClass<Integer>{

    database(1),
    catalog(2),
    vcard(3),
    site(4),
    journal(5);

    private Integer id;

    InfoSourceTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static InfoSourceTypeEnum fromId(Integer id) {
        for (InfoSourceTypeEnum at : InfoSourceTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}