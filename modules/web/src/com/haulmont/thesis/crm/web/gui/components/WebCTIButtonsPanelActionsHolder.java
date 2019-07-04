/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.gui.components;

import com.haulmont.thesis.crm.web.softphone.actions.CallControlAction;
import org.apache.commons.lang.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WebCTIButtonsPanelActionsHolder {
    protected List<CallControlAction> actionList = new LinkedList<>();

    public void addAction(CallControlAction action) {
        int index = findActionById(action.getId());
        if (index < 0) {
            index = actionList.size();
        }

        addAction(action, index);
    }

    public void addAction(CallControlAction action, int index) {
        int oldIndex = findActionById(action.getId());
        if (oldIndex >= 0) {
            removeAction(actionList.get(oldIndex));
            if (index > oldIndex) {
                index--;
            }
        }

        actionList.add(index, action);
        action.refreshState();
    }

    public void removeAction(CallControlAction action) {
        actionList.remove(action);
    }

    public void removeAction(String id) {
        CallControlAction action = getAction(id);
        if (action != null) {
            removeAction(action);
        }
    }

    public void removeAllActions() {
        actionList.clear();
    }

    public Collection<CallControlAction> getActions() {
        return Collections.unmodifiableCollection(actionList);
    }

    public CallControlAction getAction(String id) {
        for (CallControlAction action : getActions()) {
            if (ObjectUtils.equals(action.getId(), id)) {
                return action;
            }
        }
        return null;
    }

    protected int findActionById(String actionId) {
        int oldIndex = -1;
        for (int i = 0; i < actionList.size(); i++) {
            CallControlAction a = actionList.get(i);
            if (ObjectUtils.equals(a.getId(), actionId)) {
                oldIndex = i;
                break;
            }
        }
        return oldIndex;
    }
}
