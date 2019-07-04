/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Created by d.ivanov on 24.03.2017.
 */

@ManagedResource(description = "Синхронизация номенклатуры с 1С_БП")
public interface INomenclatureGoTo1cMBean {
    String NAME = "INomenclatureGoTo1c";
    String goTo1CAllNomenclature();
}
