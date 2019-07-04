/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.listener
 */
package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.Resource;
import com.haulmont.thesis.crm.entity.ResourceAttachment;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

/**
 * @author a.donskoy
 */
@ManagedBean("crm_ResourceAttacmentEntityListener")
public class ResourceAttacmentEntityListener implements BeforeDeleteEntityListener<ResourceAttachment>, BeforeInsertEntityListener<ResourceAttachment> {
    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeDelete(ResourceAttachment entity) {
        /**
         * When ResourceAttacment entity is deleted, it is first merged, so
         * кesource reference will be in managed state - no need to reload it.
         */
        Resource resource = entity.getResource();
        resource.getResourceAttachments().remove(entity);
        resource.setHasAttachments(!resource.getResourceAttachments().isEmpty());
    }


    @Override
    public void onBeforeInsert(ResourceAttachment entity) {
        /**
         * Correspondent reference in ResourceAttacment entity is a detached object, in all
         * situations, except new Resource creation. That's why need to reload it.
         */
        Resource c = persistence.getEntityManager().find(Resource.class, entity.getResource().getId());
        if (c != null) {
            c.setHasAttachments(true);
        }
    }


}