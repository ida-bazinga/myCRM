package com.haulmont.thesis.crm.web.ui.loadingInfo;

import com.google.common.collect.Lists;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.thesis.core.app.CalendarService;
import com.haulmont.thesis.crm.core.app.loadingInfo.ResourceLoadingService;
import com.haulmont.thesis.crm.entity.ResourceType;
import com.haulmont.thesis.crm.entity.Room;
import com.haulmont.thesis.crm.gui.components.charts.ScheduleChart;
import com.haulmont.thesis.crm.gui.schedulecharts.model.data.DataItem;
import com.haulmont.thesis.crm.gui.schedulecharts.model.data.GroupDataProvider;
import com.haulmont.thesis.crm.gui.schedulecharts.model.data.MapDataItem;
import com.haulmont.thesis.crm.parsers.ProjectSerializer;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 27.11.2018.
 */
public class ProjectRoomLoadingsWindow extends AbstractWindow {

    private final static UUID defaultRootRoomId = UuidProvider.fromString("17735349-9abf-29f6-d1cf-2b7d24d11c5f");
    private final static UUID defaultTypeId = UuidProvider.fromString("d27558d5-c37a-2c8a-f9f5-91c5e5f246c4");
    private final static int defaultMonthsCount = 5;

    private boolean updateChart = true;
    private Room rootRoom = null;
    private Collection<ResourceType> selectedTypes = new HashSet<>();

    protected GroupDataProvider dataProvider;

    @Named("roomLoadingChart")
    protected ScheduleChart chart;
    @Named("startDate")
    protected DateField startDateField;
    @Named("endDate")
    protected DateField endDateField;
    @Named("filterText")
    protected TextField filterTextField;
    @Named("selectRooms")
    protected Button selectRoomsBtn;
    @Named("showOptions")
    protected CheckBox showOptionsField;
    @Named("refreshBtn")
    protected Button refreshBtn;
    @Named("printBtn")
    protected Button printBtn;

    @Named("roomsDs")
    protected CollectionDatasource<Room, UUID> roomsDs;

    @Inject
    protected CalendarService calendarService;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected ResourceLoadingService resourceLoadingService;
    @Inject
    protected WindowConfig windowConfig;
    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        initDatesField();

        showOptionsField.setRequired(true);
        showOptionsField.setValue(false);

        dataProvider = new GroupDataProvider();
        dataProvider.addSerializer(new ProjectSerializer());
        chart.setDataProvider(dataProvider);

        selectRoomsBtn.setAction(createOpenFilterWindowAction());

        initListeners();

        this.addAction(createPrintChartAction());
    }

    protected Action createOpenFilterWindowAction(){
        return new BaseAction("openFilterWindow") {
            @Override
            public void actionPerform(Component component) {
                Map<String, Object> windowParams = new HashMap<>();
                windowParams.put("defaultType", getDefaultResourceType());
                windowParams.put("selectedTypes", selectedTypes);
                windowParams.put("rootRoom", rootRoom);

                final Window filterWindow = openWindow("roomTypeFilterWindow", WindowManager.OpenType.DIALOG, windowParams);

                filterWindow.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (!actionId.equals(Window.SELECT_ACTION_ID)) return;

                        CollectionDatasource<Room, UUID> rootRoomsDs =  filterWindow.getDsContext().getNN("rootRoomsDs");
                        rootRoom = rootRoomsDs.getItem();

                        CollectionDatasource<ResourceType, UUID> selectedTypesDs =  filterWindow.getDsContext().getNN("selectedTypesDs");
                        selectedTypes.clear();
                        selectedTypes.addAll(selectedTypesDs.getItems());

                        refreshRoomsDs();
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("selectRooms");
            }
        };
    }

    protected void initDatesField(){
        updateChart = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeSource.currentTimestamp());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startDateField.setValue(calendar.getTime());

        updateChart = true;
        calendar.add(Calendar.MONTH, defaultMonthsCount);
        endDateField.setValue(calendar.getTime());
    }

    protected void initListeners(){

        startDateField.addListener(getFieldValueListener());
        endDateField.addListener(getFieldValueListener());
        showOptionsField.addListener(getFieldValueListener());

        roomsDs.addListener(new CollectionDsListenerAdapter<Room>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<Room> items) {
                if(items!=null){
                    refreshChart();
                }
            }
        });

        chart.setItemClickListener(new ScheduleChart.ChartItemClickListener() {
            @Override
            public void onClick(String metaClassName, UUID entityId) {
                MetaClass metaClass = metadata.getClassNN(metaClassName);
                LoadContext ctx = new LoadContext(metaClass).setId(entityId).setView("edit");
                Entity entity = getDsContext().getDataSupplier().load(ctx);
                openEditor(windowConfig.getEditorScreenId(metaClass), entity, WindowManager.OpenType.NEW_TAB);

                //App.getInstance().getWindowManager().openEditor(windowInfo, card, WindowManager.OpenType.NEW_TAB);
            }
        });
    }

    protected ValueListener<Object> getFieldValueListener(){
        return new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (!updateChart) return;
                refreshChart();
            }
        };
    }


    //todo see https://stackoverflow.com/questions/6875807/convert-svg-to-pdf
    protected Action createPrintChartAction(){
        return new BaseAction("printChart") {
            @Override
            public void actionPerform(Component component) {
                if (chart != null){
                    showNotification("Coming soon!", NotificationType.TRAY);
                }
            }

            @Override
            public String getIcon() {
                return "icons/reports-print.png";
            }

            @Override
            public String getCaption() {
                return null;
            }
        };
    }

    @Override
    public void ready() {
        rootRoom = getDefaultRootRoom();
        selectedTypes = Lists.newArrayList(getDefaultResourceType());

        refreshRoomsDs();

        updateChart = true;
    }

    protected void refreshRoomsDs(){
        Map<String, Object> refreshParams = new HashMap<>();
        refreshParams.put("rootRoom", rootRoom);
        refreshParams.put("selectedTypes", selectedTypes);
        roomsDs.refresh(refreshParams);

        StringBuilder sb = new StringBuilder()
                .append(rootRoom.getName_ru())
                .append(": ");
        if (selectedTypes.isEmpty()){
            sb.append(getMessage("noTypesSelected"));
        } else {
            for(ResourceType item : selectedTypes) {
                sb.append(item.getInstanceName())
                        .append("; ");
            }
        }
        filterTextField.setValue(sb.toString());
    }

    public void refreshChart(){
        Date startDate = startDateField.getValue();
        Date endDate = endDateField.getValue();
        Boolean isShowOptions = showOptionsField.getValue();

        List<Room> rooms = new ArrayList<>(roomsDs.getItems());

        dataProvider.addGroup("columns", buildChartColumns(startDate, endDate));
        dataProvider.addGroup("rows", buildChartRows(rooms));
        dataProvider.addGroup("values", buildChartValues(startDate, endDate, rooms, isShowOptions));
        dataProvider.addGroup("collisions", buildChartCollisions(startDate, endDate, rooms));

        chart.drawChart();
    }

    protected List<DataItem> buildChartColumns(Date startDate, Date endDate){
        List<DataItem> result = new ArrayList<>();
        Date iterator = startDate;

        do {
            MapDataItem dataItem = new MapDataItem();
            dataItem.add("date", iterator);
            dataItem.add("isWorkDay", calendarService.isDateWorkDay(iterator));
            result.add(dataItem);

            iterator = DateUtils.addDays(iterator, 1);
        } while (endDate.after(iterator));

        return result;
    }

    protected List<DataItem> buildChartRows(List<Room> rooms){
        List<DataItem> result = new ArrayList<>();

        for (Room value : rooms){
            MapDataItem dataItem = new MapDataItem();
            dataItem.add("id", value.getId());
            dataItem.add("code", value.getCode());
            dataItem.add("name", value.getName_ru());
            dataItem.add("parentId", value.getParent().getId());
            dataItem.add("type", value.getResourceType().getName_ru());
            dataItem.add("visible", Boolean.TRUE);

            result.add(dataItem);
        }

        return result;
    }

    protected List<DataItem> buildChartValues(Date startDate, Date endDate, List<Room> rooms, Boolean isShowOptions){
        List<DataItem> result  = new ArrayList<>();

        Collection<Map<String, Object>> values = resourceLoadingService.getRoomLoadingInfo(startDate, endDate, rooms, isShowOptions);
        for (Map<String, Object> value : values){
            MapDataItem dataItem = new MapDataItem(value);
            result.add(dataItem);
        }

        return result;
    }

    protected List<DataItem> buildChartCollisions(Date startDate, Date endDate, List<Room> rooms){
        List<DataItem> result  = new ArrayList<>();

        Collection<Map<String, Object>> collisions = resourceLoadingService.getRoomLoadingCollisions(startDate, endDate, rooms);
        for (Map<String, Object> value : collisions){
            MapDataItem dataItem = new MapDataItem(value);
            result.add(dataItem);
        }
        return result;
    }

    protected Room getDefaultRootRoom(){
        LoadContext ctx = new LoadContext(Room.class)
                .setView(View.MINIMAL)
                .setId(defaultRootRoomId);
        return getDsContext().getDataSupplier().load(ctx);
    }

    protected ResourceType getDefaultResourceType(){
        LoadContext ctx = new LoadContext(ResourceType.class)
                .setView(View.MINIMAL)
                .setId(defaultTypeId);
        return getDsContext().getDataSupplier().load(ctx);
    }
}