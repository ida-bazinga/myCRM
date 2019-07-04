package com.haulmont.thesis.crm.parsers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.haulmont.thesis.crm.entity.Room;

import java.io.IOException;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 11.12.2018.
 */

public class RoomSerializer extends StdSerializer<Room> {

    public RoomSerializer() {
        this(null);
    }

    public RoomSerializer(Class<Room> t) {
        super(t);
    }

    @Override
    public void serialize(Room value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId().toString());
        jgen.writeStringField("code", value.getCode());
        jgen.writeStringField("name", value.getName_ru());
        jgen.writeStringField("parentId", value.getParent().getId().toString());
        jgen.writeStringField("type", value.getResourceType().getInstanceName());
        jgen.writeBooleanField("visible", Boolean.TRUE);

        jgen.writeEndObject();
    }

    @Override
    public Class<Room> handledType(){
        return Room.class;
    }
}
