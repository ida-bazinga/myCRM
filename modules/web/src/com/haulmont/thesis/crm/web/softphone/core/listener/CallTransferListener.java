package com.haulmont.thesis.crm.web.softphone.core.listener;

public interface CallTransferListener {

    void setTryingToTransfer(Boolean value, String destination);
}
