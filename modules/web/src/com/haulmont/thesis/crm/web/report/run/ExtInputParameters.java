package com.haulmont.thesis.crm.web.report.run;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.reports.entity.ReportTemplate;
import com.haulmont.reports.gui.report.run.InputParametersController;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.crm.web.ui.app.NewThesisPrintManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExtInputParameters extends InputParametersController {

    protected NewThesisPrintManager thesisPrintManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        thesisPrintManager = AppBeans.get(NewThesisPrintManager.class);
    }

    @Override
    public void printReport() {
        if (report != null) {
            if (printReportHandler == null) {
                if (validateAll()) {
                    Field parameterField = parameterComponents.get("entity");
                    if (parameterField != null) {
                        Doc doc = parameterField.getValue();
                        String codeDateTemplate = DateString(doc.getDate());
                        ReportTemplate reportTemplate = thesisPrintManager.loadReportTemplate(report.getId(), codeDateTemplate);
                        templateCode = reportTemplate != null ? "DEFAULT".equals(reportTemplate.getCode()) ? null : reportTemplate.getCode() : null;
                        outputFileName = String.format("%s_(%s)", report.getName(), doc.getNumber().replaceAll("/", "-"));
                    }
                    reportGuiManager.printReport(report, collectParameters(), templateCode, outputFileName, this);
                }
            } else {
                printReportHandler.handle();
            }
        }
    }

    public String DateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

}