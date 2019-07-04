/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum CompanyTypeEnum implements EnumClass<String>{

    legal("LEGAL"),
    nonResident("NONRESIDENT"),
    branch("BRANCH"),
    person("PERSON"),
    government("GOVERNMENT");

    private String id;

    CompanyTypeEnum (String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static CompanyTypeEnum fromId(String id) {
        for (CompanyTypeEnum at : CompanyTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}