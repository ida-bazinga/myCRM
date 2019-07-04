/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import javax.annotation.Nullable;
import java.util.UUID;

public class LoginToQueuesAction extends AbstractCallControlAction {

    public LoginToQueuesAction(UUID softPhoneSessionId) {
        this(softPhoneSessionId,null);
    }

    public LoginToQueuesAction(UUID softPhoneSessionId, @Nullable String shortcut) {
        super("loginToQueues", softPhoneSessionId, shortcut);
    }

}
