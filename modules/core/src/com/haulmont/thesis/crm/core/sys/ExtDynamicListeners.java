/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.sys;

import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.core.sys.DynamicListeners;

import javax.annotation.PostConstruct;

public class ExtDynamicListeners extends DynamicListeners {
    @Override
    @PostConstruct
    protected void postConstruct() {
        super.postConstruct();
        addListenerByClass(Organization.class, "crm_OrganizationEntityListener");

    }
}
