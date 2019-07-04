/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum CostTypeEnum implements EnumClass<Integer>{

    base(1),
    project(2);

    private Integer id;

    CostTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CostTypeEnum fromId(Integer id) {
        for (CostTypeEnum at : CostTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}