package com.haulmont.thesis.crm.web.ui.roomloadingcollision;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.crm.entity.RoomLoadingCollision;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author Kirill Khoroshilov, 2019
 */
public class RoomLoadingCollisionBrowser extends AbstractLookup {

    @Named("roomLoadingCollisionsDs")
    protected CollectionDatasource<RoomLoadingCollision, UUID> mainDs;

    public void init(Map<String, Object> params) {
        initListeners();
    }

    protected void initListeners() {
        mainDs.addListener(new DsListenerAdapter<RoomLoadingCollision>() {
            @Override
            public void valueChanged(RoomLoadingCollision source, String property, Object prevValue, Object value) {
                if (property.equals("isCollision")) {
                    mainDs.commit();
                }
            }
        });
    }
}