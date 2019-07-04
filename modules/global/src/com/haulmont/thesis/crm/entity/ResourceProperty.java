/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author a.donskoy
 */
@NamePattern("%s|propName")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_RESOURCE_PROPERTY")
@Entity(name = "crm$ResourceProperty")
public class ResourceProperty extends BaseUuidEntity {
    private static final long serialVersionUID = -1009892010090954154L;

    @Column(name = "PROP_NAME", length = 500)
    protected String propName;

    @Column(name = "PROP_VALUE", length = 500)
    protected String propValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID")
    protected Resource resource;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }


    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public String getPropValue() {
        return propValue;
    }


}