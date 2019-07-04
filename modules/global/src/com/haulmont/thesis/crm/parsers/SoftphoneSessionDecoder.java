/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;

import java.util.Map;

public class SoftphoneSessionDecoder {

    public static SoftPhoneSession decode(Map<String, String> params) {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(params, SoftPhoneSession.class);
    }
}
