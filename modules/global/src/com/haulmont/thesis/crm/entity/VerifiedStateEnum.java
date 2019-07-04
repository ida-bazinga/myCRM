/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum VerifiedStateEnum implements EnumClass<Integer>{

    NOTVERIFIED(0),
    VERIFIED(1),
    CANNOTVERIFIED(-1);

    private Integer id;

    VerifiedStateEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static VerifiedStateEnum fromId(Integer id) {
        for (VerifiedStateEnum at : VerifiedStateEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}