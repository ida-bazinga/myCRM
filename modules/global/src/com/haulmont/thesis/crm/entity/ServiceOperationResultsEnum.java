/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum ServiceOperationResultsEnum implements EnumClass<Integer>{

    success(2),
    err(3),
    notwork(1);

    private Integer id;

    ServiceOperationResultsEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static ServiceOperationResultsEnum fromId(Integer id) {
        for (ServiceOperationResultsEnum at : ServiceOperationResultsEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}