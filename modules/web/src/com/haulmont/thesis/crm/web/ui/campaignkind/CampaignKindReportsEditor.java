package com.haulmont.thesis.crm.web.ui.campaignkind;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.reports.entity.Report;
import com.haulmont.thesis.crm.entity.CampaignKind;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class CampaignKindReportsEditor extends AbstractEditor<CampaignKind> {
    @Named("listReports")
    protected TokenList listReports;
    @Named("allReportsDs")
    protected CollectionDatasource<Report, UUID> allReportsDs;
    @Named("reportsDs")
    protected CollectionDatasource<Report, UUID> reportsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        listReports.setItemChangeHandler(new TokenList.ItemChangeHandler() {
            @Override
            public void addItem(Object item) {
                reportsDs.addItem((Report)item);
                allReportsDs.excludeItem((Report) item);
            }

            @Override
            public void removeItem(Object item) {
                reportsDs.removeItem((Report)item);
                allReportsDs.addItem((Report) item);
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        setCaption(getItem().getName());
    }
}