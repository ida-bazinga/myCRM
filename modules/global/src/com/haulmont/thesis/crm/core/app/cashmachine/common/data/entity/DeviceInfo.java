/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class DeviceInfo extends AbstractInfo {

    @SerializedName("code")
    protected String code;

    @SerializedName("name")
    protected String name;

    @SerializedName("id")
    protected String id;

    @SerializedName("storeUuid")
    protected UUID storeUuid;

    @SerializedName("timezoneOffset")
    protected String timezoneOffset;


    //region Getters

    public String getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public String getId() {
        return id;
    }

    public UUID getStoreUuid(){
        return storeUuid;
    }

    protected String getTimezoneOffset(){
        return timezoneOffset;
    }
}
