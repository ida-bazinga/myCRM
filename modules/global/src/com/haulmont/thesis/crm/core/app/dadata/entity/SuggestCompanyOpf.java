/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestCompanyOpf {

    //Код ОКОПФ
    @SerializedName("code")
    private String opf_code;

    //Полное название ОПФ
    @SerializedName("full")
    private String opf_full;

    //Краткое название ОПФ
    @SerializedName("short")
    private String opf_short;



    public String getOpf_code() {
        return opf_code;
    }

    public String getOpf_full() {
        return opf_full;
    }

    public String getOpf_short() {
        return opf_short;
    }

}
