/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.parsers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.entity.SoftPhoneProfile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SoftphoneProfileDecoder {
    protected static ObjectMapper mapper = new ObjectMapper();

    public static List<SoftPhoneProfile> decode(String s) {
        try {
            return mapper.readValue(s, new TypeReference<List<SoftPhoneProfile>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SoftPhoneProfile decode(Map<String, String> params) {
        return mapper.convertValue(params, SoftPhoneProfile.class);
    }
}
