package com.haulmont.thesis.crm.gui.schedulecharts.model.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.thesis.crm.gui.schedulecharts.model.parsers.MapDataItemSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = MapDataItemSerializer.class)
public class MapDataItem implements DataItem {

    private static final long serialVersionUID = 1088679193696578340L;

    private Map<String, Object> properties = new HashMap<>();

    public MapDataItem(Map<String, Object> properties) {
        this.properties.putAll(properties);
    }

    public MapDataItem() {}

    @Override
    public Collection<String> getProperties() {
        return properties.keySet();
    }

    @Override
    public Object getValue(String property) {
        return properties.get(property);
    }

    public MapDataItem add(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public void remove(String key) {
        properties.remove(key);
    }
}