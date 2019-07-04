/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public abstract class AbstractInfo {

    @SerializedName("uuid")
    protected UUID uuid;

    //region Getters

    public UUID getUuid() {
        return uuid;
    }

}
