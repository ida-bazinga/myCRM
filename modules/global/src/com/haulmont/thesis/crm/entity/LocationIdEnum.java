/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum LocationIdEnum implements EnumClass<Integer> {

    location_A(46),
    location_B(47),
    location_CDE(48),
    location_G(49),
    location_H(50);

    private Integer id;

    LocationIdEnum(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static LocationIdEnum fromId(Integer id) {
        for (LocationIdEnum at : LocationIdEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}