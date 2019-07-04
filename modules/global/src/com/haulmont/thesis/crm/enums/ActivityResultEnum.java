/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum ActivityResultEnum implements EnumClass<String> {

    NO_ANSWER("no-answer"),
    CALL_TRANSFER("call-transfer"),
    MISSED_CALL("missed-call"),
    RECALL_LATER("recall-later"),
    RECALL_NOW("recall-now"),
    NON_ACTUAL_COMM("non-actual-comm"),
    ERROR("error"),
    AUTO_ANSWER("auto-answer");

    private String id;

    ActivityResultEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ActivityResultEnum fromId(String id) {
        for (ActivityResultEnum at : ActivityResultEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}