/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.ExtEmployee;

import javax.annotation.Nullable;

public interface CallCampaignOperatorQueueService {
    String NAME = "crm_CallCampaignOperatorQueueService";

    boolean isOperatorInQueue(ExtEmployee operator);

    int getQueueSize();

    @Nullable
    ExtEmployee getFirstOperator();

    void addOperator(ExtEmployee operator);

    boolean removeOperator(ExtEmployee operator);
}
