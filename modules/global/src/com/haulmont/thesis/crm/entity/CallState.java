/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author k.khoroshilov
 */
public enum CallState implements EnumClass<Integer>{

    Undefined(0),
    Ringing(1),
    Dialing(2),
    Connected(3),
    WaitingForNewParty(4),
    TryingToTransfer(5),
    Ended(6);

    private Integer id;

    CallState (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static CallState fromId(Integer id) {
        for (CallState at : CallState.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}