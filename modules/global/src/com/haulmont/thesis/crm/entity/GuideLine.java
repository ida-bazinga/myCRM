/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.TsCard;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.thesis.core.entity.TaskPattern;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_GUIDE_LINE")
@Entity(name = "crm$GuideLine")
public class GuideLine extends BaseUuidEntity implements SoftDelete, Updatable {
    private static final long serialVersionUID = 1347054383350947199L;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CARD_ID")
    protected TsCard parentCard;

    @Column(name = "STATE")
    protected String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @Column(name = "DESCRIPTION", length = 1000)
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category;

    @Column(name = "THEME", length = 650)
    protected String theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INITIATOR_ID")
    protected ExtEmployee initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXECUTOR_ID")
    protected ExtEmployee executor;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    @Column(name = "INTERVAL_")
    protected Integer interval;

    @Column(name = "DURATION")
    protected Integer duration;

    @Column(name = "STARTING_POINT")
    protected String startingPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_TEMPLATE_ID")
    protected Doc docTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_TEMPLATE_ID")
    protected TaskPattern taskTemplate;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setTaskTemplate(TaskPattern taskTemplate) {
        this.taskTemplate = taskTemplate;
    }

    public TaskPattern getTaskTemplate() {
        return taskTemplate;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setParentCard(TsCard parentCard) {
        this.parentCard = parentCard;
    }

    public TsCard getParentCard() {
        return parentCard;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtProject getProject() {
        return project;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setInitiator(ExtEmployee initiator) {
        this.initiator = initiator;
    }

    public ExtEmployee getInitiator() {
        return initiator;
    }

    public void setExecutor(ExtEmployee executor) {
        this.executor = executor;
    }

    public ExtEmployee getExecutor() {
        return executor;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setStartingPoint(StartingPointEnum startingPoint) {
        this.startingPoint = startingPoint == null ? null : startingPoint.getId();
    }

    public StartingPointEnum getStartingPoint() {
        return startingPoint == null ? null : StartingPointEnum.fromId(startingPoint);
    }

    public void setDocTemplate(Doc docTemplate) {
        this.docTemplate = docTemplate;
    }

    public Doc getDocTemplate() {
        return docTemplate;
    }


    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }


    @Override
    public Boolean isDeleted() {
        return deleteTs != null;
    }

    @Override
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public String getDeletedBy() {
        return deletedBy;
    }

    @Override
    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    @Override
    public Date getDeleteTs() {
        return deleteTs;
    }


}