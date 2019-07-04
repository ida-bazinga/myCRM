/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.Set;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_LINE_OF_BUSINESS")
@Entity(name = "crm$LineOfBusiness")
public class LineOfBusiness extends BaseLookup {
    private static final long serialVersionUID = -5721465580950391302L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_LINE_ID")
    protected LineOfBusiness parentLine;

    @Column(name = "IS_GROUP", nullable = false)
    protected Boolean isGroup = false;


    public void setParentLine(LineOfBusiness parentLine) {
        this.parentLine = parentLine;
    }

    public LineOfBusiness getParentLine() {
        return parentLine;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }



}