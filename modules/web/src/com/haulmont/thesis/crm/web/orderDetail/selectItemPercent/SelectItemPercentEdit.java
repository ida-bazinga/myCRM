package com.haulmont.thesis.crm.web.orderDetail.selectItemPercent;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.entity.DiscountTypeEnum;
import com.haulmont.thesis.crm.entity.OrderDetail;
import com.haulmont.thesis.crm.core.app.mathematic.ProductCalculate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SelectItemPercentEdit extends AbstractWindow {

    protected Collection<OrderDetail> orderDetailCollection;
    protected Set<OrderDetail> toCommit = new HashSet<>();
    protected DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(190).setWidth(350).setResizable(false);
        orderDetailCollection = (Collection<OrderDetail>) params.get("selectOrderDetail");
    }

    @Override
    public void ready() {
        this.dataManager = AppBeans.get(DataManager.class);
    }

    public void selectButton() {
        TextField margin = getComponent("marginSum");
        TextField discount = getComponent("discountSum");
        for (OrderDetail ordDetail:orderDetailCollection) {
            ordDetail.setDiscountType(DiscountTypeEnum.percent);
            ordDetail.setDiscountSum(discount.getValue() != null ? new BigDecimal(discount.getValue().toString()) : null);
            ordDetail.setMarginSum(margin.getValue() != null ? new BigDecimal(margin.getValue().toString()) : null);
            ordDetail.setCost(BigDecimal.ZERO);
            ProductCalculate productCalculate = new ProductCalculate(ordDetail);
            Map<String, Object> validate = productCalculate.validateCalculate();
            if (validate.size() > 0) {
                showNotification(getMessage(validate.get("message").toString()), NotificationType.HUMANIZED);
                return;
            }
            toCommit.add(productCalculate.costCalculation());
        }
        commit();
        close(Window.CLOSE_ACTION_ID);
    }

    protected void commit(){
        if(toCommit.size() > 0) {
            dataManager.commit(new CommitContext(toCommit));
        }
    }

    public void closeButton() {
        close(Window.CLOSE_ACTION_ID);
    }

}