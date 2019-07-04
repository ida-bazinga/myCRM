/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;

public interface CallbackListener {
    void invoke(CallbackMessage message);
}