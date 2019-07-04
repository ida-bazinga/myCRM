/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.components.calendar;

import com.haulmont.cuba.gui.ComponentPalette;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import com.haulmont.thesis.gui.components.CalendarComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @version $Id$
 */
public class ExtComponentCalendarPalette implements ComponentPalette {

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Class<? extends ComponentLoader>> getLoaders() {
        return Collections.emptyMap();
    }


    @Override
    public Map<String, Class<? extends Component>> getComponents() {
        Map<String, Class<? extends Component>> components = new HashMap<>();
        components.put(CalendarComponent.CALENDAR, ExtWebCalendar.class);
        return components;
    }

}
