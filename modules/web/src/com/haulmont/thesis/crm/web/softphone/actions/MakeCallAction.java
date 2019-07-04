/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

public class MakeCallAction extends AbstractCallControlAction {

    protected String destination;
    protected Component.HasValue target;

    public MakeCallAction(UUID softPhoneSessionId, Component.HasValue target) {
        this(softPhoneSessionId, target, null);
    }

    public MakeCallAction(UUID softPhoneSessionId, Component.HasValue target, @Nullable String shortcut) {
        this(softPhoneSessionId, String.valueOf(target.getValue()), shortcut);
        this.target = target;
    }

    public MakeCallAction(UUID softPhoneSessionId, String destination) {
        this(softPhoneSessionId, destination, null);
    }

    public MakeCallAction(UUID softPhoneSessionId, String destination, @Nullable String shortcut) {
        super("makeCall", softPhoneSessionId, shortcut);
        this.destination = destination;
        icon = "components/spipanel/icons/make_call.png";
    }

    public void setDestination(String destination) {
        this.destination = destination.trim();
    }

    public String getDestination() {
        return destination;
    }

    @Override
    protected boolean addActionMessageParams(){
        if (StringUtils.isBlank(getDestination())){
            String msg = String.format(messages.getMainMessage("phone.alert"), "null or empty" );
            showNotification(msg, IFrame.NotificationType.TRAY);
            return false;
        }

        actionMessageParams.put("destination", getDestination());

        return true;
    }

    @Override
    protected boolean isApplicable() {
        return target != null && target.getValue() != null;
    }
}