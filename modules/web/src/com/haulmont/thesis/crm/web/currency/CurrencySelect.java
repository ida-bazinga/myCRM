package com.haulmont.thesis.crm.web.currency;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.thesis.crm.entity.Currency;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class CurrencySelect extends AbstractWindow {

    @Named("mainDs")
    protected CollectionDatasource<Currency, UUID> mainDs;

    @Named("currency")
    protected LookupField currency;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(190).setWidth(300).setResizable(false);
        mainDs.refresh(params);
    }

    @Override
    public void ready() {

    }

    @Override
    public boolean close(String actionId) {
        mainDs.clear();
        return ((Window) frame).close(actionId);
    }


    public void selectButton() {
        mainDs.clear();
        if (currency.getValue()!=null) {
            mainDs.addItem((Currency) currency.getValue());
        }
        mainDs.setAllowCommit(false);
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }

    public void closeButton() {
        mainDs.clear();
        ((Window) frame).close(Window.CLOSE_ACTION_ID);
    }
}