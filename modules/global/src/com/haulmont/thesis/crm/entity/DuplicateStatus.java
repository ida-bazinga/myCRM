/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author d.ivanov
 */
public enum DuplicateStatus implements EnumClass<Integer>{

    original(1),
    merge(2),
    notduplicate(3);

    private Integer id;

    DuplicateStatus (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static DuplicateStatus fromId(Integer id) {
        for (DuplicateStatus at : DuplicateStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}