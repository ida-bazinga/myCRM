package com.haulmont.thesis.crm.web.gui.components;

import com.haulmont.charts.gui.components.charts.Chart;
import com.haulmont.charts.gui.components.map.GoogleMapViewer;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.xml.layout.loaders.map.MapViewerLoader;
import com.haulmont.charts.web.gui.components.charts.amcharts.WebChart;
import com.haulmont.charts.web.gui.components.map.google.WebGoogleMapViewer;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.mainwindow.FoldersPane;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import com.haulmont.thesis.crm.gui.components.CTIButtonsPanel;
import com.haulmont.thesis.crm.gui.components.SPIPanel;
import com.haulmont.thesis.crm.gui.components.charts.ScheduleChart;
import com.haulmont.thesis.crm.gui.xml.layout.loaders.CTIButtonsPanelLoader;
import com.haulmont.thesis.crm.gui.xml.layout.loaders.SPIPanelLoader;
import com.haulmont.thesis.crm.gui.xml.layout.loaders.ScheduleChartLoader;
import com.haulmont.thesis.crm.web.gui.components.charts.scheduleChart.WebScheduleChart;
import com.haulmont.thesis.web.gui.components.ThesisComponentPalette;

import java.util.Map;

public class CrmComponentPalette extends ThesisComponentPalette {

    @Override
    public Map<String, Class<? extends ComponentLoader>> getLoaders() {
        Map<String, Class<? extends ComponentLoader>> loaders = super.getLoaders();
        loaders.put(CTIButtonsPanel.NAME, CTIButtonsPanelLoader.class);
        loaders.put(SPIPanel.NAME, SPIPanelLoader.class);
        loaders.put(MapViewer.TAG_NAME, MapViewerLoader.class);
        loaders.put(ScheduleChart.NAME, ScheduleChartLoader.class);
        return loaders;
    }

    @Override
    public Map<String, Class<? extends Component>> getComponents() {
        Map<String, Class<? extends Component>> components = super.getComponents();
        components.remove(FoldersPane.NAME);

        components.put(CTIButtonsPanel.NAME, WebCTIButtonsPanel.class);
        components.put(SPIPanel.NAME, WebSPIPanel.class);

        components.put(Chart.NAME, WebChart.class);
        components.put(GoogleMapViewer.NAME, WebGoogleMapViewer.class);
        components.put(ScheduleChart.NAME, WebScheduleChart.class);

        return components;
    }
}
