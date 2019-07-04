/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.web.softphone.actions.AbstractCallControlAction;
import com.haulmont.thesis.crm.web.softphone.actions.CallControlAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface CTIButtonsPanel extends Component, Component.BelongToFrame {
    String NAME = "ctiButtonsPanel";

    Collection<CallControlAction> getActions();

    @Nullable
    CallControlAction getAction(String id);

    @Nonnull
    CallControlAction getActionNN(String id);

    void init(Window window);

    void refreshComponent();

    void addSoftphoneAction(AbstractCallControlAction callAction, boolean clearActions, boolean refreshComponent);

    void clearActions();
}