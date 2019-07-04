/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class StoreInfo extends AbstractInfo {

    @SerializedName("code")
    protected String code;

    @SerializedName("name")
    protected String name;

    @SerializedName("address")
    protected String address;

    //region Getters

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getUuid().toString());
        if (StringUtils.isNotBlank(code)){
            result.append(" [");
            result.append(code);
            result.append("] ");
        } else {
            result.append(" ");
        }
        result.append(name);
        if (StringUtils.isNotBlank(address)){
            result.append(" (");
            result.append(address);
            result.append(") ");
        }
        return result.toString();
    }
}
