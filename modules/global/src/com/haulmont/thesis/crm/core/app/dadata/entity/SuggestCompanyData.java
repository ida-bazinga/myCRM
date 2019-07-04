/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestCompanyData {


    //Количество филиалов
    @SerializedName("branch_type")
    private String branch_type;

    //Тип подразделения
    @SerializedName("branch_count")
    private String branch_count;

    //ИНН
    @SerializedName("inn")
    private String inn;

    //КПП
    @SerializedName("kpp")
    private String kpp;

    //ОГРН
    @SerializedName("ogrn")
    private String ogrn;

    //Код ОКПО (не заполняется)
    @SerializedName("okpo")
    private String okpo;

    //Код ОКВЭД
    @SerializedName("okved")
    private String okved;

    //Тип организации
    //LEGAL — юридическое лицо;
    //INDIVIDUAL — индивидуальный предприниматель.
    @SerializedName("type")
    private String type;

    //Адрес
    @SerializedName("address")
    private SuggestAddres suggestAddres;

    //Руководители
    @SerializedName("management")
    private SuggestCompanyManagement suggestCompanyManagement;

    //Наименование
    @SerializedName("name")
    private SuggestCompanyName suggestCompanyName;

    //ОКОПФ
    @SerializedName("opf")
    private SuggestCompanyOpf suggestCompanyOpf;

    //Статус
    @SerializedName("state")
    private SuggestCompanyState suggestCompanyState;

    //Версия справочника ОКВЭД (2001 или 2014)
    @SerializedName("okved_type")
    private String okved_type;



    public SuggestAddres getSuggestAddres()
    {
        return suggestAddres;
    }

    public SuggestCompanyManagement getSuggestCompanyManagement()
    {
        return suggestCompanyManagement;
    }

    public SuggestCompanyName getSuggestCompanyName()
    {
        return suggestCompanyName;
    }

    public SuggestCompanyOpf getSuggestCompanyOpf()
    {
        return suggestCompanyOpf;
    }

    public SuggestCompanyState getSuggestCompanyState()
    {
        return suggestCompanyState;
    }

    public String getBranch_type () {
        return branch_type;
    }

    public String getBranch_count() {
        return branch_count;
    }

    public String getInn() {
        return inn;
    }

    public String getKpp(){
        return kpp;
    }

    public String getOgrn(){
        return ogrn;
    }

    public String getOkpo() {
        return okpo;
    }

    public String getOkved() {
        return okved;
    }

    public String getType() {
        return type;
    }

    public String getOkvedType() {
        return okved_type;
    }

}
