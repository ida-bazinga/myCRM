/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.entity;

import com.haulmont.thesis.crm.entity.IEncodable;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public interface IMessage extends IEncodable, Serializable {

    UUID getSessionId();
    void setSessionId(UUID sessionId);

    String getName();

    Map<String,String> getMessageParams();
    void setMessageParams(Map<String,String> messageParams);
}