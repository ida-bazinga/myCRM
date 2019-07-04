/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app.service
 */
package com.haulmont.thesis.crm.core.app.service;

import com.haulmont.thesis.crm.core.app.worker.ContactPersonWorker;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.entity.ExtContactPerson;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Kirill Khoroshilov
 */
@Service(ContactPersonService.NAME)
public class ContactPersonServiceBean implements ContactPersonService {

    @Inject
    protected ContactPersonWorker contactWorker;

    @Override
    public List<Communication> getPrefCommunications(ExtContactPerson entity, CommunicationTypeEnum type){
        return contactWorker.getPrefCommunications(entity, type, 0);
    }

    @Override
    public List<Communication> findCommunication(CommunicationTypeEnum type, String address){
        return contactWorker.findCommunication(type, address);
    }
}