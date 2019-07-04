/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.listener;

import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;
import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;
import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;
import com.haulmont.thesis.crm.web.softphone.core.entity.EventMessage;
import com.haulmont.thesis.crm.web.softphone.core.entity.HeartbeatMessage;

import java.util.UUID;

public interface SoftPhoneListener {
    void onAction(ActionMessage action);
    void onCallback(CallbackMessage callback);
    void onHeartbeat(HeartbeatMessage heartbeat);

    void onEvent(EventMessage event);
    void onRinging(UUID sessionId, CallStatus callStatus);
    void onDialing(UUID sessionId, CallStatus callStatus);
    void onCallConnected(UUID sessionId, CallStatus callStatus);
    void onCallEnded(UUID sessionId, String callId);
    void TryingToTransfer(UUID sessionId, CallStatus callStatus);
    void onMute(UUID sessionId, String callId, Boolean isMuted);
    void onHold(UUID sessionId, String callId, Boolean isHold);
    void onProfileChange(UUID sessionId, SoftPhoneProfileName profile);

    void onCloseSoftphone();
}
