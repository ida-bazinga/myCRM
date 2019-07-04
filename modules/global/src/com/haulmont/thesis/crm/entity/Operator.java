/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern("%s %s|code,employee")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_OPERATOR")
@Entity(name = "crm$Operator")
public class Operator extends BaseUuidEntity {
    private static final long serialVersionUID = -8186033989848119746L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected ExtEmployee employee;

    @Column(name = "COMMENT_RU", length = 1000)
    protected String comment_ru;

    @OneToMany(mappedBy = "operator")
    protected List<OperatorSession> sessions;

    @ManyToMany(mappedBy = "operators")
    protected List<OutboundCampaign> campaigns;

    public void setCampaigns(List<OutboundCampaign> campaigns) {
        this.campaigns = campaigns;
    }

    public List<OutboundCampaign> getCampaigns() {
        return campaigns;
    }


    public void setSessions(List<OperatorSession> sessions) {
        this.sessions = sessions;
    }

    public List<OperatorSession> getSessions() {
        return sessions;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }


    public void setEmployee(ExtEmployee employee) {
        this.employee = employee;
    }

    public ExtEmployee getEmployee() {
        return employee;
    }


}