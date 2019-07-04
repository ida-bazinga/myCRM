/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.web.softphone.core.entity.EventMessage;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

public class EventMessageDecoder implements Decoder<String, EventMessage> {

    @Override
    public EventMessage decode(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(json, EventMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("EventMessageDecoder can not decode a message", e);
        }
    }
}
