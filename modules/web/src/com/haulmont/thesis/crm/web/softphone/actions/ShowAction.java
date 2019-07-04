/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.gui.components.Component;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import java.util.UUID;

public class ShowAction extends AbstractCallControlAction {

    public ShowAction(UUID softPhoneSessionId) {
        this(softPhoneSessionId, null);
    }

    public ShowAction(UUID softPhoneSessionId, @Nullable String shortcut) {
        super("show", softPhoneSessionId, shortcut);
    }

    @Override
    public void actionPerform(Component component) {
        throw new NotImplementedException();
    }
}
