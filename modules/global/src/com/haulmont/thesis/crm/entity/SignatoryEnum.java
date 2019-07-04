/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum SignatoryEnum implements EnumClass<Integer>{

    cfo(1),
    ceo(2),
    cfo_ceo(3);

    private Integer id;

    SignatoryEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static SignatoryEnum fromId(Integer id) {
        for (SignatoryEnum at : SignatoryEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}