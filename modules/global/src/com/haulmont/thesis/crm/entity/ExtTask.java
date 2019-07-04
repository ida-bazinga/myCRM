/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.thesis.core.entity.Task;

import javax.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author k.khoroshilov
 */
@Extends(Task.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("1")
@Entity(name = "crm$Task")
public class ExtTask extends Task {
    private static final long serialVersionUID = -956850249835315954L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_COMPANY_ID")
    protected ExtCompany extCompany;

    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_PROJECT_ID")
    protected ExtProject extProject;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE_TIME")
    protected Date startDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_TASK_ID")
    protected CategoryTask categoryTask;

    @Column(name = "DURATION_HOUR", precision = 7, scale = 2)
    protected BigDecimal durationHour;

    public void setDurationHour(BigDecimal durationHour) {
        this.durationHour = durationHour;
    }

    public BigDecimal getDurationHour() {
        return durationHour;
    }


    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setCategoryTask(CategoryTask categoryTask) {
        this.categoryTask = categoryTask;
    }

    public CategoryTask getCategoryTask() {
        return categoryTask;
    }


    public void setExtCompany(ExtCompany extCompany) {
        this.extCompany = extCompany;
    }

    public ExtCompany getExtCompany() {
        return extCompany;
    }

    @Deprecated
    public void setExtProject(ExtProject extProject) {
        this.extProject = extProject;
    }

    @Deprecated
    public ExtProject getExtProject() {
        return extProject;
    }






}