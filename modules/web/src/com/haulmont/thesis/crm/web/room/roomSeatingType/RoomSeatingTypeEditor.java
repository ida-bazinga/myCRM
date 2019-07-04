package com.haulmont.thesis.crm.web.room.roomSeatingType;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.crm.entity.RoomSeatingType;

import javax.inject.Named;
import java.util.Map;

public class RoomSeatingTypeEditor<T extends RoomSeatingType> extends AbstractEditor<T> {


    @Named("mainDs")
    protected Datasource<T> mainDs;

    protected T roomSeatingType;

    public void init(Map<String, Object> params) {
        super.init(params);
        roomSeatingType = ((T) params.get("ITEM"));
    }

    @Override
    protected void postInit() {
        mainDs.setItem(roomSeatingType);
    }

    @Override
    protected boolean preCommit() {
       boolean isAttachments =  getItem().getRoomSeatingTypeAttachment() != null ? true : false;
       getItem().setHasAttachments(isAttachments);
       return super.preCommit();
    }
}