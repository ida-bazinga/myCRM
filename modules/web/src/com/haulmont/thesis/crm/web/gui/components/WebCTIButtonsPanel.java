/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.gui.components;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.thesis.crm.gui.components.CTIButtonsPanel;
import com.haulmont.thesis.crm.web.softphone.actions.AbstractCallControlAction;
import com.haulmont.thesis.crm.web.softphone.actions.CallControlAction;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.vaadin.ui.HorizontalLayout;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class WebCTIButtonsPanel extends WebAbstractComponent<HorizontalLayout> implements CTIButtonsPanel {

    protected WebCTIButtonsPanelActionsHolder actionsHolder;

    public WebCTIButtonsPanel() {
        component = new HorizontalLayout();
    }

    // TODO: 29.09.2017 phoneNumberField add validation ('+', '(', ')', '-' and numeric & ??? 'P<dtmf>'(n))
    @Override
    public void init(Window window) {
        actionsHolder = new WebCTIButtonsPanelActionsHolder();
        setFrame(window);

        refreshComponent();
    }

    @Override
    public Collection<CallControlAction> getActions() {
        return actionsHolder.getActions();
    }

    @Nullable
    @Override
    public CallControlAction getAction(String id) {
        return actionsHolder.getAction(id);
    }

    @Nonnull
    @Override
    public CallControlAction getActionNN(String id) {
        CallControlAction action = getAction(id);
        if (action == null) {
            throw new IllegalStateException("Unable to find action with id " + id);
        }
        return action;
    }

    @Override
    public void refreshComponent() {
        component.removeAllComponents();
        BoxLayout composition = buildComposition();

        component.addComponent(WebComponentsHelper.unwrap(composition),0);
        component.setExpandRatio(WebComponentsHelper.unwrap(composition),0);
    }

    protected BoxLayout buildComposition(){
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        BoxLayout result = componentsFactory.createComponent(BoxLayout.HBOX);
        result.setSpacing(true);
        result.setWidth("100%");
        result.setMargin(true, false,false,false);

        // TODO: 19.10.2017 SetActiveProfile, Show, SetProfileExtendedStatus, Exit/Login queues
        for (CallControlAction action : this.getActions()){
            Button button = componentsFactory.createComponent(Button.NAME);
            button.setAction(action);
            button.setId(action.getId() + SoftphoneConstants.BTN_SUFFIX);
            button.setStyleName(SoftphoneConstants.BUTTON_STYLE_NAME);
            button.setCaption("");
            //button.setWidth(BUTTON_WIDTH);

            result.add(button);
        }

        return result;
    }

    @Override
    public void addSoftphoneAction(final AbstractCallControlAction callAction, boolean clearActions, boolean refreshComponent) {
        if (clearActions){
            clearActions();
        }

        this.addAction(callAction);

        if (refreshComponent){
            refreshComponent();
        }
    }

    @Override
    public void clearActions() {
        if (!this.getActions().isEmpty()) {
            this.removeAllActions();
        }
    }

    protected void addAction(CallControlAction action) {
        Preconditions.checkNotNullArgument(action, "action must be non null");

        actionsHolder.addAction(action);
    }

    protected void addAction(CallControlAction action, int index) {
        Preconditions.checkNotNullArgument(action, "action must be non null");

        actionsHolder.addAction(action, index);
    }

    protected void removeAction(@Nullable CallControlAction action) {
        actionsHolder.removeAction(action);
    }

    protected void removeAction(@Nullable String id) {
        actionsHolder.removeAction(id);
    }

    protected void removeAllActions() {
        actionsHolder.removeAllActions();
    }

    //addEvent
    protected String getParamsInfo(Map<String, String> params){
        StringBuilder builder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            Set<String> paramKeys = params.keySet();
            for (String key : paramKeys) {
                String value = params.get(key);
                builder.append(key).append("=").append(value).append("; ");
            }
        }
        return builder.toString();
    }
}

