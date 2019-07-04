package com.haulmont.thesis.crm.web.staff;

import com.haulmont.cuba.gui.components.AbstractWindow;
import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.Staff;

public class StaffGroupEditor<T extends Staff> extends AbstractEditor<T> {

    @Override
    protected void initNewItem(T item) {
	super.initNewItem(item);
    item.setIsGroup(true);
    }
    
}