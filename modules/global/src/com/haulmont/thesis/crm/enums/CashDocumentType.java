/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum CashDocumentType implements EnumClass<String>{

    SELL("Sell"),
    PAYBACK("Payback"),
    UNKNOWN("Unknown");

    private String id;

    CashDocumentType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static CashDocumentType fromId(String id) {
        for (CashDocumentType at : CashDocumentType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}