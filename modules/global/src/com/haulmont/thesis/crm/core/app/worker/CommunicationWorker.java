package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.thesis.crm.entity.CommKind;
import com.haulmont.thesis.crm.entity.Communication;

/**
 * @author Kirill Khoroshilov
 */

public interface CommunicationWorker {
    String NAME = "crm_CommunicationWorker";

    String getMaskedAddress(Communication entity);

    String getMaskedAddress(String mainPart, String additionalPart, String mask, CommKind commKind);
}

