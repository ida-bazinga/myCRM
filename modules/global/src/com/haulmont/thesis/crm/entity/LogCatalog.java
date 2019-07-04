/*
 * Copyright (c) 2018 com.haulmont.thesis.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.thesis.crm.entity.ServiceOperationResultsEnum;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.openjpa.persistence.Persistent;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_LOG_CATALOG")
@Entity(name = "crm$LogCatalog")
public class LogCatalog extends BaseUuidEntity {
    private static final long serialVersionUID = 8183276853076605509L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Column(name = "SHORT_SERVICE_OPERATION_RESULTS")
    protected Integer shortServiceOperationResults;

    @Column(name = "ERROR", length = 4000)
    protected String error;

    @Persistent
    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Column(name = "EXT_ID")
    protected String extId;

    @Column(name = "ENTITY_NAME")
    protected String entityName;

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setShortServiceOperationResults(ServiceOperationResultsEnum shortServiceOperationResults) {
        this.shortServiceOperationResults = shortServiceOperationResults == null ? null : shortServiceOperationResults.getId();
    }

    public ServiceOperationResultsEnum getShortServiceOperationResults() {
        return shortServiceOperationResults == null ? null : ServiceOperationResultsEnum.fromId(shortServiceOperationResults);
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getExtId() {
        return extId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }


}