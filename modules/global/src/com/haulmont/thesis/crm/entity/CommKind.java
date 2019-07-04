/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COMM_KIND")
@Entity(name = "crm$CommKind")
public class CommKind extends BaseLookup {
    private static final long serialVersionUID = 3384287769626218190L;

    @Column(name = "COMMUNICATION_TYPE", nullable = false)
    protected Integer communicationType;

    @Column(name = "LINK_TEMPLATE")
    protected String linkTemplate;

    public void setLinkTemplate(String linkTemplate) {
        this.linkTemplate = linkTemplate;
    }

    public String getLinkTemplate() {
        return linkTemplate;
    }


    public void setCommunicationType(CommunicationTypeEnum communicationType) {
        this.communicationType = communicationType == null ? null : communicationType.getId();
    }

    public CommunicationTypeEnum getCommunicationType() {
        return communicationType == null ? null : CommunicationTypeEnum.fromId(communicationType);
    }


}