/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum BugetItemsTypeEnum implements EnumClass<Integer>{

    income(1),
    outcome(2);

    private Integer id;

    BugetItemsTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static BugetItemsTypeEnum fromId(Integer id) {
        for (BugetItemsTypeEnum at : BugetItemsTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}