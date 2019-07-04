/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.components;

import com.haulmont.charts.gui.components.charts.Chart;
import com.haulmont.charts.gui.components.map.GoogleMapViewer;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.xml.layout.loaders.map.MapViewerLoader;
import com.haulmont.charts.web.gui.components.charts.amcharts.WebChart;
import com.haulmont.charts.web.gui.components.map.google.WebGoogleMapViewer;
import com.haulmont.cuba.gui.ComponentPalette;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @version $Id$
 */
public class ExtComponentGoogleMapPalette implements ComponentPalette {

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Class<? extends ComponentLoader>> getLoaders() {
        Map<String, Class<? extends ComponentLoader>> loaders = new HashMap<>();
        loaders.put(MapViewer.TAG_NAME, MapViewerLoader.class);
        return loaders;
    }

    @Override
    public Map<String, Class<? extends Component>> getComponents() {
        Map<String, Class<? extends Component>> components = new HashMap<>();
        components.put(Chart.NAME, WebChart.class);
        components.put(GoogleMapViewer.NAME, WebGoogleMapViewer.class);
        return components;
    }

}
