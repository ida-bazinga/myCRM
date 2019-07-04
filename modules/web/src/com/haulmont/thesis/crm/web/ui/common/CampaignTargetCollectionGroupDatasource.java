/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.GroupDatasourceImpl;
import com.haulmont.thesis.crm.core.app.service.CampaignTargetService;
import com.haulmont.thesis.crm.entity.CampaignTarget;

import java.util.Map;
import java.util.UUID;

public class CampaignTargetCollectionGroupDatasource extends GroupDatasourceImpl<CampaignTarget, UUID> {

    @Override
    protected void loadData(Map<String, Object> params) {
        detachListener(data.values());
        data.clear();

        CampaignTargetService targetService = AppBeans.get(CampaignTargetService.NAME);

        for (CampaignTarget target : targetService.getTargets(params)) {
            data.put(target.getId(), target);
            attachListener(target);
        }
    }
}