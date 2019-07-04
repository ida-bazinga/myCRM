/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum StatusDocSales implements EnumClass<String> {

    NEW("new"),
    IN1C("in1c"),
    MARKER1C("marker1c"),
    EDIT("edit"),
    DELETE("del"),
    PAID("paid"),
    REFUSED("refused"),
    SHIP("ship"),
    PREPARE("prepare");

    private String id;

    StatusDocSales(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StatusDocSales fromId(String id) {
        for (StatusDocSales at : StatusDocSales.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}