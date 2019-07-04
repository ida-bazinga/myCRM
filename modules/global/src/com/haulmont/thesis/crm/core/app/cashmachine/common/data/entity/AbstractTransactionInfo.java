/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.TranType;

import java.util.UUID;

/**
 * Created by k.khoroshilov on 30.04.2017.
 */
public abstract class AbstractTransactionInfo extends AbstractInfo{

    @SerializedName("type")
    protected TranType type;

    @SerializedName("id")
    protected String id;

    @SerializedName("userCode")
    protected String userCode;

    @SerializedName("userUuid")
    protected String userUuid;

    @SerializedName("creationDate")
    protected String creationDate;

    @SerializedName("timezone")
    protected double timezone;

    //region Getters

    public TranType getType(){
        return type;
    }

    public String getId(){
        return id;
    }

    public String getUserCode() {
        return userCode;
    }

    public UUID getUserUuid() {
        return UUID.fromString(userUuid);
    }

    public double getTimezone() {
        return timezone;
    }

}
