/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;

import javax.persistence.*;

/**
 * @author Redcu
 */
@NamePattern("%s %s|address,campaign")
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("520")
@Table(name = "CRM_EMAIL_ACTIVITY")
@Entity(name = "crm$EmailActivity")
public class EmailActivity extends BaseActivity {
    private static final long serialVersionUID = -2779302659305036677L;














}