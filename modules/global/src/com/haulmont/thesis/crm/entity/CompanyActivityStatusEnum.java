/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author d.ivanov
 */
public enum CompanyActivityStatusEnum implements EnumClass<Integer>{

    ACTIVE(1),
    LIQUIDATING(2),
    LIQUIDATED(3);

    private Integer id;

    CompanyActivityStatusEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CompanyActivityStatusEnum fromId(Integer id) {
        for (CompanyActivityStatusEnum at : CompanyActivityStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}