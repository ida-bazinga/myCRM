/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.processaction.activity;

import com.haulmont.thesis.crm.entity.CallActivity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component(CallActivityAccessData.NAME)
@Scope("prototype")
public class CallActivityAccessData<T extends CallActivity> extends DefaultActivityAccessData<T>  {
    public static final String NAME = "crm_CallActivityAccessData";

    public CallActivityAccessData(Map<String, Object> params) {
        super(params);
    }

    @Override
    public void setItem(T item) {
        super.setItem(item);
    }

}
