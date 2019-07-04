package com.haulmont.thesis.crm.web.orderDetail.group;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.Currency;
import com.haulmont.thesis.crm.entity.OrderDetail;

import javax.annotation.Nullable;
import javax.inject.Named;
import java.util.*;

public class OrderDetailGroupEdit extends AbstractWindow {

    @Named("dependentDetail")
    protected LookupField dependentDetail;

    @Named("ordDocGroup")
    protected TextField ordDocGroup;

    @Named("isDependentDetail")
    protected CheckBox isDependentDetail;

    @Named("isOrdDocGroup")
    protected CheckBox isOrdDocGroup;

    @Named("detailDs")
    protected CollectionDatasource<Currency, UUID> detailDs;

    protected DataManager dataManager;
    protected Collection<OrderDetail> orderDetailCollection;
    protected Set<Entity> toCommit = new HashSet<>();
    protected boolean isDelOrdDocGroup = false;
    protected boolean isDelDependentDetail = false;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(305).setWidth(350).setResizable(false);
        detailDs.refresh(params);
        orderDetailCollection = (Collection<OrderDetail>) params.get("selectOrderDetail");
        isOrdDocGroup.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                isDelOrdDocGroup = (boolean)value;
                ordDocGroup.setEditable(!isDelOrdDocGroup);
                if (isDelOrdDocGroup) {
                    ordDocGroup.setValue(null);
                }
            }
        });
        isDependentDetail.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                isDelDependentDetail = (boolean)value;
                dependentDetail.setEditable(!isDelDependentDetail);
                if (isDelDependentDetail) {
                    dependentDetail.setValue(null);
                }
            }
        });
    }

    @Override
    public void ready() {
        dependentDetail.setOptionsDatasource(detailDs);
        this.dataManager = AppBeans.get(DataManager.class);
    }

    public void selectButton() {
        if (isCommit()) {
            if (commit()) {
                close(Window.CLOSE_ACTION_ID);
            }
        }
    }

    public boolean commit() {
        if (toCommit.size() > 0) {
            dataManager.commit(new CommitContext(toCommit));
        }
        return true;
    }

    public boolean isCommit() {
        String ordDocGroupString = ordDocGroup.getValue() != null ? ordDocGroup.getValue().toString() : "";
        OrderDetail selectOrderDetail = dependentDetail.getValue();
        boolean editOrdDocGroup = ordDocGroup.getValue() != null;
        boolean editDependentDetail = dependentDetail.getValue() != null;

        for (OrderDetail orderDetail:orderDetailCollection) {
            if (isDelDependentDetail != editDependentDetail) {
                orderDetail.setDependentDetail(selectOrderDetail);
            }
            if (isDelOrdDocGroup != editOrdDocGroup) {
                orderDetail.setOrderDetailGroup(ordDocGroupString);
            }
            toCommit.add(orderDetail);
        }
        return true;
    }

    public void closeButton() {
        close(Window.CLOSE_ACTION_ID);
    }

}