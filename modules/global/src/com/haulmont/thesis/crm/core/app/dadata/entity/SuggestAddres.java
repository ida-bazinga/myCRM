/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestAddres {

    //Адрес одной строкой:адрес организации для юридических лиц; город проживания для индивидуальных предпринимателей.
    //Стандартизован, поэтому может отличаться от записанного в ЕГРЮЛ.
    @SerializedName("value")
    private String address_value;

    //Адрес одной строкой (полный, от региона)
    @SerializedName("unrestricted_value")
    private String unrestricted_value;

    //Гранулярный адрес. Может отсутствовать
    @SerializedName("data")
    private Address address_data;


    public String getAddress_value() {
        return address_value;
    }

    public String getAddress_unrestricted_value() {
        return unrestricted_value;
    }

    public Address getAddress_data() {
        return address_data;
    }

}

