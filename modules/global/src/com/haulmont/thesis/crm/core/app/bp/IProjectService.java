/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.thesis.crm.entity.Log1C;


/**
 * @author d.ivanov
 */
public interface IProjectService {
    String NAME = "IProjectService";

    boolean setProjectOne(Log1C log);
}