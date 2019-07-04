/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.listener.BeforeDetachEntityListener;
import com.haulmont.thesis.crm.entity.Activity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author k.khoroshilov
 */
@ManagedBean("crm_ActivityListener")
public class ActivityListener implements BeforeDetachEntityListener<Activity>, BeforeInsertEntityListener<Activity> {

    @Inject
    protected Persistence persistence;
    @Inject
    protected Messages messages;

    @Override
    public void onBeforeDetach(Activity entity, EntityManager entityManager) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YY hh:mm");

        StringBuilder title = new StringBuilder();

        if (entity.getDirection() != null) title.append(messages.getMessage(entity.getDirection()));

        if (entity.getCompany() != null) {
            title.append(", ");
            title.append(entity.getCompany().getName());
        }

        if (entity.getCreateTs() != null) {
            title.append(", ");
            title.append(sdf.format(entity.getCreateTs()));
        } else {
            title.append(", ");
            title.append(sdf.format(new Date()));
        }

        if (entity.getOperatorSession() != null && entity.getOperatorSession().getOperator() != null) {
            title.append(", ");
            title.append(entity.getOperatorSession().getOperator().getCode());
        }

        if (entity.getOwner() != null) {
            title.append(", ");
            title.append(entity.getOwner().getName());
        }

        entity.setName(title.toString());

        if (entity.getConnectionStartTime() != null) {
            entity.setPreparationSeconds(new Date(entity.getConnectionStartTime().getTime() - entity.getCreateTs().getTime()));
        }else{
            entity.setPreparationSeconds(new Date(entity.getCreateTs().getTime() - entity.getCreateTs().getTime()));
        }

        if (entity.getConnectionStartTime() != null && entity.getConnectionEndTime() != null) {
            entity.setConnectingSeconds(new Date(entity.getConnectionEndTime().getTime() - entity.getConnectionStartTime().getTime()));
        }else{
            entity.setConnectingSeconds(new Date(entity.getCreateTs().getTime() - entity.getCreateTs().getTime()));
        }

        if (entity.getConnectionEndTime() != null) {
            entity.setEditingSeconds(new Date(entity.getEndTime().getTime() - entity.getConnectionEndTime().getTime()));
        }else{
            entity.setEditingSeconds(new Date(entity.getCreateTs().getTime() - entity.getCreateTs().getTime()));
        }

        entity.setTotalSeconds(new Date(entity.getEndTime().getTime() - entity.getCreateTs().getTime()));
    }

    @Override
    public void onBeforeInsert(Activity entity) {
        entity.setEndTime(AppBeans.get(TimeSource.class).currentTimestamp());
    }
}