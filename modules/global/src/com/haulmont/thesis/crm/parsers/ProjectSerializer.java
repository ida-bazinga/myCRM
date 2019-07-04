package com.haulmont.thesis.crm.parsers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.haulmont.thesis.crm.entity.ExtProject;

import java.io.IOException;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 11.12.2018.
 */

public class ProjectSerializer extends StdSerializer<ExtProject> {

    public ProjectSerializer() {
        this(null);
    }

    public ProjectSerializer(Class<ExtProject> t) {
        super(t);
    }

    @Override
    public void serialize(ExtProject value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId().toString());
        jgen.writeStringField("name_ru", value.getName());
        jgen.writeEndObject();
    }

    @Override
    public Class<ExtProject> handledType(){
        return ExtProject.class;
    }
}