package com.haulmont.thesis.crm.web.outboundCampaign;

import com.haulmont.cuba.gui.components.Table;
import com.haulmont.thesis.crm.entity.OutboundCampaign;
import com.haulmont.thesis.web.ui.basicdoc.browse.AbstractDocBrowser;

import javax.annotation.Nullable;
import java.util.Map;

public class OutboundCampaignBrowser<T extends OutboundCampaign> extends AbstractDocBrowser<OutboundCampaign> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "crm$OutboundCampaign";
    }

    @Override
    protected void initTableColoring() {
        cardsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null)
                    return OutboundCampaignBrowser.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(T entity) {
        if (entity.getStatus() != null && entity.getStatus().getCode().equals("inProgress")) return "taskremind";
        if (entity.getStatus() != null && entity.getStatus().getCode().equals("canceled")) return "thesis-task-finished";
        if (entity.getStatus() != null && entity.getStatus().getCode().equals("ended")) return "thesis-task-finished";

        return null;
    }
}