package com.haulmont.thesis.crm.core.wf

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.TimeSource
import com.haulmont.thesis.crm.entity.CallCampaign
import com.haulmont.workflow.core.activity.CardActivity
import com.haulmont.workflow.core.entity.Card
import org.jbpm.api.activity.ActivityExecution

class SetEndTimeFactActivity extends CardActivity {

    public void execute(ActivityExecution execution) throws Exception {
        super.execute(execution)
        Card card = findCard(execution)

        if (card instanceof CallCampaign){
            card.setEndTimeFact(AppBeans.get(TimeSource.class).currentTimestamp())
        }
    }
}
