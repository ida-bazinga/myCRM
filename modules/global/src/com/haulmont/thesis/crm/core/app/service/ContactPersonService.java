/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app.service
 */
package com.haulmont.thesis.crm.core.app.service;

import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.entity.ExtContactPerson;

import java.util.List;

/**
 * @author Kirill Khoroshilov
 */
public interface ContactPersonService {
    String NAME = "crm_ContactPersonService";

    List<Communication> getPrefCommunications(ExtContactPerson entity, CommunicationTypeEnum type);

    List<Communication> findCommunication(CommunicationTypeEnum type, String address);
}