/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.reports.ReportingBean;
import com.haulmont.thesis.core.entity.TsReport;
import com.thoughtworks.xstream.XStream;

public class ExtThesisReportingBean extends ReportingBean {

    @Override
    protected XStream createXStream() {
        XStream xStream = super.createXStream();
        xStream.alias("report", TsReport.class);
        xStream.omitField(TsReport.class, "xml");
        xStream.omitField(TsReport.class, "deleteTs");
        xStream.omitField(TsReport.class, "deletedBy");
        xStream.omitField(TsReport.class, "overwriteByInit");
        return xStream;
    }
}
