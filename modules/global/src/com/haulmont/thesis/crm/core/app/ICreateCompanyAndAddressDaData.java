/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.thesis.crm.entity.Address;
import com.haulmont.thesis.crm.entity.ExtCompany;

/**
 * Created by d.ivanov on 21.03.2017.
 */
public interface ICreateCompanyAndAddressDaData {
    String NAME = "ICreateCompanyAndAddressDaData";

    ExtCompany getCompany(String inn);
    Address getAddress(String txtAddress);
}
