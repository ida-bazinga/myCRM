/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum ProjectRoomStatusEnum implements EnumClass<Integer>{

    approved(1),
    approvalPending(2),
    cancelled(3),
    newApprovableItem(4);

    private Integer id;

    ProjectRoomStatusEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static ProjectRoomStatusEnum fromId(Integer id) {
        for (ProjectRoomStatusEnum at : ProjectRoomStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}