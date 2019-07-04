/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Created by d.ivanov on 10.05.2017.
 */
@ManagedResource(description = "Проверка контрольной суммы ИНН контрагентов")
public interface InnCheckSumMBean {
    String NAME = "InnCheckSum";
    String innCheckSum();
}
