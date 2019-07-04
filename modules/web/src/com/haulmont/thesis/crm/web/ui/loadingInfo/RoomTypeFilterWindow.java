package com.haulmont.thesis.crm.web.ui.loadingInfo;

import com.google.common.collect.Lists;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.thesis.crm.entity.ResourceType;
import com.haulmont.thesis.crm.entity.Room;

import javax.inject.Named;
import java.util.*;

public class RoomTypeFilterWindow extends AbstractWindow {

    private ResourceType defaultType;

    private Room rootRoom = null;
    private Collection<ResourceType> selectedTypes = new HashSet<>();

    @Named("rootRoom")
    protected LookupField rootRoomField;
    @Named("selectedTypes")
    protected TokenList selectedTypesField;
    @Named("rootRoomsDs")
    protected CollectionDatasource<Room, UUID> rootRoomsDs;
    @Named("typesDs")
    protected CollectionDatasource<ResourceType, UUID> typesDs;
    @Named("selectedTypesDs")
    protected CollectionDatasource<ResourceType, UUID> selectedTypesDs;
    @Named("confirmBtn")
    protected Button confirmButton;
    @Named("closeBtn")
    protected Button closeButton;

    @Override
    public void init(Map<String, Object> params) {
        defaultType = (ResourceType) params.get("defaultType");

        selectedTypes.clear();
        selectedTypes.addAll((Collection<ResourceType>) params.get("selectedTypes"));
        rootRoom = (Room) params.put("rootRoom", rootRoom);

        selectedTypesField.setRequired(true);

        initListeners();

        confirmButton.setAction(new BaseAction(Window.SELECT_ACTION_ID) {
            @Override
            public void actionPerform(Component component) {
                close(Window.SELECT_ACTION_ID);
            }
        });

        closeButton.setAction(new BaseAction(Window.CLOSE_ACTION_ID) {
            @Override
            public void actionPerform(Component component) {
                close(Window.CLOSE_ACTION_ID);
            }
        });

        getDialogParams()
                .setModal(true)
                .setWidth(500)
                .setCloseable(false);
    }

    protected void initListeners(){
        rootRoomField.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                selectedTypesDs.clear();
                selectedTypesDs.addItem(defaultType);
            }
        });

        selectedTypesDs.addListener(new CollectionDsListenerAdapter<ResourceType>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<ResourceType> items) {
                refreshTypesDs();
            }
        });
    }

    @Override
    public void ready() {
        rootRoomField.setValue(rootRoom);

        if (!selectedTypes.isEmpty()){
            selectedTypesDs.clear();
            for(ResourceType type :selectedTypes){
                selectedTypesDs.addItem(type);
            }
        }
    }

    @Override
    protected boolean preClose(String actionId) {
        if (actionId.equals("confirm") && selectedTypesDs.size() == 0) {
            showNotification("Error", getMessage("noTypesSelected"), NotificationType.HUMANIZED);
            return false;
        }
        return super.preClose(actionId);
    }

    protected void refreshTypesDs(){
        Map<String, Object> refreshParams = new HashMap<>();

        List<String> excludeList = Lists.newArrayList("Комплекс", "Этаж" , "Кабинет", "Зона");

        for (ResourceType type : selectedTypesDs.getItems()){
            excludeList.add(type.getName_ru());
        }

        refreshParams.put("exclude", excludeList);
        typesDs.refresh(refreshParams);
    }
}