/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestCompanyName {


    //Полное наименование с ОПФ
    @SerializedName("full_with_opf")
    private String name_full_with_opf;

    //Краткое наименование с ОПФ
    @SerializedName("short_with_opf")
    private String name_short_with_opf;

    //Наименование на латинице
    @SerializedName("latin")
    private String name_latin;

    //Полное наименование
    @SerializedName("full")
    private String name_full;

    //Краткое наименование
    @SerializedName("short")
    private String name_short;



    public String getName_full_with_opf() {
        return name_full_with_opf;
    }

    public String getName_short_with_opf()
    {
        return name_short_with_opf;
    }

    public String getName_latin(){
        return name_latin;
    }

    public String getName_full(){
        return name_full;
    }

    public String getName_short(){
        return name_short;
    }

}
