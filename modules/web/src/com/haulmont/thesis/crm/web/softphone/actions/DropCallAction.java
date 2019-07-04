/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class DropCallAction extends AbstractCallControlAction {

    public DropCallAction(String callId, UUID softPhoneSessionId) {
        this(callId, softPhoneSessionId,null);
    }

    public DropCallAction(@Nonnull String callId, UUID softPhoneSessionId, @Nullable String shortcut) {
        super("dropCall", softPhoneSessionId, shortcut);
        this.callId = callId;
        icon = "components/spipanel/icons/drop_call.png";
    }

    @Override
    protected boolean addActionMessageParams(){
        actionMessageParams.put("callId", callId);
        return true;
    }
}
