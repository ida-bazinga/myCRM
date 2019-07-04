/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum BookingEventDetailStatusEnum implements EnumClass<String> {

    NEW("new"),
    EDITED("edited"),
    REMOVED("removed"),
    APPROVED("approved");

    private String id;

    BookingEventDetailStatusEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BookingEventDetailStatusEnum fromId(String id) {
        for (BookingEventDetailStatusEnum at : BookingEventDetailStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}