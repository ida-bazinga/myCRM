/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COMPANY_SCALE")
@Entity(name = "crm$CompanyScale")
public class CompanyScale extends BaseLookup {
    private static final long serialVersionUID = 5096865379618982654L;

}