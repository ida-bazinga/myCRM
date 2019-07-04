/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.catalog;

import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description = "Отправка конфигурации помещения из лога")
public interface ICatalogService {
    String NAME = "ICatalogService";
    String initCatalogService();
}
