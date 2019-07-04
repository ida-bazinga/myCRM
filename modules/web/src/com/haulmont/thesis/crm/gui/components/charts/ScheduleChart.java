package com.haulmont.thesis.crm.gui.components.charts;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.thesis.crm.gui.schedulecharts.model.configurations.AbstractConfiguration;
import com.haulmont.thesis.crm.gui.schedulecharts.model.data.AbstractDataProvider;

import java.util.UUID;

public interface ScheduleChart extends Component, Component.BelongToFrame {
    String NAME = "scheduleChart";

    AbstractConfiguration getConfiguration();
    void setConfiguration(AbstractConfiguration chart);

    void setDataProvider(AbstractDataProvider dataProvider);

    void setItemClickListener(ChartItemClickListener listener);

    AbstractDataProvider getDataProvider();

    void drawChart();
    void drawChart(AbstractConfiguration configuration);

    public interface ChartItemClickListener {
        void onClick(String metaClassName, UUID entityId);
    }
}
