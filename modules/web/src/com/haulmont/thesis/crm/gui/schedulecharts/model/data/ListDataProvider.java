package com.haulmont.thesis.crm.gui.schedulecharts.model.data;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.haulmont.thesis.crm.gui.schedulecharts.model.configurations.AbstractConfiguration;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ListDataProvider implements DataProvider {

    private static final long serialVersionUID = -8893186272622531844L;

    private String dateFormat = DEFAULT_DATE_FORMAT;
    private List<DataItem> items = new ArrayList<>();
    private Set<JsonSerializer> serializers = new HashSet<>();

    public ListDataProvider() {
    }

    public ListDataProvider(DataItem... items) {
        if (items != null) {
            this.items.addAll(Arrays.asList(items));
        }
    }

    public ListDataProvider(List<DataItem> items) {
        this.items.addAll(items);
    }

    @Override
    public List<DataItem> getItems() {
        return items;
    }

    @Override
    public void addItem(DataItem item) {
        items.add(item);
    }

    @Override
    public void addItems(Collection<DataItem> items) {
        this.items.addAll(items);
    }

    @Override
    public boolean contains(DataItem item) {
        return items.contains(item);
    }

    @Override
    public void updateItem(DataItem item) {
    }

    @Override
    public boolean isEmpty(){
        return items.isEmpty();
    }

    @Override
    public void removeItem(DataItem item) {
        items.remove(item);
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public void setDateFormat(String format) {
        dateFormat = StringUtils.isNotBlank(format) ? format : DEFAULT_DATE_FORMAT;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String toJson () {
        try {
            ObjectMapper mapper = new ObjectMapper();

            if (serializers.size()>0){
                SimpleModule simpleModule = new SimpleModule("SimpleModule");
                for (JsonSerializer serializer : serializers){
                    simpleModule.addSerializer(serializer);
                }
                mapper.registerModule(simpleModule);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
            mapper.setDateFormat(sdf);

            List<DataItem> items = isEmpty() ? new ArrayList<DataItem>() : getItems();

            return mapper.writeValueAsString(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSerializer(JsonSerializer serializer){
        serializers.add(serializer);
    }
}