/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestCompanyManagement {


    //ФИО руководителя
    @SerializedName("name")
    private String management_name;

    //Должность руководителя
    @SerializedName("post")
    private String management_post;


    public String getManagement_name() {
        return management_name;
    }

    public String getManagement_post () {
        return management_post;
    }

}
