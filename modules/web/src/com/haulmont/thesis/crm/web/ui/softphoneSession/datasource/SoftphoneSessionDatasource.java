/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.softphoneSession.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.DatasourceImpl;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;
import com.haulmont.thesis.crm.web.softphone.core.SoftPhoneSessionManager;

public class SoftphoneSessionDatasource extends DatasourceImpl<SoftPhoneSession> {
    protected SoftPhoneSessionManager softphoneSessionManager;

    public SoftphoneSessionDatasource(){
        softphoneSessionManager = AppBeans.get(SoftPhoneSessionManager.class);
    }

    @Override
    public void commit() {
        super.commit();
    }

    @Override
    public SoftPhoneSession getItem() {
        return super.getItem();
    }
}
