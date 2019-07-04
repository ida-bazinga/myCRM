/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;
import org.atmosphere.config.managed.Decoder;

import java.io.IOException;

/**
 * Decode a String into a {@link ActionMessage}.
 */
public class ActionMessageDecoder implements Decoder<String, ActionMessage> {

    @Override
    public ActionMessage decode(String s) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(s, ActionMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("ActionMessageDecoder can not decode a message", e);
        }
    }
}
