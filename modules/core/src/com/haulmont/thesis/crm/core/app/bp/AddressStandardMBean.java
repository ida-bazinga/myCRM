/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Created by d.ivanov on 24.03.2017.
 */
@ManagedResource(description = "Стандартизация адреса")
public interface AddressStandardMBean {
    String NAME = "AddressStandard";
    String addressStandard();
}
