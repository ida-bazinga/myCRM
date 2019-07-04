/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Column;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamePattern("%s|name_ru")
@Table(name = "CRM_EQUIPMENT_STATE")
@Entity(name = "crm$EquipmentState")
public class EquipmentState extends BaseLookup {
    private static final long serialVersionUID = 1518936034217070931L;




}