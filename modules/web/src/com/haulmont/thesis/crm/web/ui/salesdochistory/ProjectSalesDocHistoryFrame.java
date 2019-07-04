/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.salesdochistory;

import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.thesis.crm.entity.SaleDocsHistoryInfo;

import javax.inject.Named;
import java.util.UUID;

public class ProjectSalesDocHistoryFrame extends AbstractFrame {

    protected boolean initialized = false;

    @Named("salesDocHistoryDs")
    protected GroupDatasource<SaleDocsHistoryInfo, UUID> salesDocHistoryDs;
    @Named("docHistoryTable")
    protected GroupTable docHistoryTable;

    public void init() {
        if (initialized) {
            return;
        }

    }

    public void refreshDs() {
        salesDocHistoryDs.refresh();
        docHistoryTable.expandAll();
    }
}