/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.thesis.crm.entity.CallStatus;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

// TODO: 05.11.2017 Move to package com.haulmont.thesis.crm.core.parsers;

public class CallStatusDecoder {
    private final static Log log = LogFactory.getLog(CallStatusDecoder.class);

    public static CallStatus decode(Map<String, String> params) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.convertValue(params, CallStatus.class);
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
