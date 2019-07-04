/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class ActivateAction extends AbstractCallControlAction {

    public ActivateAction(String callId, UUID softPhoneSessionId) {
        this(callId, softPhoneSessionId, null);
    }

    public ActivateAction(@Nonnull String callId, UUID softPhoneSessionId, @Nullable String shortcut) {
        super("activate", softPhoneSessionId, shortcut);
        this.callId = callId;
        icon = "components/spipanel/icons/ringing.png";
    }

    @Override
    protected boolean addActionMessageParams(){
        actionMessageParams.put("callId", callId);
        return true;
    }

}

