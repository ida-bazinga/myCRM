/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum OriginatorType implements EnumClass<Integer> {
    None(0),
    Queue(1),
    RingGroup(2),
    Extension(3),
    Ivr(4),
    Conference(5),
    ParkingPlace(6),
    Fax(7),
    ExternalLine(8),
    SpecialMenu(9);

    private Integer id;

    public Integer getId() {
        return id;
    }

    OriginatorType(Integer value) {
        this.id = value;
    }

    @Nullable
    public static OriginatorType fromId(Integer id) {
        for (OriginatorType at : OriginatorType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}