/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.actions;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneActionManager;
import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbstractCallControlAction extends AbstractAction implements CallControlAction, Action.UiPermissionAware {
    private boolean enabledByUiPermissions = true;
    private boolean visibleByUiPermissions = true;

    private boolean enabledExplicitly = true;
    private boolean visibleExplicitly = true;

    protected String callId;
    protected UUID softphoneSessionId;
    protected UUID actionUuid;
    protected Map<String, String> actionMessageParams;

    protected SoftPhoneActionManager softphoneActionManager;

    /**
     * Constructor that allows to specify the action name.
     * @param softphoneSessionId    Id softphone session
     * @param id        action's name
     */

    protected AbstractCallControlAction(String id, UUID softphoneSessionId) {
        this(id, softphoneSessionId,null);
    }

    protected AbstractCallControlAction(String id, UUID softphoneSessionId, @Nullable String shortcut) {
        super(id, shortcut);
        this.softphoneSessionId = softphoneSessionId;
        actionMessageParams = new HashMap<>();
        softphoneActionManager = AppBeans.get(SoftPhoneActionManager.class);
    }

    @Override
    public void actionPerform(Component component) {
        if (softphoneSessionId != null && addActionMessageParams()) {
            ActionMessage message = new ActionMessage(softphoneSessionId, id, actionMessageParams);
            actionUuid = message.getActionId();
            preInvoke();
            softphoneActionManager.invoke(message);
            afterInvoke();
        }
    }

    protected void preInvoke(){}

    protected void afterInvoke(){}

    protected boolean addActionMessageParams(){
        return true;
    }

    public void onSuccess(Map<String,String> messageParams){
    }

    public void onError(){
    }

    public UUID getActionUuid() {
        return actionUuid;
    }

    /**
     * Callback method which is invoked by the action to determine its enabled state.
     * @return true if the action is enabled for the current user
     */
    protected boolean isPermitted() {
        return true;
    }

    /**
     * Callback method which is invoked by the action to determine its enabled state.
     * @return true if the action is enabled for the current context, e.g. there is a selected row in a table
     */
    protected boolean isApplicable() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visibleExplicitly != visible) {
            this.visibleExplicitly = visible;

            refreshState();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabledExplicitly != enabled) {
            this.enabledExplicitly = enabled;

            refreshState();
        }
    }

    protected void setVisibleInternal(boolean visible) {
        super.setVisible(visible);
    }

    protected void setEnabledInternal(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void refreshState() {
        super.refreshState();

        setVisibleInternal(visibleExplicitly && visibleByUiPermissions);

        setEnabledInternal(enabledExplicitly && isPermitted() && isApplicable()
                && enabledByUiPermissions && visibleByUiPermissions);
    }

    @Override
    public boolean isEnabledByUiPermissions() {
        return enabledByUiPermissions;
    }

    @Override
    public void setEnabledByUiPermissions(boolean enabledByUiPermissions) {
        if (this.enabledByUiPermissions != enabledByUiPermissions) {
            this.enabledByUiPermissions = enabledByUiPermissions;

            refreshState();
        }
    }

    @Override
    public boolean isVisibleByUiPermissions() {
        return visibleByUiPermissions;
    }

    @Override
    public void setVisibleByUiPermissions(boolean visibleByUiPermissions) {
        if (this.visibleByUiPermissions != visibleByUiPermissions) {
            this.visibleByUiPermissions = visibleByUiPermissions;

            refreshState();
        }
    }

    protected void openWindow(String windowAlias, WindowManager.OpenType openType, Map<String, Object> params) {
        WindowInfo windowInfo = AppBeans.get(WindowConfig.class).getWindowInfo(windowAlias);
        App.getInstance().getWindowManager().openWindow(windowInfo, openType, params);
    }

    protected void showOptionDialog(String title, String message, IFrame.MessageType messageType, Action[] actions) {
        App.getInstance().getWindowManager().showOptionDialog(title, message, messageType, actions);
    }

    protected void showNotification(String caption, IFrame.NotificationType type) {
        App.getInstance().getWindowManager().showNotification(caption, type);
    }
}

