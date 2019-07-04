/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;
import com.haulmont.thesis.crm.web.ui.callactivity.CallStatusHolder;
import org.apache.commons.lang.BooleanUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class MuteAction extends AbstractCallControlAction {

    protected CallStatusHolder target;

    public MuteAction(String callId, UUID softPhoneSessionId, CallStatusHolder target) {
        this(callId, softPhoneSessionId, target, null);
    }

    public MuteAction(@Nonnull String callId, UUID softPhoneSessionId, CallStatusHolder target, @Nullable String shortcut) {
        super("mute", softPhoneSessionId, shortcut);
        this.callId = callId;
        this.target = target;
        icon = SoftphoneConstants.MUTE_ICON;
    }

    @Override
    protected boolean addActionMessageParams(){
        actionMessageParams.put("callId", callId);
        return true;
    }

    @Override
    protected void preInvoke(){
        target.addCallbackListener(actionUuid, new CallbackListener() {
            @Override
            public void invoke(CallbackMessage message){
                if (message.isSuccess())
                    onSuccess(message.getMessageParams());

                if (message.isError())
                    onError();
            }
        });
    }

    @Override
    public void onSuccess(Map<String, String> messageParams){
        boolean mutedOn = BooleanUtils.toBoolean(messageParams.get(SoftphoneConstants.MUTE_ON_PARAM));
        String muteIcon = SoftphoneConstants.MUTE_ICON;
        String unMuteIcon = SoftphoneConstants.UNMUTE_ICON;
        this.setIcon(mutedOn ? muteIcon : unMuteIcon);
    }
}
