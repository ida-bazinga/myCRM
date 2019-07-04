/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author p.chizhikov
 */
public enum DocDependenceEnum implements EnumClass<Integer>{

    order(1),
    invoice(2),
    act(3),
    vatInvoice(4);

    private Integer id;

    DocDependenceEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static DocDependenceEnum fromId(Integer id) {
        for (DocDependenceEnum at : DocDependenceEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}