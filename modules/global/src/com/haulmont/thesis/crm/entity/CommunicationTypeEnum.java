/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum CommunicationTypeEnum implements EnumClass<Integer>{

    phone(0),
    email(1),
    im(2),
    snet(3),
    site(4);

    private Integer id;

    CommunicationTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CommunicationTypeEnum fromId(Integer id) {
        for (CommunicationTypeEnum at : CommunicationTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}