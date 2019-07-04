/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum NomenclatureTypeEnum implements EnumClass<Integer>{

    service(1),
    good(2);

    private Integer id;

    NomenclatureTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static NomenclatureTypeEnum fromId(Integer id) {
        for (NomenclatureTypeEnum at : NomenclatureTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}