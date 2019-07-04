/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.crm.enums.ActivityResTypeEnum;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Kirill Khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ACTIVITY_RES")
@Entity(name = "crm$ActivityRes")
public class ActivityRes extends BaseLookup {
    private static final long serialVersionUID = 4028839391896141937L;

    @Column(name = "IS_NEED_DETAILS", nullable = false)
    protected Boolean isNeedDetails = false;

    @Column(name = "RESULT_TYPE", nullable = false)
    protected String resultType;

    @JoinTable(name = "CRM_CAMPAIGN_TYPE_ACTIVITY_RESULT_LINK",
            joinColumns = @JoinColumn(name = "ACTIVITY_RESULT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CAMPAIGN_KIND_ID"))
    @ManyToMany
    protected Set<CampaignKind> campaignKinds;

    @Column(name = "USE_ALL_KINDS", nullable = false)
    protected Boolean useAllKinds = true;

    @Column(name = "ENTITY_TYPE", length = 50)
    protected String entityType;

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setUseAllKinds(Boolean useAllKinds) {
        this.useAllKinds = useAllKinds;
    }

    public Boolean getUseAllKinds() {
        return useAllKinds;
    }

    public void setCampaignKinds(Set<CampaignKind> campaignKinds) {
        this.campaignKinds = campaignKinds;
    }

    public Set<CampaignKind> getCampaignKinds() {
        return campaignKinds;
    }

    public void setIsNeedDetails(Boolean isNeedDetails) {
        this.isNeedDetails = isNeedDetails;
    }

    public Boolean getIsNeedDetails() {
        return isNeedDetails;
    }

    public void setResultType(ActivityResTypeEnum resultType) {
        this.resultType = resultType == null ? null : resultType.getId();
    }

    public ActivityResTypeEnum getResultType() {
        return resultType == null ? null : ActivityResTypeEnum.fromId(resultType);
    }

    @MetaProperty(related = "entityType")
    public String getEntityTypeName() {
        if (StringUtils.isNotBlank(entityType)){
            MetaClass metaClass = AppBeans.get(Metadata.class).getClassNN(entityType);
            return AppBeans.get(Messages.class).getTools().getEntityCaption(metaClass);
        }
        return "";
    }
}