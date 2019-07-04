/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class CancelTransferAction extends AbstractCallControlAction {

    //todo нужно снимать флаг перевода звонка перевода в окнах.

    public CancelTransferAction(String callId, UUID softPhoneSessionId) {
        this(callId, softPhoneSessionId, null);
    }

    public CancelTransferAction(@Nonnull String callId, UUID softPhoneSessionId, @Nullable String shortcut) {
        super("cancelTransfer", softPhoneSessionId, shortcut);
        this.callId = callId;
    }

    @Override
    protected boolean addActionMessageParams(){
        actionMessageParams.put("callId", callId);
        return true;
    }
}