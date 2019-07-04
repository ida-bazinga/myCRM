/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum CompanyCellsEnum implements EnumClass<String> {

    kpp("KPP"),
    fullName("FULLNAME"),
    addressFirstDoc("ADDRESSFIRSTDOC");

    private String id;

    CompanyCellsEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CompanyCellsEnum fromId(String id) {
        for (CompanyCellsEnum at : CompanyCellsEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}