/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

/**
 * Decode a String into a {@link CallbackMessage}.
 */
public class CallbackMessageDecoder implements Decoder<String, CallbackMessage> {

    @Override
    public CallbackMessage decode(String s) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(s, CallbackMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("CallbackMessageDecoder can not decode a message", e);
        }
    }
}
