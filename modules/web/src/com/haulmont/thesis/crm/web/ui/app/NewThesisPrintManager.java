/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.app;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.entity.ReportTemplate;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.web.app.ThesisPrintManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(NewThesisPrintManager.NAME)
public class NewThesisPrintManager extends ThesisPrintManager {

    public static final String NAME = "thesis_NewThesisPrintManager";
    private static Log log = LogFactory.getLog(NewThesisPrintManager.class);
    public PopupButton printButton;


    public void printBulkReport(Report report, Collection<Entity> entities, boolean isParams) {

        Map<String, Object> params = new HashMap<>();
        if (!isParams) {
            params.put(ENTITY_ALIAS, entities.iterator().next());
        }
        if (entities.size() > 1)
            bulkReport(report, entities, isParams);
        else
            printReport(report, params, isParams);
    }


    protected void printReport(Report report, Map<String, Object> params, boolean isParams) {

        if (report == null) {
            log.error("Report \"" + report.getName() + "\" not found");
            return;
        }
        if (!isParams) {
            Doc doc = (Doc) params.get("entity");
            String docNumber = doc.getNumber();
            String outputFileName = String.format("%s_(%s)", report.getName(), docNumber.replaceAll("/", "-"));
            String codeDateTemplate = DateString(doc.getDate());
            ReportTemplate reportTemplate = loadReportTemplate(report.getId(), codeDateTemplate);
            String reportTemplateCode = reportTemplate != null ? "DEFAULT".equals(reportTemplate.getCode()) ? null : reportTemplate.getCode() : null;
            reportGuiManager.printReport(report, params, reportTemplateCode, outputFileName);
        } else {
            printButton.getFrame().openWindow("report$inputParameters", WindowManager.OpenType.DIALOG, Collections.<String, Object>singletonMap("report", report));
        }
    }


    protected void bulkReport(Report report, Collection entities, boolean isParams) {
        if (report == null) {
            log.error("Report \"" + report.getName() + "\" not found");
            return;
        }
        if (!isParams) {
            reportGuiManager.bulkPrint(report, ENTITY_ALIAS, entities);
        } else {
            printButton.getFrame().openWindow("report$inputParameters", WindowManager.OpenType.DIALOG, Collections.<String, Object>singletonMap("report", report));
        }
    }

    public ReportTemplate loadReportTemplate(UUID reportId, String codeDateTemplate) {
        LoadContext ctx = new LoadContext(ReportTemplate.class);
        LoadContext.Query query = ctx.setQueryString("select t from report$ReportTemplate t where t.report.id = :reportId and t.code <= :code order by t.code desc");
        query.setParameter("reportId", reportId);
        query.setParameter("code", codeDateTemplate);
        query.getFirstResult();
        return dataService.load(ctx);
    }

    public String DateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    public boolean reportParameters(Report report) {
        boolean isParams = false;

        if (report.getInputParameters() != null) {
            for (ReportInputParameter p : report.getInputParameters()) {
                if (!ENTITY_ALIAS.equals(p.getAlias())) {
                    isParams = true;
                }
            }
        }
        return isParams;
    }

}
