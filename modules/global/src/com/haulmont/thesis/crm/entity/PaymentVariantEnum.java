/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum PaymentVariantEnum implements EnumClass<Integer>{

    prepaidExpense(0),
    regularPayment(1);

    private Integer id;

    PaymentVariantEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static PaymentVariantEnum fromId(Integer id) {
        for (PaymentVariantEnum at : PaymentVariantEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}