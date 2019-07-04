package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.thesis.crm.entity.BaseActivity;

/**
 * @author Kirill Khoroshilov, 2018
 */

public interface ActivityWorker {
    String NAME = "crm_ActivityWorker";

    String createDescription(BaseActivity entity);
}
