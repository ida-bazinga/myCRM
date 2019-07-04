/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.cuba.core.entity.BaseUuidEntity;
import org.apache.openjpa.persistence.Persistent;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author p.chizhikov
 */
@NamePattern("%s %s|id,entityName")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_LOG1_C")
@Entity(name = "crm$Log1C")
public class Log1C extends BaseUuidEntity {
    private static final long serialVersionUID = 3111979390984195384L;

    @Persistent
    @Column(name = "ENTITY_ID", nullable = false)
    protected UUID entityId;

    @Column(name = "ENTITY_NAME", nullable = false)
    protected String entityName;

    @Column(name = "SHORT_SERVICE_OPERATION_RESULTS")
    protected Integer shortServiceOperationResults;

    @Column(name = "EXT_ID")
    protected String extId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Lob
    @Column(name = "ERROR")
    protected String error;

    @Column(name = "PRIORITY")
    protected Integer priority;

    @Column(name = "EXT_SYSTEM", nullable = false)
    protected String extSystem;

    public void setExtSystem(ExtSystem extSystem) {
        this.extSystem = extSystem == null ? null : extSystem.getId();
    }

    public ExtSystem getExtSystem() {
        return extSystem == null ? null : ExtSystem.fromId(extSystem);
    }


    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getExtId() {
        return extId;
    }

    public void setShortServiceOperationResults(ServiceOperationResultsEnum shortServiceOperationResults) {
        this.shortServiceOperationResults = shortServiceOperationResults == null ? null : shortServiceOperationResults.getId();
    }

    public ServiceOperationResultsEnum getShortServiceOperationResults() {
        return shortServiceOperationResults == null ? null : ServiceOperationResultsEnum.fromId(shortServiceOperationResults);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }


}