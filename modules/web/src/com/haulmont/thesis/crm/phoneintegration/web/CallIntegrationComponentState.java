/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.phoneintegration.web;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * Created by k.khoroshilov on 11.04.2016.
 */
public class CallIntegrationComponentState extends JavaScriptComponentState {
    public boolean isConnected;
    public String message;
    public ServiceCommand command;
}


