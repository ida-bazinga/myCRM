/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.khoroshilov on 09.05.2017.
 */

//"NORMAL" "ALCOHOL_MARKED" "ALCOHOL_NOT_MARKED"
public enum CommodityType {

    @SerializedName("NORMAL")
    NORMAL,

    @SerializedName("ALCOHOL_MARKED")
    ALCOHOL_MARKED,

    @SerializedName("ALCOHOL_NOT_MARKED")
    ALCOHOL_NOT_MARKED
}
