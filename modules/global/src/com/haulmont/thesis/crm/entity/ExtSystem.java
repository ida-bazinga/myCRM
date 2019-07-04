/*
 * Copyright (c) 2018 com.haulmont.thesis.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum ExtSystem implements EnumClass<String> {

    BP1CEFI("bp1cefi"),
    BP1CNEVA("bp1cneva"),
    DADATASUGGESTION("dadata-suggestion"),
    DADATASTANDART("dadata-standatrt");

    private String id;

    ExtSystem(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ExtSystem fromId(String id) {
        for (ExtSystem at : ExtSystem.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}