package com.haulmont.thesis.crm.gui.schedulecharts.model.data;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GroupDataProvider implements AbstractDataProvider {

    private static final long serialVersionUID = -5612768306765340481L;

    private String dateFormat = "yyyy-MM-dd'T'00:00:00Z";
    private Map<String, Collection<DataItem>> items = new HashMap<>();
    private Set<JsonSerializer> serializers = new HashSet<>();

    public GroupDataProvider() {
    }

//    public GroupDataProvider(Map<String, List<DataItem>> items) {
//        this.items.putAll(items);
//    }

    public void addGroup(String groupName) {
//        if (!contains(groupName)){
            addGroup(groupName, new ArrayList<DataItem>());
//        }
    }

    public void addGroup(String groupName, Collection<DataItem> groupItems) {
//        if (contains(groupName)){
//            addItems(groupName, groupItems);
//        } else {
            items.put(groupName, groupItems);
//        }
    }

    public void removeGroup(String name) {
        items.remove(name);
    }

    public void clear(){
        items.clear();
    }

    public void addItem(String groupName, DataItem item) {
        Collection<DataItem> groupItems = items.get(groupName);
        groupItems.add(item);
    }

    public void addItems(String groupName, Collection<DataItem> newItems) {
        Collection<DataItem> groupItems = items.get(groupName);
        groupItems.addAll(newItems);
    }

    public boolean contains(String groupName) {
        return items.containsKey(groupName);
    }

    public Map<String, Collection<DataItem>>  getItems() {
        return items;
    }

 /*
    public void updateItem(DataItem item) {
    }
*/

    @Override
    public boolean isEmpty(){
        return items.isEmpty();
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

            Map<String, Collection<DataItem>> items = isEmpty() ? new HashMap<String, Collection<DataItem>>() : getItems();

            return mapper.writeValueAsString(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSerializer(JsonSerializer serializer){
        serializers.add(serializer);
    }
}