/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.thesis.crm.entity.CampaignKind;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_CampaignKindEntityListener")
public class CampaignKindEntityListener implements AfterDeleteEntityListener<CampaignKind> {

    @Override
    public void onAfterDelete(CampaignKind entity) {
        entity.setReports(null);
        entity.setProcs(null);
    }
}