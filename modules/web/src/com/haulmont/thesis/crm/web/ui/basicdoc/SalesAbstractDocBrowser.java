/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.basicdoc;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.reports.entity.Report;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.crm.web.ui.app.NewThesisPrintManager;
import com.haulmont.thesis.crm.web.ui.common.actions.NewCopyDocAction;
import com.haulmont.thesis.web.ui.basicdoc.browse.AbstractDocBrowser;
import com.haulmont.workflow.core.app.WfUtils;

import java.util.List;


public class SalesAbstractDocBrowser<T extends Doc> extends AbstractDocBrowser<T> {

    protected PopupButton printButton;
    protected String windowId;
    protected NewThesisPrintManager thesisPrintManager;

    @Override
    public void ready() {
        initPrintActions();
    }

    protected void initPrintActions() {
        printButton = getComponent("printButton");
        windowId = String.format("%s.browse", this.entityName);
        thesisPrintManager = AppBeans.get(NewThesisPrintManager.class);
        if (printButton != null) {
            reportList();
        }
    }

    protected void addPrintActions(final Report report) {
        String actionId = WfUtils.Translit.toTranslit(report.getName().toLowerCase().trim()).replace(" ", "_");
        printButton.addAction(new AbstractAction(actionId) {
            @Override
            public void actionPerform(Component component) {
                boolean isParams = thesisPrintManager.reportParameters(report);
                if (isParams || cardsTable.getSingleSelected() != null) {
                    thesisPrintManager.printButton = printButton;
                    thesisPrintManager.printBulkReport(report, cardsTable.getSelected(), isParams);

                } else {
                    showNotification(messages.getMessage(this.getClass(), "Не указаны обязательные параметры"), IFrame.NotificationType.HUMANIZED);
                }

            }
            @Override
            public String getCaption() {
                return report.getLocName() != null ? report.getLocName() : report.getName();
            }
        });
    }

    protected void reportList() {
        List<Report> reportList = thesisPrintManager.loadReports(windowId);
        for (Report report:reportList) {
            addPrintActions(report);
        }
    }

    @Override
    protected Action createCopyAction() {
        return new NewCopyDocAction(cardsTable);
    }




}
