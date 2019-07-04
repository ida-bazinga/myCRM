/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.ExtCompany;

import java.util.List;

/**
 * Created by k.khoroshilov on 11.02.2017.
 */
public interface CallerIdService {
    String NAME = "crm_CallerIdService";

    List<ExtCompany> getCompaniesByPhoneNumber(String phoneNumber);

}
