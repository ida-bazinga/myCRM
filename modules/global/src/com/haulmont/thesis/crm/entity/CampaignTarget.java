/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

/**
 * @author Kirill Khoroshilov
 */
@NamePattern("%s|company")
@MetaClass(name = "crm$CampaignTarget")
public class CampaignTarget extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 7033123483644830391L;

    protected static String REPLACEMENT_SYMBOL = "-";

    @MetaProperty
    protected ExtCompany company;

    @MetaProperty
    protected ExtContactPerson contactPerson;

    @MetaProperty
    protected Integer priority;

    @MetaProperty
    protected Communication communication;

    @MetaProperty
    protected String description;

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public Communication getCommunication() {
        return communication;
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setContactPerson(ExtContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public ExtContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @MetaProperty(related ="contactPerson" )
    public String getContactPersonString(){
        return getContactPerson() != null ? getContactPerson().getFullName(): REPLACEMENT_SYMBOL;
    }

    @MetaProperty(related ="communication" )
    public String getCommunicationString(){
        return getCommunication() != null ? getCommunication().getMaskedAddress() : REPLACEMENT_SYMBOL;
    }
}