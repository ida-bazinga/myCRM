package com.haulmont.thesis.crm.web.bookingEvent;

import com.haulmont.thesis.crm.entity.BookingEvent;
import com.haulmont.thesis.web.ui.basicdoc.browse.AbstractDocBrowser;

import java.util.Map;

public class BookingEventBrowse<T extends BookingEvent> extends AbstractDocBrowser<T> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "crm$BookingEvent";
    }
}