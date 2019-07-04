/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.khoroshilov on 09.05.2017.
 */

/*
кассовые документы - SELL,PAYBACK,CASH_INCOME,CASH_OUTCOME,OPEN_SESSION,FPRINT,CLOSE_SESSION.
инвентаризация - INVENTORY;
приемка - ACCEPT;
возврат поставщику - RETURN;
списание - WRITE_OFF;
акт переоценки - REVALUATION;
вскрытие тары - OPEN_TARE.
*/
public enum DocType {

    @SerializedName("SELL")
    SELL,

    @SerializedName("PAYBACK")
    PAYBACK,

    @SerializedName("CASH_INCOME")
    CASH_INCOME,

    @SerializedName("CASH_OUTCOME")
    CASH_OUTCOME,

    @SerializedName("OPEN_SESSION")
    OPEN_SESSION,

    @SerializedName("FPRINT")
    FPRINT,

    @SerializedName("CLOSE_SESSION")
    CLOSE_SESSION,

    @SerializedName("INVENTORY")
    INVENTORY,

    @SerializedName("ACCEPT")
    ACCEPT,

    @SerializedName("RETURN")
    RETURN,

    @SerializedName("WRITE_OFF")
    WRITE_OFF,

    @SerializedName("REVALUATION")
    REVALUATION,

    @SerializedName("OPEN_TARE")
    OPEN_TARE
}
