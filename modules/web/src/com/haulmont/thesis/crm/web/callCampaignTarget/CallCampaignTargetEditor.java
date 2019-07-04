package com.haulmont.thesis.crm.web.callCampaignTarget;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.thesis.crm.entity.CallCampaignTarget;

import javax.inject.Named;
import java.util.Map;

public class CallCampaignTargetEditor extends AbstractEditor<CallCampaignTarget> {

    @Named("lastGroup")
    protected GroupBoxLayout lastGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        //getDialogParams().setWidth(870).setHeight(450);
    }

    @Override
    protected void postInit(){
        lastGroup.setVisible(getItem().getLastActivity() != null);
    }

}