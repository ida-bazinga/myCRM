/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum ProjectRoomVariantEnum implements EnumClass<Integer>{

    variant1(1),
    variant2(2),
    variant3(3),
    variant4(4),
    variant5(5),
    variant6(6),
    variant7(7),
    variant8(8),
    variant9(9),
    variant10(10);

    private Integer id;

    ProjectRoomVariantEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static ProjectRoomVariantEnum fromId(Integer id) {
        for (ProjectRoomVariantEnum at : ProjectRoomVariantEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}