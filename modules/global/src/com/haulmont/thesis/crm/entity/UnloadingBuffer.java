/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import java.util.UUID;
import javax.persistence.Column;
import org.apache.openjpa.persistence.Persistent;
import javax.persistence.MappedSuperclass;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_UNLOADING_BUFFER")
@Entity(name = "crm$UnloadingBuffer")
public class UnloadingBuffer extends BaseUuidEntity {
    private static final long serialVersionUID = -5474821711828154984L;

    @Persistent
    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Persistent
    @Column(name = "UNLOADING_ID")
    protected UUID unloadingId;

    public void setUnloadingId(UUID unloadingId) {
        this.unloadingId = unloadingId;
    }

    public UUID getUnloadingId() {
        return unloadingId;
    }


    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }


}