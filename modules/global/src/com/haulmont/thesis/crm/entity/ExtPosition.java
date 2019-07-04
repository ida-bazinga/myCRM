/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.Column;
import com.haulmont.thesis.core.entity.Position;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author a.donskoy
 */
@NamePattern("%s|name")
@Extends(Position.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("P")
@Entity(name = "crm$Position")
public class ExtPosition extends Position {
    private static final long serialVersionUID = 6207011227088737088L;

    @Column(name = "GEN_NAME", length = 400)
    protected String genName;

    @Column(name = "DAT_NAME", length = 400)
    protected String datName;

    @Column(name = "LATIN", length = 400)
    protected String latin;

    @Column(name = "IS_DEPT_CHIEF")
    protected Boolean isDeptChief;

    public void setIsDeptChief(Boolean isDeptChief) {
        this.isDeptChief = isDeptChief;
    }

    public Boolean getIsDeptChief() {
        return isDeptChief;
    }


    public void setLatin(String latin) {
        this.latin = latin;
    }

    public String getLatin() {
        return latin;
    }


    public void setGenName(String genName) {
        this.genName = genName;
    }

    public String getGenName() {
        return genName;
    }

    public void setDatName(String datName) {
        this.datName = datName;
    }

    public String getDatName() {
        return datName;
    }


}