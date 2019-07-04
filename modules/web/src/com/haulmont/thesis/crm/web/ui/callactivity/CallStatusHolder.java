package com.haulmont.thesis.crm.web.ui.callactivity;

import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.web.softphone.actions.CallbackListener;

import javax.annotation.Nullable;
import java.util.UUID;

public interface CallStatusHolder extends Window {

    void setCallStatus(CallStatus callStatus);
    @Nullable
    CallStatus getCallStatus();

    void addCallbackListener(UUID actionId, CallbackListener listener);
    void removeCallbackListener(UUID actionId);
}
