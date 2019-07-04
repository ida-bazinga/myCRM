/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.thesis.crm.entity.Log1C;

/**
 * @author d.ivanov
 * (не используется в новой верии)
 */
@Deprecated
public interface IContractService {
    String NAME = "IContractService";

    boolean setContractOne(Log1C log);
}