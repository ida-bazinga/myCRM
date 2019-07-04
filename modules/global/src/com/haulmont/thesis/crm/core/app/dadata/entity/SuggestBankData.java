/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestBankData {


    @SerializedName("bic")
    private String bik;

    @SerializedName("swift")
    private String swift;

    @SerializedName("correspondent_account")
    private String correspondent_account;

    @SerializedName("address")
    private SuggestAddres suggestAddres;

    @SerializedName("name")
    private SuggestCompanyName companyName;


    public String getBik () {
        return bik;
    }

    public String getSwift() {
        return swift;
    }

    public String getCorrespondent_account() {
        return correspondent_account;
    }

    public SuggestAddres getSuggestAddres()
    {
        return suggestAddres;
    }

    public SuggestCompanyName getCompanyName() {return companyName;}









}
