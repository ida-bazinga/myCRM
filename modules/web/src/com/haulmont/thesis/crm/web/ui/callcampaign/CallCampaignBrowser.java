package com.haulmont.thesis.crm.web.ui.callcampaign;

import com.haulmont.cuba.gui.components.Table;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.thesis.crm.enums.CampaignState;
import com.haulmont.thesis.crm.web.ui.basic.browse.AbstractCampaignCardBrowser;

import javax.annotation.Nullable;

public class CallCampaignBrowser<T extends CallCampaign> extends AbstractCampaignCardBrowser<T> {

    @Override
    protected void initTableColoring() {
        cardsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null)
                    return CallCampaignBrowser.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(T entity) {
        if (entity.getState() != null && entity.getState().equals(CampaignState.InWork.getId())) return "taskremind";
        if (entity.getState() != null && entity.getState().equals(CampaignState.Canceled.getId())) return "thesis-task-finished";
        if (entity.getState() != null && entity.getState().equals(CampaignState.Completed.getId())) return "thesis-task-finished";

        return null;
    }

}