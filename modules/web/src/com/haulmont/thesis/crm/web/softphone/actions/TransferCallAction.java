package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.thesis.crm.web.softphone.core.listener.CallTransferListener;
import com.haulmont.thesis.crm.web.ui.callactivity.CallStatusHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TransferCallAction extends AbstractCallControlAction {

    protected BeginTransferAction beginAction;
    protected BlindTransferAction blindAction;

    private HoldAction holdAction;

    public TransferCallAction(String callId, UUID softPhoneSessionId, CallStatusHolder target, Set<CallTransferListener> listeners) {
        this(callId, softPhoneSessionId, target, listeners, null);
    }

    public TransferCallAction(@Nonnull String callId, UUID softPhoneSessionId, CallStatusHolder target, Set<CallTransferListener> listeners, @Nullable String shortcut) {
        super("transfer", softPhoneSessionId, shortcut);
        icon = "components/spipanel/icons/transfer-call.png";

        beginAction = new BeginTransferAction(callId, softPhoneSessionId, target, listeners);
        blindAction = new BlindTransferAction(callId, softPhoneSessionId, listeners);
        holdAction = new HoldAction(callId, softPhoneSessionId, target);
    }

    @Override
    public void actionPerform(Component component) {
        holdAction.actionPerform(null);
        Map<String, Object> dialogParams = new HashMap<>();

        dialogParams.put("beginTransferAction", beginAction);
        dialogParams.put("blindTransferAction", blindAction);
        openWindow("callTransferDialog", WindowManager.OpenType.DIALOG, dialogParams);
    }
}
