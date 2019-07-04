package com.haulmont.thesis.crm.web.ui.country;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
import java.util.Map;

public class CountryEditor extends AbstractEditor {

    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

}