/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum CompanySubdivisionEnum implements EnumClass<Integer>{

    Sector(0),
    Department(1),
    Direction(2),
    Administration(3);

    private Integer id;

    CompanySubdivisionEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CompanySubdivisionEnum fromId(Integer id) {
        for (CompanySubdivisionEnum at : CompanySubdivisionEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}