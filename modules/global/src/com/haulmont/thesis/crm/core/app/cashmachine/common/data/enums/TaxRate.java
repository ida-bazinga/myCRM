/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.khoroshilov on 01.05.2017.
 */
// tax "NO_VAT" "VAT_10" "VAT_18" "VAT_0" "VAT_18_118" "VAT_10_110"
public enum TaxRate {

    @SerializedName("NO_VAT")
    NO_VAT,

    @SerializedName("VAT_10")
    VAT_10,

    @SerializedName("VAT_18")
    VAT_18,

    @SerializedName("VAT_0")
    VAT_0,

    @SerializedName("VAT_18_118")
    VAT_18_118,

    @SerializedName("VAT_10_110")
    VAT_10_110
}
