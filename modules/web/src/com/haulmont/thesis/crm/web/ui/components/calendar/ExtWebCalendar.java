/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.components.calendar;

import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.entity.CalendarEventItem;
import com.haulmont.thesis.web.gui.components.calendar.WebCalendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ExtWebCalendar extends WebCalendar {

    @Override
    protected void initCalendarEvents(User user) {
        if (calendarViewDatasource != null) {
            Date startDate = calendarTools.getDateWithoutTime(calendar.getStartDate());
            GregorianCalendar endCalendarDate = new GregorianCalendar();
            endCalendarDate.setTime(calendarTools.getEndDateTime(calendar.getEndDate()));
            HashMap<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endCalendarDate.getTime());
            params.put("user", user);
            calendarViewDatasource.refresh(params);
            Date currentDate = timeSource.currentTimestamp();
            for (Object calendarViewId : calendarViewDatasource.getItemIds()) {
                //LinkCalendarEvent linkCalendarEvent = new LinkCalendarEvent();
                ExtLinkCalendarEvent linkCalendarEvent = new ExtLinkCalendarEvent();

                CalendarEventItem eventItem = (CalendarEventItem) calendarViewDatasource.getItem(calendarViewId);
                linkCalendarEvent.setCard(eventItem.getCard());

                linkCalendarEvent.setStart(eventItem.getStartTime());
                linkCalendarEvent.setEnd(eventItem.getEndTime());

                String description = String.format("%s [%s-%s]", eventItem.getDescription(), formatDate(eventItem.getStartTime()), formatDate(eventItem.getEndTime())) ;
                //linkCalendarEvent.setCaption(createEventCaption(description));
                linkCalendarEvent.setCaption(description);
                linkCalendarEvent.setDescription(description);
                String styleName = (currentDate.getTime() > eventItem.getEndTime().getTime() ? "tezisEvent-foggoten" : "tezisEvent");
                linkCalendarEvent.setStyleName(styleName);

                linkCalendarEvent.setAllDay(false);

                calendar.addEvent(linkCalendarEvent);
            }
        }
    }

    protected String formatDate(Date date) {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatDate.format(date);
    }


}