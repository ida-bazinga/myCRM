/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class SendDTMFAction extends AbstractCallControlAction {

    protected String dtmfTone;

    public SendDTMFAction(String callId, String dtmfTone, UUID softPhoneSessionId) {
        this(callId, dtmfTone, softPhoneSessionId,null);
    }

    public SendDTMFAction(@Nonnull String callId, @Nonnull String dtmfTone, UUID softPhoneSessionId, @Nullable String shortcut) {
        super("sendDTMF", softPhoneSessionId, shortcut);
        this.callId = callId;
        this.dtmfTone = dtmfTone;
    }

    @Override
    protected boolean addActionMessageParams(){
        actionMessageParams.put("callId", callId);
        actionMessageParams.put("dtmf", dtmfTone);
        return true;
    }
}
