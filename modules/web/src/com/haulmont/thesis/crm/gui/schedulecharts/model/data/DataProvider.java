package com.haulmont.thesis.crm.gui.schedulecharts.model.data;

import java.util.Collection;
import java.util.List;

public interface DataProvider extends AbstractDataProvider {

    List<DataItem> getItems();

    void addItem(DataItem item);

    void addItems(Collection<DataItem> items);

    boolean contains(DataItem item);

    void updateItem(DataItem item);

    void removeItem(DataItem item);
}