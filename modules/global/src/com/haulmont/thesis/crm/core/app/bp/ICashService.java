/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import java.util.List;

/**
 * Created by d.ivanov on 05.06.2017.
 */
public interface ICashService {
    String NAME = "ICashService";

    boolean setCashService(List<Object> cashList);
}
