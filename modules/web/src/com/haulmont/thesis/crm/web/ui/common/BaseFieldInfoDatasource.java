/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common;

import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.web.ui.FieldInfoDatasource;

/**
 * Created by k.khoroshilov on 19.02.2017.
 */
public class BaseFieldInfoDatasource<FieldInfo, UUID> extends FieldInfoDatasource {

    protected Datasource getMainDs() {
        return dsContext.get("categoryDs");
    }
}
