package com.haulmont.thesis.crm.web.invDoc.surcharge;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.OrdDoc;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Map;

public class Invsurcharge extends AbstractWindow {

    @Named("sum")
    protected TextField sum;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(150).setWidth(350).setResizable(false);
        Button furtherButton = getComponent("furtherButton");
        furtherButton.setAction(surcharge());
        Button closeButton = getComponent("closeButton");
        closeButton.setAction(close());
        setSum((OrdDoc) params.get("ordDoc"));
    }

    protected Action close() {
        return new AbstractAction("close") {
            @Override
            public void actionPerform(Component component) {
                getContext().getParams().remove("sum");
                close(Window.CLOSE_ACTION_ID);
            }
        };
    }

    protected Action surcharge() {
        return new AbstractAction("surcharge") {
            @Override
            public void actionPerform(Component component) {
                if (sum.getValue() == null) {
                    showNotification(getMessage("noSum"), NotificationType.HUMANIZED);
                    return;
                }
                getContext().getParams().put("sum", sum.getValue());
                close(Window.COMMIT_ACTION_ID);
            }
        };
    }

    protected void setSum(OrdDoc ordDoc) {
        BigDecimal sum = ordDoc.getFullSum();
        IntegrationResolver ir = ordDoc.getIntegrationResolver();
        if (ir != null) {
            if (ir.getSumPayment() != null) {
                sum = sum.subtract(ir.getSumPayment());
            }
        }
        this.sum.setValue(sum);
    }


}