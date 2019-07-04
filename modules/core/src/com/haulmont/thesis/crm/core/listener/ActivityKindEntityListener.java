/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.core.listener;

import javax.annotation.ManagedBean;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.thesis.crm.entity.ActivityKind;

/**
 * @author k.khoroshilov
 */
@ManagedBean("crm_ActivityKindEntityListener")
public class ActivityKindEntityListener implements AfterDeleteEntityListener<ActivityKind> {


    @Override
    public void onAfterDelete(ActivityKind entity) {
        entity.setReports(null);
        entity.setProcs(null);
    }
}