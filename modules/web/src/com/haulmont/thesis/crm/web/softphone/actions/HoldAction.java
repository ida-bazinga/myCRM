package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;
import com.haulmont.thesis.crm.web.ui.callactivity.CallStatusHolder;
import org.apache.commons.lang.BooleanUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class HoldAction extends AbstractCallControlAction {

    protected CallStatusHolder target;

    public HoldAction(String callId, UUID softPhoneSessionId, CallStatusHolder target) {
        this(callId,  softPhoneSessionId, target,null);
    }

    public HoldAction(@Nonnull String callId, UUID softPhoneSessionId, CallStatusHolder target, @Nullable String shortcut) {
        super("hold", softPhoneSessionId, shortcut);
        this.callId = callId;
        this.target = target;
        this.icon = SoftphoneConstants.HOLD_ICON;
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
        boolean holdOn = BooleanUtils.toBoolean(messageParams.get(SoftphoneConstants.HOLD_ON_PARAM));
        String holdIcon = SoftphoneConstants.HOLD_ICON;
        String unHoldIcon = SoftphoneConstants.UNHOLD_ICON;
        this.setIcon(holdOn ? holdIcon : unHoldIcon);
    }
}