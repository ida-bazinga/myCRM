/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.workflow.core.entity.Attachment;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import javax.persistence.Column;

/**
 * @author a.donskoy
 */
@Listeners("crm_ResourceAttacmentEntityListener")
@NamePattern("%s|name")
@DiscriminatorValue("R")
@Entity(name = "crm$ResourceAttachment")
public class ResourceAttachment extends Attachment {
    private static final long serialVersionUID = -1845185214426586982L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_ID")
    protected Resource resource;


    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }




}