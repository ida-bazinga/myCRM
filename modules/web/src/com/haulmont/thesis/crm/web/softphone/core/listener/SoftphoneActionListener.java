/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.listener;

import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;

public interface SoftphoneActionListener {
    void invokeAction(ActionMessage message);
}
