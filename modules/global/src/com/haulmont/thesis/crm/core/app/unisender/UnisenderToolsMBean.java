/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.unisender;

import org.springframework.jmx.export.annotation.ManagedOperation;

/**
 * Created by a.donskoy on 24.04.2018.
 */
public interface UnisenderToolsMBean {

    @ManagedOperation(description = "Run all requests for new MessageCampaigns, unexpired MessageCampaigns' and Messages' statuses")
    void getMessagesStatusesTask();
}
