package com.haulmont.thesis.crm.web.ui.callcampaigntarget;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;

import java.util.Map;

public class CallCampaignTargetEditor<T extends CallCampaignTrgt> extends AbstractEditor<T> {

    @Override
    public void init(final Map<String, Object> params) {
        getDialogParams().setHeight(200).setWidth(450);
    }
}