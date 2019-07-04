package com.haulmont.thesis.crm.web.ui.task;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.core.entity.Task;
import com.haulmont.thesis.crm.entity.CategoryTask;
import com.haulmont.thesis.crm.entity.ExtTask;
import com.haulmont.thesis.crm.entity.Labour;
import com.haulmont.thesis.web.ui.task.TaskEditor;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.global.TimeUnit;
import com.haulmont.workflow.core.global.WfConstants;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

public class ExtTaskEditor<T extends  ExtTask> extends TaskEditor<T> {

    @Inject
    private DataManager dataManager;
    @Named("categoryTask")
    protected LookupField categoryTask;
    @Named("sumLabourHour")
    protected Label sumLabourHour;
    @Named("labourDs")
    protected CollectionDatasource<Labour, UUID> labourDs;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        sumLabourHour();
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        loadCategoryTask();
        initSumLabour();
    }


    @Override
    protected void createFinishDateCalculationListener() {
        //calculate finishDateTime when duration changes and clear duration when finishDateTime changes
        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                switch (property) {
                    case "duration":
                    case "timeUnit":
                        Task task = getItem();
                        if (task.getPrimaryTask() != null && !WfUtils.isCardInState(task.getPrimaryTask(), "Finished")) {
                            finishDateTimePlan.setValue(null);
                        }
                        Integer duration = task.getDuration();
                        TimeUnit timeUnit = task.getTimeUnit();
                        if (duration != null && timeUnit != null) {
                            ((T) task).setDurationHour(getLabourIntensityInHours(duration, timeUnit));
                        }
                        break;
                    case "finishDateTimePlan":
                        if (value == null) break;
                        if (isAutoFinishDate) {
                            isAutoFinishDate = false;
                            return;
                        }
                        break;
                    case "primaryTask":
                        Action startProcessAction = actionsFrame.getButtonAction(WfConstants.ACTION_START);
                        if (value != null) {
                            setExecutorDependentTask();
                            finishDateTimePlanDateField.setEnabled(false);
                            finishDateTimePlanDateField.setValue(null);
                            if (startProcessAction != null) startProcessAction.setVisible(false);
                        } else {
                            finishDateTimePlanDateField.setEnabled(true);
                            if (startProcessAction != null) startProcessAction.setVisible(true);
                        }
                        break;
                    case "taskType":
                        loadCategoryTask();
                        break;
                }
            }
        });
    }

    @Override
    protected void setComponentsVisibility() {

        durationLabel.setValue(getMessage("taskDuration"));
        /*
        if (!finishDateTimePlanDateField.isEditable()) {
            durationLabel.setVisible(false);
            durationBox.setVisible(false);
        }
        */

        if (task.getFinishDateTimeFact() != null) {
            finishDateTimeFactLabel.setVisible(true);
            finishDateTimeFactBox.setVisible(true);
        }

        /*
        if (durationTextField != null && durationTextField.getValue() != null &&
                BooleanUtils.isFalse(durationTextField.isEditable())) {
            timeUnit.setVisible(false);
        }
        */

        TabSheet.Tab taskDependentTab = tabsheet.getTab("taskDependentTab");
        LoadContext ctx = new LoadContext(Task.class);
        ctx.setView(View.MINIMAL).setUseSecurityConstraints(false);
        ctx.setQueryString("select t from tm$Task t where t.primaryTask.id = :task").setParameter("task", task).setMaxResults(1);
        List<Task> dependencies = dataService.loadList(ctx);
        taskDependentTab.setVisible(dependencies.size() > 0 || task.getPrimaryTask() != null);

        setDisplayCheckBoxes();

        if (runtimePropertiesFrameMain != null) {
            runtimePropertiesFrameMain.setCategoryFieldVisible(false);
            if (!cardIsEditable)
                runtimePropertiesFrameMain.setCategoryFieldEditable(false);
        }

        if (runtimePropertiesFrameTab != null) {
            runtimePropertiesFrameTab.setCategoryFieldVisible(false);
            if (!cardIsEditable)
                runtimePropertiesFrameTab.setCategoryFieldEditable(false);
        }
    }

    private CollectionDatasource<CategoryTask, UUID> categoryTaskDs() {
        String query = String.format("select e from crm$CategoryTask e where e.taskType.id = '%s'", task.getTaskType().getId());
        CollectionDatasource<CategoryTask, UUID> ds = new DsBuilder()
                .setViewName("edit")
                .setMetaClass(metadata.getSession().getClass(CategoryTask.class)).buildCollectionDatasource();
        ds.setQuery(query);
        ds.refresh();
        return ds;
    }

    protected void loadCategoryTask() {
        if (task.getTaskType() != null) {
            if (categoryTask.getOptionsDatasource() != null) {
                if (categoryTask.getValue() != null) {
                    categoryTask.setValue(null);
                }
                categoryTask.getOptionsDatasource().clear();
            }
            categoryTask.setOptionsDatasource(categoryTaskDs());
        }
    }

    @Override
    protected void initLazyTabs() {
        super.initLazyTabs();
        tabsheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                String tabName = newTab.getName();
                if ("labourTab".equals(tabName)) {
                    dateCalculationListener();
                    removeLabour();
                }
                if ("mainTab".equals(tabName)) {
                    sumLabourHour();
                }
            }
        });
    }

    protected void sumLabourHour() {
        sumHour(labourDs.getItems());
    }

    protected void sumHour(Collection<Labour> collection) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Labour labour:collection) {
            sum = sum.add(labour.getLabourHour());
        }
        sumLabourHour.setValue(sum);
    }

    protected void initSumLabour() {
        LoadContext loadContext = new LoadContext(Labour.class).setView("edit");
        loadContext.setQueryString("select e from crm$Labour e where e.task.id = :Id").setParameter("Id", task.getId());
        Collection<Labour> collection = dataManager.loadList(loadContext);
        sumHour(collection);
    }

    protected BigDecimal getLabourIntensityInHours(Integer labourIntensity, TimeUnit labourUnit) {
        BigDecimal labourHour = null;
        if (labourIntensity != null && labourUnit != null)
            if (labourUnit.equals(TimeUnit.DAY)) {
                labourHour = BigDecimal.valueOf(labourIntensity * workCalendarService.getWorkDayLengthInMillis() / 3600000.0)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (labourUnit.equals(TimeUnit.MINUTE))
                labourHour = BigDecimal.valueOf(labourIntensity / 60.0).setScale(2, BigDecimal.ROUND_HALF_UP);
            else
                labourHour = BigDecimal.valueOf(labourIntensity).setScale(2, BigDecimal.ROUND_HALF_UP);
        return labourHour;
    }

    @Override
    @SuppressWarnings("unused")
    protected void taskTypeChanged(T source, @Nullable final Object prevValue, @Nullable Object value) {
        if (prevValue != null && !prevValue.equals(value)) {
            doChangeTaskType();
        }
    }

    public void createLabour() {
        Labour labour = metadata.create(Labour.class);
        labour.setTask(((ExtTask) task));
        labour.setDuration(1);
        labour.setTimeUnit(TimeUnit.HOUR);
        labour.setLabourHour(BigDecimal.ONE);
        labour.setExecutionDate(Calendar.getInstance().getTime());
        labourDs.addItem(labour);
    }

    protected void dateCalculationListener() {
        labourDs.addListener(new DsListenerAdapter<Labour>() {
            @Override
            public void valueChanged(Labour source, String property, Object prevValue, Object value) {
                switch (property) {
                    case "duration":
                    case "timeUnit":
                        Labour labour = source;
                        if (labour.getDuration() != null && labour.getDuration() >= 0) {
                            Integer duration = labour.getDuration();
                            TimeUnit timeUnit = labour.getTimeUnit();

                            if (duration == null || timeUnit == null) {
                                return;
                            }

                            labour.setLabourHour(getLabourIntensityInHours(duration, timeUnit));
                        }
                        break;
                }
            }
        });
    }

    protected void removeLabour() {
        Table labourTable = getComponent("labourTable");
        RemoveAction action = ((RemoveAction) labourTable.getAction("remove"));
        action.setAutocommit(false);
    }
    

}