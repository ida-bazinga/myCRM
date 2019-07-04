package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.web.softphone.core.listener.CallTransferListener;
import com.haulmont.thesis.crm.web.ui.cti.transfer.CallTransferDialog;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BlindTransferAction extends AbstractCallControlAction {

    protected String destination;
    protected Set<CallTransferListener> transferListeners = new HashSet<>();
    protected CallTransferDialog dialogWindow;

    public BlindTransferAction(String callId, UUID softPhoneSessionId, Set<CallTransferListener> listeners) {
        this(callId, softPhoneSessionId, listeners,null);
    }

    public BlindTransferAction(@Nonnull String callId, UUID softPhoneSessionId, Set<CallTransferListener> listeners, @Nullable String shortcut) {
        super("blindTransfer", softPhoneSessionId, shortcut);
        this.callId = callId;
        this.transferListeners.addAll(listeners);
    }

    public String getDestination() {
        return destination.trim();
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public CallTransferDialog getDialogWindow() {
        return dialogWindow;
    }

    public void setDialogWindow(CallTransferDialog dialogWindow) {
        this.dialogWindow = dialogWindow;
    }

    @Override
    protected boolean addActionMessageParams(){
        actionMessageParams.put("callId", callId);

        super.addActionMessageParams();
        if (StringUtils.isBlank(getDestination())){
            String msg = String.format(messages.getMainMessage("phone.alert"), "null or empty" );
            showNotification(msg, IFrame.NotificationType.TRAY);
            return false;
        }
        actionMessageParams.put("destination", getDestination());

        return true;
    }

    @Override
    protected void preInvoke() {
        for (CallTransferListener listener : transferListeners) {
            listener.setTryingToTransfer(false, getDestination());
        }
    }

    @Override
    protected void afterInvoke(){
        getDialogWindow().close(Window.Editor.WINDOW_CLOSE);
    }
}
