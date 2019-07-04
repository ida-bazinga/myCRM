/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.payOrd;

import org.springframework.jmx.export.annotation.*;

import java.util.UUID;


@ManagedResource(description = "Создать платежное поручение")
public interface ICreatePayOrdMBean {

        String NAME = "ICreatePayOrdMBean";
        boolean create(UUID invId);

}
