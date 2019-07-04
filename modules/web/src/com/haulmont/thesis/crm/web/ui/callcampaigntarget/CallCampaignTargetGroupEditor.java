package com.haulmont.thesis.crm.web.ui.callcampaigntarget;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;

import javax.inject.Named;

public class CallCampaignTargetGroupEditor<T extends CallCampaignTrgt> extends AbstractEditor<T> {

    @Named("lastGroup")
    protected GroupBoxLayout lastGroup;
    @Named("scheduledActivityBox")
    protected BoxLayout scheduledActivityBox;
    @Named("nextGroup")
    protected GroupBoxLayout nextGroup;

    @Override
    protected void postInit(){
        lastGroup.setVisible(getItem().getLastActivity() != null);

        scheduledActivityBox.setVisible(
                getItem().getNextActivity() != null && getItem().getNextActivity().getKind().getCode().equals("campaign-scheduled-target")
        );

        nextGroup.setVisible(getItem().getNextActivity() != null && !getItem().getNextActivity().getKind().getCode().equals("campaign-scheduled-target"));
    }
}