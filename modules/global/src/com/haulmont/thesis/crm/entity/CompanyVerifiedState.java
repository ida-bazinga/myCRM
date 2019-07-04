/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Column;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.thesis.core.entity.Correspondent;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_COMPANY_VERIFIED_STATE")
@Entity(name = "crm$CompanyVerifiedState")
public class CompanyVerifiedState extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = 6088639621872830338L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CORRESPONDENT_ID")
    protected Correspondent correspondent;



    @Column(name = "BY_USER")
    protected String byUser;

    @Column(name = "STATE")
    protected Integer state;


    @Column(name = "COMMENT_RU", length = 4000)
    protected String commentRu;



    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public Date getUpdateTs() {
        return updateTs;
    }


    public void setCommentRu(String commentRu) {
        this.commentRu = commentRu;
    }

    public String getCommentRu() {
        return commentRu;
    }


    public String getByUser() {
        return byUser;
    }

    public void setByUser(String byUser) {
        this.byUser = byUser;
    }



    public void setCorrespondent(Correspondent correspondent) {
        this.correspondent = correspondent;
    }

    public Correspondent getCorrespondent() {
        return correspondent;
    }







    public void setState(VerifiedStateEnum state) {
        this.state = state == null ? null : state.getId();
    }

    public VerifiedStateEnum getState() {
        return state == null ? null : VerifiedStateEnum.fromId(state);
    }















}