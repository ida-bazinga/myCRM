/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.thesis.crm.entity.Log1C;

/**
 * @author d.ivanov
 */
public interface IInvService {
    String NAME = "IInvService";

    boolean setInvOne(Log1C log);
}