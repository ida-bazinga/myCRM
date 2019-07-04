/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum TypeTransactionDebiting implements EnumClass<String> {

    PAYSUPPLINER("PAYSUPPLINER"),
    TRANSFERTAX("TRANSFERTAX"),
    TRANSFERUNDERCONTRACT("TRANSFERUNDERCONTRACT");

    private String id;

    TypeTransactionDebiting(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TypeTransactionDebiting fromId(String id) {
        for (TypeTransactionDebiting at : TypeTransactionDebiting.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}