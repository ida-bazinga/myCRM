package com.haulmont.thesis.crm.web.room;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.crm.entity.Room;

import java.util.Map;

import javax.inject.Inject;

public class RoomLookup extends AbstractLookup {

    @Inject
    protected TreeTable mainTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
    super.init(params);

        mainTable.addGeneratedColumn("reference", new Table.ColumnGenerator<Room>() {
            @Override
            public Component generateCell(Room entity) {
                String value = (entity.getReference() != null) ? entity.getReference() : "";
                Link field = componentsFactory.createComponent(Link.NAME);
                field.setTarget("_blank");
                field.setCaption(value);
                field.setUrl(value);
                return field;
            }
        });
    }
}