/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.entity;

import java.util.UUID;

public interface IActionMessage extends IMessage {

    UUID getActionId();
}
