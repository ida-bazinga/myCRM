/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_RESOURCE_TYPE")
@Entity(name = "crm$ResourceType")
public class ResourceType extends BaseLookup {
    private static final long serialVersionUID = -1047562744695159703L;

    @Column(name = "KIND_RESOURCES")
    protected Integer kindResources;

    public void setKindResources(ResourceKindEnum kindResources) {
        this.kindResources = kindResources == null ? null : kindResources.getId();
    }

    public ResourceKindEnum getKindResources() {
        return kindResources == null ? null : ResourceKindEnum.fromId(kindResources);
    }


}