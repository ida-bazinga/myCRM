package com.haulmont.thesis.crm.web.callCampaignTarget;

import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.thesis.crm.entity.CallCampaignTarget;
import com.haulmont.thesis.crm.entity.CampaignTargetStatusEnum;

import javax.annotation.Nullable;
import javax.inject.Named;

public class CallCampaignTargetBrowser<T extends CallCampaignTarget> extends AbstractLookup{

    @Named("mainDs")
    private GroupDatasource<CallCampaignTarget, UUID> mainDs;
    @Named("mainTable")
    protected GroupTable targetsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initTableColoring();
    }

    public void refreshData(Timer timer) {
        mainDs.refresh();
    }

    protected void initTableColoring() {
        targetsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null)
                    return CallCampaignTargetBrowser.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(T target) {
        if (target.getStatus() != null && target.getStatus().equals(CampaignTargetStatusEnum.COMPLETED)) return "thesis-task-finished";
        if (target.getStatus() != null && target.getStatus().equals(CampaignTargetStatusEnum.INVOLVED)) return "taskremind";
        return null;
    }
}