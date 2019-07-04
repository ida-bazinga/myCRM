package com.haulmont.thesis.crm.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Kirill Khoroshilov
 */
public enum ActivityResTypeEnum implements EnumClass<String> {

    SUCCESSFUL("successful"),
    UNSUCCESSFUL("unsuccessful"),
    SPECIAL("special"),
    FAIL("fail");

    private String id;

    ActivityResTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ActivityResTypeEnum fromId(String id) {
        for (ActivityResTypeEnum at : ActivityResTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}