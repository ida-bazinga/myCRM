/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum TypeSaleEnum implements EnumClass<String> {

    Commercial("COMMERCIAL"),
    GovermentContract("GOVERMENTCONTRACT"),
    PartnershipContract("PARTNERSHIPCONTRACT");

    private String id;

    TypeSaleEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TypeSaleEnum fromId(String id) {
        for (TypeSaleEnum at : TypeSaleEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}