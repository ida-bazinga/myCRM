/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author d.ivanov
 */
public enum FacilityManagmentTypeEnum implements EnumClass<String> {

    TECHNICALSERVICE("technicalservice"),
    CONDUCTION("conduction"),
    INSTALLATION("installation"),
    DEINSTALLATION("deinstallation"),
    EARLYINSTALLATION("earlyinstallation"),
    LATEDEINSTALLATION("latedeinstallation");

    private String id;

    FacilityManagmentTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static FacilityManagmentTypeEnum fromId(String id) {
        for (FacilityManagmentTypeEnum at : FacilityManagmentTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}