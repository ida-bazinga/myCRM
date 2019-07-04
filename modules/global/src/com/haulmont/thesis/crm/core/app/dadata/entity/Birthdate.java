/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author leon0399
 */
public class Birthdate extends AbstractEntity {

    @SerializedName("birthdate")
    private String birthdate;

    /**
     * @return Исходная дата
     */
    @Override
    public String getSource() {
        return super.getSource();
    }

    /**
     * @return Стандартизованная дата
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * @return Код проверки
     */
    @Override
    public int getQc() {
        return super.getQc();
    }

    @Override
    public String toString() {
        return "Birthdate[source=" + getSource() +
                ", birthdate=" + getBirthdate() +
                ", qc=" + getQc() +
                "]";
    }
}
