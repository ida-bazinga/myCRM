/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.thesis.crm.core.app.worker.ActivityWorker;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.BaseActivity;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_ActivityEntityListener")
public class ActivityEntityListener implements BeforeInsertEntityListener<BaseActivity>, BeforeUpdateEntityListener<BaseActivity> {

    @Inject
    protected Persistence persistence;
    @Inject
    protected ActivityWorker activityWorker;

    @Override
    public void onBeforeInsert(BaseActivity entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (CollectionUtils.containsAny(fields, Arrays.asList("kind", "createTime", "contactPerson", "owner"))) {
            entity.setDescription(activityWorker.createDescription(entity));
        }
    }


    @Override
    public void onBeforeUpdate(BaseActivity entity) {
        onBeforeInsert(entity);
    }
}