/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_PROJECT_FORMAT")
@Entity(name = "crm$ProjectFormat")
public class ProjectFormat extends BaseLookup {
    private static final long serialVersionUID = 5025121860264881334L;

}