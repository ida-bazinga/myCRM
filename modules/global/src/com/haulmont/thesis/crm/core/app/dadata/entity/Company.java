/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author leon0399
 */
public class Company extends AbstractEntity {
    //region Variables

    @SerializedName("result")
    private String result;

    @SerializedName("short")
    private String shortName;

    @SerializedName("full")
    private String fullName;

    @SerializedName("kpp")
    private String kpp;

    @SerializedName("ogrn")
    private String ogrn;

    @SerializedName("okpo")
    private String okpo;

    @SerializedName("unparsed_parts")
    private String unparsedParts;

    //endregion

    //region Getters

    /**
     * @return Исходная компания одной строкой
     */

    @Override
    public String getSource() {
        return super.getSource();
    }

    /**
     * @return Стандартизованный адрес одной строкой
     */
    public String getResult() {
        return result;
    }

    /**
     * @return Индекс
     */

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getKpp() {
        return kpp;
    }

    public String getOgrn() {
        return ogrn;
    }

    public String getOkpo() {
        return okpo;
    }


    public String getUnparsedParts() {
        return unparsedParts;
    }

    /**
     * Код проверки адреса
     *
     * @return
     * @see <a href="https://dadata.ru/api/clean/#qc>Код проверки адреса</a>
     */
}

