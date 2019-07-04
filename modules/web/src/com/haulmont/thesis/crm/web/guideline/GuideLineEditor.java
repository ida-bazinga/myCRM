package com.haulmont.thesis.crm.web.guideline;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.GuideLine;

import java.util.Map;

public class GuideLineEditor<T extends GuideLine> extends AbstractEditor<T> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
      getWindowManager().setWindowCaption(this, String.format("%s(%s)", getMessage("caption"), getItem().getProject().getName()), null);
    }

}