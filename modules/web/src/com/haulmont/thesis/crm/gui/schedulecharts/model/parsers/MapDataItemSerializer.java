package com.haulmont.thesis.crm.gui.schedulecharts.model.parsers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.haulmont.thesis.crm.gui.schedulecharts.model.data.MapDataItem;

import java.io.IOException;

public class MapDataItemSerializer extends StdSerializer<MapDataItem> {

    public MapDataItemSerializer(){ this (null);}

    public MapDataItemSerializer(Class<MapDataItem> t) {
        super(t);
    }

    @Override
    public void serialize(MapDataItem value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        for (String propName : value.getProperties()){
            jgen.writeObjectField(propName, value.getValue(propName));
        }
        jgen.writeEndObject();
    }
}
