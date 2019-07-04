/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */

public class SuggestBank {

    //Наименование одной строкой (как показывается в списке подсказок)
    @SerializedName("value")
    private String value;

    //Наименование одной строкой (полное)
    @SerializedName("unrestricted_value")
    private String unrestricted_value;

    @SerializedName("data")
    private SuggestBankData suggestBankData;



    public String getBank_value() {
        return value;
    }

    public String getBank_unrestricted_value() {
        return unrestricted_value;
    }

    public SuggestBankData getSuggestBankData()
    {
        return suggestBankData;
    }

}
