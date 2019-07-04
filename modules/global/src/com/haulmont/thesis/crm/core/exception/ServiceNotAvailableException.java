/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.exception;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;

import javax.annotation.Nonnull;


/**
 * Created by k.khoroshilov on 03.06.2017.
 */
public class ServiceNotAvailableException extends Exception {

    public ServiceNotAvailableException(@Nonnull String serviceName) {
        super(AppBeans.get(Messages.class).formatMessage(
                "com.haulmont.thesis.crm.core.exception",
                "serviceIsNotAvailable",
                serviceName
        ));
    }
}
