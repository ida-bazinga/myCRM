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
@Table(name = "CRM_TERRITORIAL_CLASSIFIER")
@Entity(name = "crm$TerritorialClassifier")
public class TerritorialClassifier extends BaseLookup {
    private static final long serialVersionUID = 2556353133263660105L;

}