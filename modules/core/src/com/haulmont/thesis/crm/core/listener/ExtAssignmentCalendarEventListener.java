/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.core.enums.TaskState;
import com.haulmont.thesis.core.listener.AssignmentCalendarEventListener;
import com.haulmont.thesis.crm.entity.ExtTask;
import com.haulmont.workflow.core.entity.Assignment;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;


public class ExtAssignmentCalendarEventListener extends AssignmentCalendarEventListener {

    @Inject
    protected DataManager dataManager;

    @Override
    public void onBeforeInsert(Assignment entity) {
        if (!addEntityToCache(entity))
            return;

        if (TaskState.DelayedStart.getId().replace(",", "").equals(entity.getName()))
            return;

        if (entity.getCard() != null && entity.getUser() != null && entity.getFinished() == null) {
            Date startDateTime = getStartTime(entity);
            calendarWorker.createCalendarEventItem(entity.getCard(), entity.getUser(), startDateTime,
                    entity.getDueDate(), entity, false);
        }

    }

    @Override
    public void onBeforeUpdate(Assignment entity) {
        if (!addEntityToCache(entity))
            return;

        if (TaskState.DelayedStart.getId().replace(",", "").equals(entity.getName()))
            return;

        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (fields.contains("finished")) {
            calendarWorker.removeCalendarEventItem(entity.getCard(), entity.getId());
        } else if (!Collections.disjoint(fields, Arrays.asList("user", "card", "createTs", "dueDate"))) {
            calendarWorker.removeCalendarEventItem(entity.getCard(), entity.getId());

            if (entity.getCard() != null && entity.getUser() != null && entity.getFinished() == null) {
                Date startDateTime = getStartTime(entity);
                calendarWorker.createCalendarEventItem(entity.getCard(), entity.getUser(), startDateTime,
                        entity.getDueDate(), entity, false);
            }
        }
    }

    @Authenticated
    @Nullable
    protected Date getStartTime(Assignment entity) {
        LoadContext loadContext = new LoadContext(ExtTask.class).setView("_local").setId(entity.getCard().getId());

        ExtTask task = dataManager.load(loadContext);

        if (task != null && task.getStartDateTime() != null) {
            return task.getStartDateTime();
        }

        if (entity.getDueDate() != null){
            GregorianCalendar startDateCalendar = new GregorianCalendar();
            startDateCalendar.setTime(entity.getDueDate());
            startDateCalendar.add(Calendar.MINUTE, -30);
            return startDateCalendar.getTime();
        } else {
            return null;
        }
    }

}
