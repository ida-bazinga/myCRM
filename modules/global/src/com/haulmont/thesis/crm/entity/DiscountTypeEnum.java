/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum DiscountTypeEnum implements EnumClass<Integer>{

    percent(1),
    sumPrice(2),
    sumTotal(3);

    private Integer id;

    DiscountTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static DiscountTypeEnum fromId(Integer id) {
        for (DiscountTypeEnum at : DiscountTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}