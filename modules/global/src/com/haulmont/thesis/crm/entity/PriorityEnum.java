/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum PriorityEnum implements EnumClass<String>{

    high("HIGH"),
    medium("MEDIUM"),
    low("LOW");

    private String id;

    PriorityEnum (String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static PriorityEnum fromId(String id) {
        for (PriorityEnum at : PriorityEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}