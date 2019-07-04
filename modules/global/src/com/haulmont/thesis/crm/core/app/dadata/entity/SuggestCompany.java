/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by d.ivanov on 10.01.2017.
 */
public class SuggestCompany {

    //Наименование компании одной строкой (как показывается в списке подсказок)
    @SerializedName("value")
    private String value;

    //Наименование компании одной строкой (полное)
    @SerializedName("unrestricted_value")
    private String unrestricted_value;

    @SerializedName("data")
    private SuggestCompanyData suggestCompanyData;



    public String getValue() {
        return value;
    }

    public String getUnrestricted_value() {
        return unrestricted_value;
    }

    public SuggestCompanyData getSuggestCompanyData()
    {
        return suggestCompanyData;
    }

    /*
    //Адрес одной строкой: адрес организации для юридических лиц; адрес налоговой для индивидуальных предпринимателей.
    @SerializedName("data.address.value")
    private String address_value;

    //Гранулярный адрес. Может отсутствовать
    @SerializedName("data.address.data")
    private String address_data;

    //Количество филиалов
    @SerializedName("data.branch_type")
    private String branch_type;

    //Тип подразделения
    @SerializedName("data.branch_count")
    private String branch_count;

    //ИНН
    @SerializedName("data.inn")
    private String inn;

    //КПП
    @SerializedName("data.kpp")
    private String kpp;

    //ФИО руководителя
    @SerializedName("data.management.name")
    private String management_name;

    //Должность руководителя
    @SerializedName("data.management.post")
    private String management_post;

    //Полное наименование с ОПФ
    @SerializedName("data.name.full_with_opf")
    private String name_full_with_opf;

    //Краткое наименование с ОПФ
    @SerializedName("data.name.short_with_opf")
    private String name_short_with_opf;

    //Наименование на латинице
    @SerializedName("data.name.latin")
    private String name_latin;

    //Полное наименование
    @SerializedName("data.name.full")
    private String name_full;

    //Краткое наименование
    @SerializedName("data.name.short")
    private String name_short;

    //ОГРН
    @SerializedName("data.ogrn")
    private String ogrn;

    //Код ОКПО (не заполняется)
    @SerializedName("data.okpo")
    private String okpo;

    //Код ОКВЭД
    @SerializedName("data.okved")
    private String okved;

    //Код ОКОПФ
    @SerializedName("data.opf.code")
    private String opf_code;

    //Полное название ОПФ
    @SerializedName("data.opf.full")
    private String opf_full;

    //Краткое название ОПФ
    @SerializedName("data.opf.short")
    private String opf_short;

    //Дата актуальности сведений
    @SerializedName("data.state.actuality_date")
    private String state_actuality_date;

    //Дата регистрации
    @SerializedName("data.state.registration_date")
    private String state_registration_date;

    //Дата ликвидации
    @SerializedName("data.state.liquidation_date")
    private String state_liquidation_date;

    //Статус организации
    //ACTIVE — действующая;
    //LIQUIDATING — ликвидируется;
    //LIQUIDATED — ликвидирована.
    @SerializedName("data.state.status")
    private String state_status;

    //Тип организации
    //LEGAL — юридическое лицо;
    //INDIVIDUAL — индивидуальный предприниматель.
    @SerializedName("data.type")
    private String type;



    public String getAddress_value() {
        return address_value;
    }

    public String getAddress_data() {
        return address_data;
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

    public String getManagement_name() {
        return management_name;
    }

    public String getManagement_post () {
        return management_post;
    }

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

    public String getOgrn(){
        return ogrn;
    }

    public String getOkpo() {
        return okpo;
    }

    public String getOkved() {
        return okved;
    }

    public String getOpf_code() {
        return opf_code;
    }

    public String getOpf_full() {
        return opf_full;
    }

    public String getOpf_short() {
        return opf_short;
    }

    public String getState_actuality_date() {
        return state_actuality_date;
    }

    public String getState_registration_date() {
        return state_registration_date;
    }

    public String getState_liquidation_date() {
        return state_liquidation_date;
    }

    public String getState_status() {
        return state_status;
    }

    public String getType() {
        return type;
    }

   */


}
