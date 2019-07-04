/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app.service
 */
package com.haulmont.thesis.crm.core.app.service;

import com.haulmont.thesis.crm.entity.CampaignTarget;

import java.util.List;
import java.util.Map;

/**
 * @author Kirill Khoroshilov
 */
public interface CampaignTargetService {
    String NAME = "crm_CampaignTargetService";

    List<CampaignTarget> getTargets(Map<String, Object> params);
}