/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.service;

import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.thesis.crm.entity.CampaignKind;
import com.haulmont.thesis.crm.entity.EmailCampaign;

import java.util.List;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 10.03.2017.
 */
public interface CampaignService {
    String NAME = "crm_CampaignService";

    Long getCountWithSpecifiedKind(UUID kindId);

    List<BaseCampaign> getWithKind(UUID kindId);

    boolean isSameKindExist(CampaignKind kind);

    String createName(CallCampaign entity);

    String createName(EmailCampaign entity);
}
