package com.haulmont.thesis.crm.gui.xml.layout.loaders;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.gui.xml.layout.LayoutLoaderConfig;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractDatasourceComponentLoader;
import com.haulmont.thesis.crm.gui.components.charts.ScheduleChart;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import java.util.UUID;

public class ScheduleChartLoader extends AbstractDatasourceComponentLoader {

    protected LayoutLoaderConfig config;
    protected ComponentsFactory factory;

    public ScheduleChartLoader(Context context, LayoutLoaderConfig config, ComponentsFactory factory) {
        super(context);
        this.config = config;
        this.factory = factory;
    }

    @Override
    public Component loadComponent(ComponentsFactory factory, Element element, Component parent) {
        final ScheduleChart component = factory.createComponent(element.getName());

        initComponent(component, element, parent);
        return component;
    }

    protected void initComponent(ScheduleChart component, Element element, Component parent) {
        assignXmlDescriptor(component, element);
        loadId(component, element);
//        loadDatasource(component, element);

        loadVisible(component, element);
        loadEditable(component, element);
        loadEnable(component, element);

        loadStyleName(component, element);

//        loadCaption(component, element);
//        loadDescription(component, element);

        loadHeight(component, element);
        loadWidth(component, element);
        loadAlign(component, element);

        assignFrame(component);
    }

//    protected void loadDatasource(ScheduleChart component, Element element) {
//        String datasource = element.attributeValue("datasource");
//        if (!StringUtils.isEmpty(datasource)) {
//            CollectionDatasource<Entity<UUID>, UUID> ds = context.getDsContext().get(datasource);
//            if (ds == null) {
//                throw new IllegalStateException("Cannot find data source by name: " + datasource);
//            }
//            component.setDatasource(ds);
//        }
//    }

}
