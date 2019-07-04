/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.enums
 */
package com.haulmont.thesis.crm.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kirill Khoroshilov
 */
public enum SoftPhoneProfileName implements EnumClass<String> {
    AVAILABLE("available"),
    AWAY("away"),
    DND("out of office"),
    CUSTOM_1("custom 1"),
    CUSTOM_2("custom 2");
    //CUSTOM_HOURS("customHours"),
    //EXCEPTIONS("exceptions");

    private String id;

    SoftPhoneProfileName(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @JsonCreator
    @Nullable
    public static SoftPhoneProfileName fromId(String id) {
        for (SoftPhoneProfileName at : SoftPhoneProfileName.values()) {
            if (at.getId().equalsIgnoreCase(id)) {
                return at;
            }
        }
        return null;
    }

    public static List<SoftPhoneProfileName> getOptions(){
        return getOptions(new ArrayList<SoftPhoneProfileName>());
    }

    public static List<SoftPhoneProfileName> getOptions(List<SoftPhoneProfileName> exclude){
        List<SoftPhoneProfileName> result = new LinkedList<>();

        for (SoftPhoneProfileName val : SoftPhoneProfileName.values()) {
            if (!exclude.contains(val)) result.add(val);
        }
        return result;
    }
}