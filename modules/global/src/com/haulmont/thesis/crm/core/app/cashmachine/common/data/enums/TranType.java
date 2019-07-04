/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.khoroshilov on 09.05.2017.
 */
public enum TranType {

    @SerializedName("DOCUMENT_OPEN")
    DOCUMENT_OPEN,

    @SerializedName("REGISTER_POSITION")
    REGISTER_POSITION,

    @SerializedName("POSITION_TAX")
    POSITION_TAX,

    @SerializedName("PAYMENT")
    PAYMENT,

    @SerializedName("DOCUMENT_CLOSE_FPRINT")
    DOCUMENT_CLOSE_FPRINT,

    @SerializedName("DOCUMENT_CLOSE")
    DOCUMENT_CLOSE,

    @SerializedName("")
    EMPTY

}
