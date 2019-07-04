package com.haulmont.thesis.crm.web.ui.operator;

import com.haulmont.cuba.gui.components.AbstractWindow;
import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractLookup;

public class OperatorBrowser extends AbstractLookup {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidth(800).setHeight(400);
    }
}