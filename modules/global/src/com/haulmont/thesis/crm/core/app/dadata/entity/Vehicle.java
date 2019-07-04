/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author leon0399
 */
public class Vehicle extends AbstractEntity {

    @SerializedName("result")
    private String result;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    /**
     * @return Исходное значение
     */
    @Override
    public String getSource() {
        return super.getSource();
    }

    /**
     * @return Стандартизованное значение
     */
    public String getResult() {
        return result;
    }

    /**
     * @return Марка
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return Модель
     */
    public String getModel() {
        return model;
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
        return "Vehicle[source=" + getSource() +
                ", result=" + getResult() +
                ", brand=" + getBrand() +
                ", model=" + getModel() +
                ", qc=" + getQc() +
                "]";
    }
}
