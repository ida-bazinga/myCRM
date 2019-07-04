/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.components.calendar;

import com.haulmont.thesis.web.gui.components.calendar.LinkCalendarEvent;

public class ExtLinkCalendarEvent extends LinkCalendarEvent {

    private boolean allDay;

    @Override
    public void setAllDay(boolean b) {
        allDay = b;
    }

    @Override
    public boolean isAllDay() {
        return allDay;
    }
}
