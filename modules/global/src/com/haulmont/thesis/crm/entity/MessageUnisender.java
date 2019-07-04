/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_MESSAGE_UNISENDER")
@Entity(name = "crm$MessageUnisender")
public class MessageUnisender extends BaseLookup {
    private static final long serialVersionUID = -3922277212062139094L;

    @Column(name = "LIST_ID", length = 50)
    protected String list_id;


    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getList_id() {
        return list_id;
    }

    public MessageUnisender(String subject, int unisender_id, int list_id) {
        this.name_ru = subject;
        this.code = String.valueOf(unisender_id);
        this.list_id = String.valueOf(list_id);
    }

}