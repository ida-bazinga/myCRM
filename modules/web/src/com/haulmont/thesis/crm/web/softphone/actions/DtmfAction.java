/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DtmfAction extends AbstractCallControlAction {

    public DtmfAction(String callId, UUID softPhoneSessionId) {
        this(callId, softPhoneSessionId, null);
    }

    public DtmfAction(@Nonnull String callId, UUID softPhoneSessionId, @Nullable String shortcut) {
        super("dtmf", softPhoneSessionId, shortcut);
        this.callId = callId;
        icon = "components/spipanel/icons/dtmf.png";
    }

    @Override
    public void actionPerform(Component component) {
        Map<String, Object> dialogParams = new HashMap<>();
        dialogParams.put("callId", callId);
        dialogParams.put("softPhoneSessionId", softphoneSessionId);

        openWindow("dtmfDialog", WindowManager.OpenType.DIALOG, dialogParams);
    }
}
