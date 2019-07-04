package com.haulmont.thesis.crm.web.employee;

import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.web.ui.employee.EmployeeEditor;

import java.util.Map;

public class ExtEmployeeEditor<T extends ExtEmployee> extends EmployeeEditor<T> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }
}