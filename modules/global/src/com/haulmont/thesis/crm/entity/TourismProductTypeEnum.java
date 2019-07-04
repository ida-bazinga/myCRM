/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum TourismProductTypeEnum implements EnumClass<Integer>{

    Hotel(0),
    Transfer(1),
    Excursion(2),
    Other(3);

    private Integer id;

    TourismProductTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static TourismProductTypeEnum fromId(Integer id) {
        for (TourismProductTypeEnum at : TourismProductTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}