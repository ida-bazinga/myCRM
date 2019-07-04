/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.annotation.PostConstruct;
import javax.persistence.*;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.thesis.core.entity.*;
import com.haulmont.thesis.crm.enums.CampaignState;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Locale;

/**
 * @author Kirill Khoroshilov
 */
@NamePattern("%s|name")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "ID")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "CRM_BASE_CAMPAIGN")
@Entity(name = "crm$BaseCampaign")
public abstract class BaseCampaign extends TsCard implements BelongOrganization, HasDetailedDescription {
    private static final long serialVersionUID = 1450123845861673606L;

    @Column(name = "CARD_NUMBER", nullable = false, length = 50)
    protected String number= "";

    @Temporal(TemporalType.DATE)
    @Column(name = "CARD_DATE", nullable = false)
    protected Date date;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "PRIORITY")
    protected String priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "KIND_ID")
    protected CampaignKind kind;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME_PLAN")
    protected Date startTimePlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME_PLAN")
    protected Date endTimePlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME_FACT")
    protected Date endTimeFact;

    @Column(name = "CARD_COMMENT", length = 2000)
    protected String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected ExtEmployee owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    protected Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @Column(name = "CONTENT", length = 4000)
    protected String content;


    public ExtProject getProject() {
        return project;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ExtEmployee getOwner() {
        return owner;
    }

    public void setOwner(ExtEmployee owner) {
        this.owner = owner;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setEndTimeFact(Date endTimeFact) {
        this.endTimeFact = endTimeFact;
    }

    public Date getEndTimeFact() {
        return endTimeFact;
    }

    public void setEndTimePlan(Date endTimePlan) {
        this.endTimePlan = endTimePlan;
    }

    public Date getEndTimePlan() {
        return endTimePlan;
    }

    public void setStartTimePlan(Date startTimePlan) {
        this.startTimePlan = startTimePlan;
    }

    public Date getStartTimePlan() {
        return startTimePlan;
    }

    public CampaignKind getKind() {
        return kind;
    }

    public void setKind(CampaignKind kind) {
        this.kind = kind;
        this.category = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority == null ? null : priority.getId();
    }

    public PriorityEnum getPriority() {
        return priority == null ? null : PriorityEnum.fromId(priority);
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    @Override
    public void setCategory(Category category) {
        if ((category != null) && !(category instanceof CampaignKind))
            throw new IllegalArgumentException("Category of campaign must be instance of CampaignKind!");
        super.setCategory(category);
        this.kind = (CampaignKind) category;
    }

    @PostConstruct
    protected void init() {
        setState(CampaignState.New.getId());
    }

    // TODO: 24.03.2018 To service
    @Override
    public String getDetailedDescription(Locale locale, boolean includeState, boolean includeShortInfo) {
        StringBuilder result = new StringBuilder();
        String locState = getLocState();
        //String locState = getLocState(locale);

        if (StringUtils.isNotBlank(getDescription())){
            result.append(getDescription())
                    .append(" ");
        }

        if (includeState && StringUtils.isNotBlank(locState)){
            result.append("[")
                    .append(locState)
                    .append("]")
                    .append(" ");
        }

        if (includeShortInfo && getProject() != null) {
            int infoLength = MAX_SUBJECT_LENGTH - result.length();

            if (infoLength > MIN_SHORT_INFO_LENGTH) {
                String shortInfo = StringUtils.defaultIfBlank(getProject().getName(), "");

                result.append(" ")
                        .append(StringUtils.abbreviate(shortInfo, infoLength))
                        .append("-");
            }
        }
        return result.toString().trim();
    }

    @Override
    public String getLocState() {
        if (getState() == null)
            return super.getLocState();

        return AppBeans.get(Messages.class).getMessage(CampaignState.fromId(getState()));
    }

    @Override
    public String toString() {
        return getInstanceName() + " [" + super.toString() + "]";
    }
}