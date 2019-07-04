package com.haulmont.thesis.crm.web.room;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.Room;

public class RoomGroupEditor<T extends Room> extends AbstractEditor<T> {

    @Override
    protected void initNewItem(T item) {
	super.initNewItem(item);
    item.setIsGroup(true);
    }
    
}