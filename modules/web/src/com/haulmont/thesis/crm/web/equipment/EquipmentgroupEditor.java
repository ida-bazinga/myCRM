package com.haulmont.thesis.crm.web.equipment;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.Equipment;

public class EquipmentgroupEditor<T extends Equipment> extends AbstractEditor<T> {

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setIsGroup(true);
    }
}