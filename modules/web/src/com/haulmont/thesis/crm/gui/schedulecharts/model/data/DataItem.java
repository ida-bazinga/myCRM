package com.haulmont.thesis.crm.gui.schedulecharts.model.data;

import java.io.Serializable;
import java.util.Collection;

public interface DataItem extends Serializable {

    Collection<String> getProperties();

    Object getValue(String property);
}