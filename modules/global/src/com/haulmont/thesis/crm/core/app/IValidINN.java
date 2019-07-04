/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

/**
 * Created by d.ivanov on 29.03.2017.
 */
public interface IValidINN {
    String NAME = "IValidINN";
    Boolean isValidINN (String inn);
}
