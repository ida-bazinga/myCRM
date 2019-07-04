package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.web.softphone.core.entity.CallbackMessage;
import com.haulmont.thesis.crm.web.softphone.core.listener.CallTransferListener;
import com.haulmont.thesis.crm.web.ui.callactivity.CallStatusHolder;
import com.haulmont.thesis.crm.web.ui.cti.transfer.CallTransferDialog;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BeginTransferAction extends AbstractCallControlAction {

    protected String destination;
    protected CallStatusHolder target;
    protected Set<CallTransferListener> transferListeners = new HashSet<>();
    protected CallTransferDialog dialogWindow;

    public BeginTransferAction(String callId, UUID softPhoneSessionId, CallStatusHolder target, Set<CallTransferListener> listeners) {
        this(callId, softPhoneSessionId, target, listeners, null);
    }

    public BeginTransferAction(@Nonnull String callId, UUID softPhoneSessionId, CallStatusHolder target, Set<CallTransferListener> listeners, @Nullable String shortcut) {
        super("beginTransfer", softPhoneSessionId, shortcut);
        this.callId = callId;
        this.target = target;
        this.transferListeners.addAll(listeners);
    }

    public String getDestination() {
        return destination.trim();
    }

    public void setDestination(String destination) {
        this.destination = destination.trim();
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

        if (getDestination() == null || StringUtils.isBlank(getDestination())){
            String msg = String.format(messages.getMainMessage("phone.alert"), "null or empty" );
            showNotification(msg, IFrame.NotificationType.TRAY);
            return false;
        }
        actionMessageParams.put("destination", getDestination());

        return true;
    }

    @Override
    protected void preInvoke(){
        for (CallTransferListener listener : transferListeners){
            listener.setTryingToTransfer(true, getDestination());
        }

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
    public void onSuccess(Map<String,String> messageParams){
        showOptionDialog(
                messages.getMessage(getClass(),"confirmTransfer.title"),
                messages.getMessage(getClass(),"confirmTransfer.msg"),
                IFrame.MessageType.CONFIRMATION,
                new Action[]{
                        new CompleteTransferAction(callId, softphoneSessionId),
                        new CancelTransferAction(callId, softphoneSessionId)
                }
        );
    }

    @Override
    public void onError(){
    }

    @Override
    protected void afterInvoke(){
        getDialogWindow().close(Window.Editor.WINDOW_CLOSE);
    }
}
