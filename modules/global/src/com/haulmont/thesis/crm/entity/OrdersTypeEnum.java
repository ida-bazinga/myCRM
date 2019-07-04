/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum OrdersTypeEnum implements EnumClass<Integer>{

    travel(1),
    exhibition(2),
    technical(3),
    delivery(4),
    catering(5),
    lease(6);

    private Integer id;

    OrdersTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static OrdersTypeEnum fromId(Integer id) {
        for (OrdersTypeEnum at : OrdersTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}