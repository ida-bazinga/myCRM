/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.entity.IEncodable;
import org.atmosphere.config.managed.Encoder;

import java.io.IOException;

/**
 * Encode a {@link IEncodable} into a String
 */
public class MessageEncoder implements Encoder<IEncodable, String> {

    @Override
    public String encode(IEncodable m) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
