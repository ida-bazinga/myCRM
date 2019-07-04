/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

public class SetActiveProfileAction extends AbstractCallControlAction {

    protected String newProfileName;

    public SetActiveProfileAction(UUID softPhoneSessionId) {
        this(softPhoneSessionId,null);
    }

    public SetActiveProfileAction(UUID softPhoneSessionId, @Nullable String shortcut) {
        super("setActiveProfile", softPhoneSessionId, shortcut);
    }

    public String getNewProfileName() {
        return newProfileName;
    }

    public void setNewProfileName(String newProfileName) {
        this.newProfileName = newProfileName;
    }

    @Override
    protected boolean addActionMessageParams() {
        if (StringUtils.isBlank(getNewProfileName()))
            return false;

        actionMessageParams.put("profileName", getNewProfileName());

        return true;
    }
}

