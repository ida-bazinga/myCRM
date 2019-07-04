/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.phoneintegration.web;

import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.entity.SoftphoneProfileEnum;

/**
 * Created by k.khoroshilov on 05.06.2016.
 */

//CryptoProSignatureWindowListener
public interface CallIntegrationWindowListener {

    void wsConnected();
    void wsDisconnected();
    void incomingCall(CallStatus status);
    void outgoingCall(CallStatus status);
    void callEnded(CallStatus status);
    void callEstablished(CallStatus status);
    void waitingForNewParty(CallStatus status);
    void tryingToTransfer(CallStatus status);
    void setSetActiveProfile(SoftphoneProfileEnum profile);
    void error();
    void addEvent(String value, String description);
    void exitFromQueues();
}
