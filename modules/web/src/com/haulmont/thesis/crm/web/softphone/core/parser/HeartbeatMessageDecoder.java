/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.web.softphone.core.entity.HeartbeatMessage;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

public class HeartbeatMessageDecoder implements Decoder<String, HeartbeatMessage> {

    @Override
    public HeartbeatMessage decode(String s) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(s, HeartbeatMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("HeartbeatMessageDecoder can not decode a message", e);
        }
    }
}

