package com.haulmont.thesis.crm.web.unit;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.entity.Unit;

import javax.inject.Inject;
import java.util.Map;

public class UnitEditor<T extends Unit> extends AbstractEditor<T> {


    @Inject
    protected UserSession userSession;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
    }

}