/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import com.haulmont.workflow.core.global.TimeUnit;
import java.math.BigDecimal;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import java.util.Date;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author d.ivanov
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_LABOUR")
@Entity(name = "crm$Labour")
public class Labour extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = -4124250218290875533L;

    @Column(name = "DURATION", nullable = false)
    protected Integer duration;

    @Column(name = "TIME_UNIT", nullable = false)
    protected String timeUnit;

    @Column(name = "LABOUR_HOUR", nullable = false, precision = 7, scale = 2)
    protected BigDecimal labourHour;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TASK_ID")
    protected ExtTask task;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXECUTION_DATE", nullable = false)
    protected Date executionDate;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Date getExecutionDate() {
        return executionDate;
    }


    public void setTask(ExtTask task) {
        this.task = task;
    }

    public ExtTask getTask() {
        return task;
    }


    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }


    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit == null ? null : timeUnit.getId();
    }

    public TimeUnit getTimeUnit() {
        return timeUnit == null ? null : TimeUnit.fromId(timeUnit);
    }

    public void setLabourHour(BigDecimal labourHour) {
        this.labourHour = labourHour;
    }

    public BigDecimal getLabourHour() {
        return labourHour;
    }


}