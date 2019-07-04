
/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author a.donskoy
 */
public enum SocialNetworksTypeEnum implements EnumClass<Integer>{

    vk(1),
    ok(2),
    skype(3),
    instagram(4),
    facebook(5);

    private Integer id;

    SocialNetworksTypeEnum (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static SocialNetworksTypeEnum fromId(Integer id) {
        for (SocialNetworksTypeEnum at : SocialNetworksTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}