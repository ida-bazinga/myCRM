/*
 * Copyright (c) 2017 com.haulmont.thesis.entity
 */
package com.haulmont.thesis.crm.core.app.unisender.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author a.donskoy
 */
public enum EmailAvailabilityEnum implements EnumClass<Integer> {
    available(0),
    unreachable(1),
    temp_unreachable(2),
    mailbox_full(3),
    spam_rejected(4),
    spam_folder(5);

    private Integer id;

    EmailAvailabilityEnum(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static EmailAvailabilityEnum fromId(Integer id) {
        for (EmailAvailabilityEnum at : EmailAvailabilityEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}