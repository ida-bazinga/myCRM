/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.thesis.crm.entity.IntegrationResolver;

/**
 * Created by d.ivanov on 19.05.2016.
 */
public interface IStatusService {
    String NAME = "IStatusService";

    boolean getStatusAll();
    boolean getStatusOne(IntegrationResolver integrationResolver);
}
