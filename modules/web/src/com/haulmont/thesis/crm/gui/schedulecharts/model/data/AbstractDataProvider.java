package com.haulmont.thesis.crm.gui.schedulecharts.model.data;

import java.io.Serializable;

public interface AbstractDataProvider extends Serializable {

    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    boolean isEmpty();

    String getDateFormat();
    void setDateFormat(String format);

    String toJson();
}