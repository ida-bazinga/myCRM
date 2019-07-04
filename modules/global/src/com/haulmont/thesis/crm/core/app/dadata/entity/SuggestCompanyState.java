/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestCompanyState {


    //Дата актуальности сведений
    @SerializedName("actuality_date")
    private long state_actuality_date;

    //Дата регистрации
    @SerializedName("registration_date")
    private long state_registration_date;

    //Дата ликвидации
    @SerializedName("liquidation_date")
    private long state_liquidation_date;


    //Статус организации
    //ACTIVE — действующая;
    //LIQUIDATING — ликвидируется;
    //LIQUIDATED — ликвидирована.
    @SerializedName("status")
    private String state_status;



    public long getState_actuality_date() {
        return state_actuality_date;
    }

    public long getState_registration_date() {
        return state_registration_date;
    }

    public long getState_liquidation_date() { return state_liquidation_date; }

    public String getState_status() {
        return state_status;
    }


}
