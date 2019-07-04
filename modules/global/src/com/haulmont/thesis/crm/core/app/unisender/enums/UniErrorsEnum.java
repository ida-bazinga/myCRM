/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.core.app.unisender.enums
 */
package com.haulmont.thesis.crm.core.app.unisender.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author a.donskoy
 */
public enum UniErrorsEnum implements EnumClass<String> {

    unspecified("unspecified"),
    invalid_api_key("invalid_api_key"),
    access_denied("access_denied"),
    unknown_method("unknown_method"),
    invalid_arg("invalid_arg"),
    not_enough_money("not_enough_money"),
    retry_later("retry_later"),
    api_call_limit_exceeded_for_api_key("api_call_limit_exceeded_for_api_key"),
    api_call_limit_exceeded_for_ip("api_call_limit_exceeded_for_ip");

    private String id;

    UniErrorsEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static UniErrorsEnum fromId(String id) {
        for (UniErrorsEnum at : UniErrorsEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}