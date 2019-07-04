/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.khoroshilov on 09.05.2017.
 */
public enum PaymentType {

    @SerializedName("CASH")
    CASH,

    @SerializedName("CARD")
    CARD
}
