/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.khoroshilov on 01.05.2017.
 */
// measureName "" "шт" "кг" "л" "м" "км" "м2" "м3" "компл" "упак" "ед" "дроб"
public enum MeasureUnit {

    @SerializedName("шт")
    pc("796"),

    @SerializedName("кг")
    kg("166"),

    @SerializedName("л")
    l("112"),

    @SerializedName("м")
    m("006"),

    @SerializedName("км")
    km("008"),

    @SerializedName("м2")
    m2("055"),

    @SerializedName("м3")
    m3("113"),

    @SerializedName("компл")
    kit("839"),

    @SerializedName("упак")
    pkg("778"),

    @SerializedName("ед")
    unit("851"),

    @SerializedName("дроб")
    fraction("879"),

    @SerializedName("")
    EMPTY("000");

    private final String value;

    private MeasureUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
