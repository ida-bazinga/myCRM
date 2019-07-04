/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.gui.xml.layout.loaders.ComponentLoader;
import com.haulmont.thesis.crm.gui.components.CTIButtonsPanel;
import org.dom4j.Element;

public class CTIButtonsPanelLoader extends ComponentLoader {

    public CTIButtonsPanelLoader(ComponentLoader.Context context) {
        super(context);
    }

    @Override
    public Component loadComponent(ComponentsFactory factory, Element element, Component parent) {
        CTIButtonsPanel component = factory.createComponent(element.getName());

        initComponent(component, element, parent);

        return component;
    }

    protected void initComponent(CTIButtonsPanel component, Element element, Component parent) {
        loadId(component, element);
        loadEnable(component, element);
        loadVisible(component, element);
        loadAlign(component, element);
        loadHeight(component, element);
        loadWidth(component, element);

        assignFrame(component);
    }
}
