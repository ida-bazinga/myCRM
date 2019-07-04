/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.softphoneSession.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneSessionManager;

import java.util.Map;
import java.util.UUID;

public class SoftphoneSessionCollectionDatasource extends CollectionDatasourceImpl<SoftPhoneSession, UUID> {

    @Override
    protected void loadData(Map<String, Object> params) {
        detachListener(data.values());
        data.clear();
        SoftPhoneSessionManager sessionManager = AppBeans.get(SoftPhoneSessionManager.class);

        for (SoftPhoneSession entity : sessionManager.getSessions()) {
            data.put(entity.getId(), entity);
            attachListener(entity);
        }
    }
}