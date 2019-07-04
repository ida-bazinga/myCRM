/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Column;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.thesis.core.entity.DocCategory;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_ACTIVITY_RESULT")
@Entity(name = "crm$ActivityResult")
public class ActivityResult extends BaseLookup {
    private static final long serialVersionUID = 3162401281025214440L;

    @Column(name = "IS_NEED_DETAILS", nullable = false)
    protected Boolean isNeedDetails = false;

    @Column(name = "RESULT_TYPE", nullable = false)
    protected Integer resultType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_CATEGORY_ID")
    protected DocCategory docCategory;

    public ActivityResultTypeEnum getResultType() {
        return resultType == null ? null : ActivityResultTypeEnum.fromId(resultType);
    }

    public void setResultType(ActivityResultTypeEnum resultType) {
        this.resultType = resultType == null ? null : resultType.getId();
    }

    public void setDocCategory(DocCategory docCategory) {
        this.docCategory = docCategory;
    }

    public DocCategory getDocCategory() {
        return docCategory;
    }

    public void setIsNeedDetails(Boolean isNeedDetails) {
        this.isNeedDetails = isNeedDetails;
    }

    public Boolean getIsNeedDetails() {
        return isNeedDetails;
    }











}