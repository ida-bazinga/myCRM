/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CONTACT_ROLE")
@Entity(name = "crm$ContactRole")
public class ContactRole extends BaseLookup {
    private static final long serialVersionUID = 4230205830928372405L;

    @JoinTable(name = "CRM_CONTACT_ROLES_CONTACT_PERSON_LINK",
        joinColumns = @JoinColumn(name = "CONTACT_ROLES_ID"),
        inverseJoinColumns = @JoinColumn(name = "CONTACT_PERSON_ID"))
    @ManyToMany
    protected List<ExtContactPerson> contactPersons;

    public void setContactPersons(List<ExtContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public List<ExtContactPerson> getContactPersons() {
        return contactPersons;
    }


}