package com.haulmont.thesis.crm.web.lineOfBusiness;

import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.LineOfBusiness;

public class LineOfBusinessEditor<T extends LineOfBusiness> extends AbstractEditor<T> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setIsGroup(false);
    }
}