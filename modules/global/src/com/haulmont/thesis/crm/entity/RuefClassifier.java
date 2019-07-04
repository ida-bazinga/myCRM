/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;

/**
 * @author p.chizhikov
 */
@NamePattern("[%s] %s|code,name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_RUEF_CLASSIFIER")
@Entity(name = "crm$RuefClassifier")
public class RuefClassifier extends BaseLookup {
    private static final long serialVersionUID = -2369505471905447372L;

    @Column(name = "FULL_NAME", length = 1000)
    protected String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_RUEF_CLASSIFIER_ID")
    protected RuefClassifier parentRuefClassifier;
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }



    public void setParentRuefClassifier(RuefClassifier parentRuefClassifier) {
        this.parentRuefClassifier = parentRuefClassifier;
    }

    public RuefClassifier getParentRuefClassifier() {
        return parentRuefClassifier;
    }


    public String getFullName() {
        return String.format("[%s] %s", code, name_ru);
    }


}