/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.mailigen;

import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description = "Сервис Mailigen")
public interface IMailigenMBean {
    String NAME = "IMailigen";
    boolean getIsStatus();
}
