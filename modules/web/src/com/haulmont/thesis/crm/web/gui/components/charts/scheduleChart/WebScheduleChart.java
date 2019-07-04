package com.haulmont.thesis.crm.web.gui.components.charts.scheduleChart;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.thesis.crm.gui.components.charts.ScheduleChart;
import com.haulmont.thesis.crm.gui.schedulecharts.model.configurations.AbstractConfiguration;
import com.haulmont.thesis.crm.gui.schedulecharts.model.data.AbstractDataProvider;
import com.vaadin.ui.VerticalLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

//public class WebScheduleChart extends WebAbstractComponent<WebScheduleChart.ChartPanel> implements ScheduleChart {
public class WebScheduleChart extends WebAbstractComponent<VerticalLayout> implements ScheduleChart {

    private AbstractDataProvider dataProvider;
    private AbstractConfiguration configuration;
    private ScheduleChartJsComponent chartJsComponent;

    protected Date startDate;
    protected Date endDate;

    protected TimeSource timeSource = AppBeans.get(TimeSource.NAME);

    public WebScheduleChart() {
//        component = new ChartPanel();
        component = new VerticalLayout();
        component.setPrimaryStyleName("3kChart");
        component.setSizeFull();
        chartJsComponent = new ScheduleChartJsComponent();
        component.addComponent(chartJsComponent);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(timeSource.currentTimestamp());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startDate = calendar.getTime();

        calendar.add(Calendar.MONTH, +5);
        endDate = calendar.getTime();
    }

    @Override
    public AbstractConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(AbstractConfiguration configuration) {
        drawChart();
    }

    @Override
    public void setItemClickListener(ChartItemClickListener listener) {
        chartJsComponent.setItemClickListener(listener);
    }

/*
    @Override
    public void setDatasource(CollectionDatasource<Entity<UUID>, UUID> datasource) {
        this.datasource = datasource;

// todo use it   for hierarchy
//        this.hierarchyProperty = ((HierarchicalDatasource) datasource).getHierarchyPropertyName();
//
//        // if showProperty is null, the Tree will use itemId.toString
//        MetaProperty metaProperty = hierarchyProperty == null ? null : datasource.getMetaClass().getProperty(hierarchyProperty);


        if (datasource == null){
            setDataProvider(null);
        } else {
            CollectionDsHelper.autoRefreshInvalid(datasource, true);
        }

        setDataProvider(new GroupDataProvider());

            datasource.addListener(new CollectionDsListenerAdapter<Entity<UUID>>() {
                @Override
                public void collectionChanged(CollectionDatasource ds, Operation operation, List<Entity<UUID>> items) {
                    Collection<Room> rows;
                    if (ds.size() > 0) {
                        //noinspection unchecked
                        rows = ds.getItems();
                    } else {
                        rows = Collections.EMPTY_LIST;
                    }
                    //todo to roomLoadingScheduleChart
                    chartJsComponent.setRows(RoomEncoder.encode(rows));
                }
            });
        }
    }

    @Override
    public CollectionDatasource<Entity<UUID>, UUID> getDatasource() {
        return datasource;
    }

*/

    public AbstractDataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(AbstractDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public AbstractConfiguration getChart() {
        return configuration;
    }

    @Override
    public void drawChart() {
        if (dataProvider != null && !dataProvider.isEmpty()) {
            chartJsComponent.setData(dataProvider.toJson());
        }
    }

    @Override
    public void drawChart(AbstractConfiguration configuration) {
        this.configuration = configuration;
        drawChart();
    }

    protected class ChartPanel extends VerticalLayout{

        public ChartPanel(){
            setPrimaryStyleName("3kChart");
            setSizeFull();

//          добавить идентификатор компонента для JS кода чтобы там не вычислять контейнер
//            setId("3kChart");

        }

        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            //if (initial || dirty) {
//            if (dirty) {
//              if (initial) {
//                if (chartJsComponent != null) {

//                    setChartDates();

                    if (dataProvider != null && !dataProvider.isEmpty()) {
                        chartJsComponent.setData(dataProvider.toJson());
                    }

//                    setupDefaults(chart);

//                    if (chart.getDataProvider() != null) {
//                        chart.getDataProvider().bindToChart(chart);
//                    }

//                    getState().configuration = chart.toString();
//                    }
//                dirty = false;
//                }
//            }
        }
    }

}
