/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.contactcenter;

/**
 * @author k.khoroshilov
 */
@Deprecated
public interface OperatorQueueSchedulerBean {
    String NAME = "crm_OperatorQueueScheduler";

    Boolean queueProcessing();

}
