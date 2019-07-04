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

public class AbstractSoftphoneListener implements SoftPhoneListener {
    @Override
    public void onAction(ActionMessage action) {
    }

    @Override
    public void onCallback(CallbackMessage callback) {
    }

    @Override
    public void onHeartbeat(HeartbeatMessage heartbeat) {
    }

    @Override
    public void onEvent(EventMessage event) {
    }

    @Override
    public void onRinging(UUID sessionId, CallStatus callStatus) {
    }

    @Override
    public void onDialing(UUID sessionId, CallStatus callStatus) {
    }

    @Override
    public void onCallConnected(UUID sessionId, CallStatus callStatus) {
    }

    @Override
    public void onCallEnded(UUID sessionId, String callId) {
    }

    @Override
    public void TryingToTransfer(UUID sessionId, CallStatus callStatus) {
    }

    @Override
    public void onMute(UUID sessionId, String callId, Boolean isMuted) {
    }

    @Override
    public void onHold(UUID sessionId, String callId, Boolean isHold) {
    }

    @Override
    public void onProfileChange(UUID sessionId, SoftPhoneProfileName profile) {
    }

    @Override
    public void onCloseSoftphone() {
    }
}
