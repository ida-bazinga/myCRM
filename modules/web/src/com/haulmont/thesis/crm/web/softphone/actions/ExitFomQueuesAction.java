/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import javax.annotation.Nullable;
import java.util.UUID;

public class ExitFomQueuesAction extends AbstractCallControlAction {

    public ExitFomQueuesAction(UUID softPhoneSessionId) {
        this(softPhoneSessionId,null);
    }

    public ExitFomQueuesAction(UUID softPhoneSessionId, @Nullable String shortcut) {
        super("exitFomQueues", softPhoneSessionId, shortcut);
    }

}
