/*
 * Copyright (c) 2016 com.haulmont.thesis.core.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.HasDetailedDescription;
import com.haulmont.thesis.core.enums.MorphologyCase;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import com.haulmont.thesis.core.global.EntityCopyUtils;

@Deprecated
@Listeners("thesis_DocEntityListener")
@NamePattern("%s|theme")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("5100")
@Table(name = "CRM_OUTBOUND_CAMPAIGN")
@Entity(name = "crm$OutboundCampaign")
@EnableRestore
@TrackEditScreenHistory
public class OutboundCampaign extends Doc implements HasDetailedDescription{
    private static final long serialVersionUID = 8003010452317208188L;

    @Column(name = "PRIORITY")
    protected String priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected CallCampaignStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    protected Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    protected Date endTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME_FACT")
    protected Date endTimeFact;

    @Column(name = "MAX_ATTEMPT_COUNT")
    protected Integer maxAttemptCount;

    @Column(name = "MAX_SIZE_OF_QUEUE")
    protected Integer maxSizeOfQueue;

    @Column(name = "MIN_SIZE_OF_QUEUE")
    protected Integer minSizeOfQueue;

    @Column(name = "CALL_MODE")
    protected String callMode;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "outboundCampaign")
    protected List<CallCampaignTarget> targets;

    @OnDelete(DeletePolicy.CASCADE)
    @JoinTable(name = "CRM_CAMPAIGN_OPERATOR_LINK",
        joinColumns = @JoinColumn(name = "CAMPAIGN_ID"),
        inverseJoinColumns = @JoinColumn(name = "OPERATOR_ID"))
    @ManyToMany
    protected List<Operator> operators;

    @Column(name = "NOTES", length = 2000)
    protected String notes;
    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }







    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }




    public void setTargets(List<CallCampaignTarget> targets) {
        this.targets = targets;
    }

    public List<CallCampaignTarget> getTargets() {
        return targets;
    }


    public PriorityEnum getPriority() {
        return priority == null ? null : PriorityEnum.fromId(priority);
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority == null ? null : priority.getId();
    }


    public void setCallMode(OutboundCampaignTypeEnum callMode) {
        this.callMode = callMode == null ? null : callMode.getId();
    }

    public OutboundCampaignTypeEnum getCallMode() {
        return callMode == null ? null : OutboundCampaignTypeEnum.fromId(callMode);
    }


    public void setMaxAttemptCount(Integer maxAttemptCount) {
        this.maxAttemptCount = maxAttemptCount;
    }

    public Integer getMaxAttemptCount() {
        return maxAttemptCount;
    }

    public void setMaxSizeOfQueue(Integer maxSizeOfQueue) {
        this.maxSizeOfQueue = maxSizeOfQueue;
    }

    public Integer getMaxSizeOfQueue() {
        return maxSizeOfQueue;
    }

    public void setMinSizeOfQueue(Integer minSizeOfQueue) {
        this.minSizeOfQueue = minSizeOfQueue;
    }

    public Integer getMinSizeOfQueue() {
        return minSizeOfQueue;
    }


    public void setStatus(CallCampaignStatus status) {
        this.status = status;
    }

    public CallCampaignStatus getStatus() {
        return status;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTimeFact(Date endTimeFact) {
        this.endTimeFact = endTimeFact;
    }

    public Date getEndTimeFact() {
        return endTimeFact;
    }



    @Override
    public void copyFrom(Doc srcDoc, Set<com.haulmont.cuba.core.entity.Entity> toCommit, boolean copySignatures,
                         boolean onlyLastAttachmentsVersion, boolean useOriginalAttachmentCreatorAndCreateTs, boolean copyAllVersionMainAttachment) {
        super.copyFrom(srcDoc, toCommit, copySignatures, onlyLastAttachmentsVersion, useOriginalAttachmentCreatorAndCreateTs, copyAllVersionMainAttachment);
        Metadata metadata = AppBeans.get(Metadata.NAME);
        MetaClass metaClass = metadata.getClassNN(getClass());
        for (MetaProperty metaProperty : metaClass.getOwnProperties()) {
            setValue(metaProperty.getName(), srcDoc.getValue(metaProperty.getName()));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String getDetailedDescription(Locale locale, boolean includeState, boolean includeShortInfo) {
        Messages messages = AppBeans.get(Messages.NAME);
        String dateFormat = Datatypes.getFormatStrings(locale).getDateFormat();
        String description;
        String locState = getLocState(locale);

        description =
                getDocKind().getName() + " "
                        + (StringUtils.isBlank(getNumber()) ? "" : messages.getMessage(Doc.class, "notification.number", locale) + " " + getNumber() + " ")
                        + (getDate() == null ? "" : messages.getMessage(Doc.class, "notification.from", locale) + " "
                        + new SimpleDateFormat(dateFormat).format(getDate()))
                        + (includeState && StringUtils.isNotBlank(locState) ? " [" + locState + "]" : "");


        if (includeShortInfo && (StringUtils.isNotBlank(getTheme()) || StringUtils.isNotBlank(getComment()))) {
            int length = description.length();
            int infoLength = MAX_SUBJECT_LENGTH - length;

            if (infoLength < MIN_SHORT_INFO_LENGTH) return description;

            String shortInfo = StringUtils.defaultIfBlank(getTheme(), getComment());
            return description + " - " + StringUtils.abbreviate(shortInfo, infoLength) + " - ";
        } else {
            return description;
        }
    }

    
}