/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.thesis.core.app.CardDeadlineInfoServiceImpl;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.workflow.core.entity.Card;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

public class ExtCardDeadlineInfoServiceImpl extends CardDeadlineInfoServiceImpl implements ExtCardDeadlineInfoService {

    @Override
    protected String getDescription(Card card) {
        if (card instanceof CallCampaign){
            CallCampaign campaign = (CallCampaign) card;
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
            return  "С " + sdf.format(campaign.getStartTimePlan()) + " по " + sdf.format(campaign.getEndTimePlan()) + "\n" +
                    StringUtils.defaultIfBlank(StringUtils.abbreviate(campaign.getComment(), 20),"");
        }

        return super.getDescription(card);
    }

}
