/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.web.ui.task;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.thesis.core.entity.Task;
import com.haulmont.thesis.crm.entity.ExtTask;
import com.haulmont.thesis.crm.entity.Labour;
import com.haulmont.thesis.web.ui.task.TaskResolutionForm;
import com.haulmont.workflow.core.global.TimeUnit;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;

/**
 * @author d.ivanov
 */
public class ExtTaskResolutionForm extends TaskResolutionForm {

    @Override
    protected void onWindowCommit() {
        String errorMsg = getErrorMsg();
        if (!StringUtils.isBlank(errorMsg)) {
            showErrorNotification(errorMsg);
        } else {
            Task task = (Task) cardDs.getItem();
            task.setLabourIntensity(labourIntensity.getValue() != null ? Integer.parseInt((String) labourIntensity.getValue()) : null);
            Integer li = task.getLabourIntensity();
            TimeUnit lu = task.getLabourUnit();
            task.setLabourHour(getLabourIntensityInHours(li, lu));
            commitAttachments();
            if (li != null) {
                Labour labour = createLabour(task);
                commitLabour(labour);
            }
            close(COMMIT_ACTION_ID);
        }
    }

    public Labour createLabour(Task task) {
        Labour labour = metadata.create(Labour.class);
        labour.setTask(((ExtTask) task));
        labour.setDuration(task.getLabourIntensity());
        labour.setTimeUnit(task.getLabourUnit());
        labour.setLabourHour(task.getLabourHour());
        labour.setExecutionDate(Calendar.getInstance().getTime());
        return labour;
    }

    public void commitLabour(Labour labour) {
        CommitContext commitContext = new CommitContext();
        commitContext.getCommitInstances().add(labour);
        getDsContext().getDataSupplier().commit(commitContext);
        getDsContext().commit();
    }

}
