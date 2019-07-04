/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum ActivityResultTypeEnum implements EnumClass<Integer>{
    SUCCESSFUL(0),
    CALLBACKLATER(1),
    CALLBACKNOW(2),
    FAIL(3),
    UNSUCCESSFUL(4),
    NORESPONSE(5),
    REDIRECT(6);

    private Integer id;

    ActivityResultTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static ActivityResultTypeEnum fromId(Integer id) {
        for (ActivityResultTypeEnum at : ActivityResultTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}