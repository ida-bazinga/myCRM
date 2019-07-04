/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import org.apache.openjpa.persistence.Persistent;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author d.ivanov
 */
@NamePattern("%s|entityName")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_INTEGRATION_RESOLVER", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_CRM_INTEGRATION_RESOLVER_UNQ", columnNames = {"ENTITY_ID", "ENTITY_NAME", "EXT_SYSTEM"})
})
@Entity(name = "crm$IntegrationResolver")
public class IntegrationResolver extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = 1483775723512315254L;

    @Persistent
    @Column(name = "ENTITY_ID", nullable = false)
    protected UUID entityId;

    @Column(name = "EXT_ID")
    protected String extId;

    @Column(name = "ENTITY_NAME", nullable = false)
    protected String entityName;





    @Column(name = "POSTED", nullable = false)
    protected Boolean posted = false;


    @Column(name = "SUM_PAYMENT", precision = 15, scale = 2)
    protected BigDecimal sumPayment;

    @Column(name = "DEL", nullable = false)
    protected Boolean del = false;

    @Column(name = "STATE_DOC_SALES", nullable = false)
    protected String stateDocSales;

    @Column(name = "EXT_SYSTEM", nullable = false)
    protected String extSystem;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;


    public void setExtSystem(ExtSystem extSystem) {
        this.extSystem = extSystem == null ? null : extSystem.getId();
    }

    public ExtSystem getExtSystem() {
        return extSystem == null ? null : ExtSystem.fromId(extSystem);
    }


    public void setStateDocSales(StatusDocSales stateDocSales) {
        this.stateDocSales = stateDocSales == null ? null : stateDocSales.getId();
    }

    public StatusDocSales getStateDocSales() {
        return stateDocSales == null ? null : StatusDocSales.fromId(stateDocSales);
    }


    public void setSumPayment(BigDecimal sumPayment) {
        this.sumPayment = sumPayment;
    }

    public BigDecimal getSumPayment() {
        return sumPayment;
    }


    public void setDel(Boolean del) {
        this.del = del;
    }

    public Boolean getDel() {
        return del;
    }


    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public Date getUpdateTs() {
        return updateTs;
    }



    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public Boolean getPosted() {
        return posted;
    }







    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
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


}