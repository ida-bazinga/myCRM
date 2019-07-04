package com.haulmont.thesis.crm.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.haulmont.thesis.crm.entity.Room;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 23.12.2018.
 */

public class RoomEncoder {
    protected static ObjectMapper mapper = new ObjectMapper();
    protected static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    protected static SimpleModule simpleModule = new SimpleModule("SimpleModule");

    public static String encode(Room m) {
        try {
            simpleModule.addSerializer(new RoomSerializer());

            mapper.registerModule(simpleModule);
            mapper.setAnnotationIntrospector(new IgnoreInheritedIntrospector());
            mapper.setDateFormat(df);

            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(Collection<Room> m) {
        try {
            simpleModule.addSerializer(Room.class, new RoomSerializer());

            mapper.registerModule(simpleModule);
            mapper.setAnnotationIntrospector(new IgnoreInheritedIntrospector());
            mapper.setDateFormat(df);

            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
