/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.Updatable;
import java.util.Date;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_DUPLICATE_COMPANY")
@Entity(name = "crm$DuplicateCompany")
public class DuplicateCompany extends BaseUuidEntity implements Updatable {
    private static final long serialVersionUID = 8545723304783069895L;

    @Column(name = "CODE")
    protected String code;

    @Column(name = "NAME", length = 4000)
    protected String name;

    @Column(name = "COUNT_DUPLICATE")
    protected Integer countDuplicate;

    @Column(name = "EXTERNAL_SYSTEM")
    protected String externalSystem;

    @Column(name = "STATUS")
    protected Integer status;

    @OneToMany(mappedBy = "duplicateCompany")
    protected List<DuplicateCompanyDetail> duplicateCompanyDetail;

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


    public void setStatus(DuplicateStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public DuplicateStatus getStatus() {
        return status == null ? null : DuplicateStatus.fromId(status);
    }


    public void setDuplicateCompanyDetail(List<DuplicateCompanyDetail> duplicateCompanyDetail) {
        this.duplicateCompanyDetail = duplicateCompanyDetail;
    }

    public List<DuplicateCompanyDetail> getDuplicateCompanyDetail() {
        return duplicateCompanyDetail;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCountDuplicate(Integer countDuplicate) {
        this.countDuplicate = countDuplicate;
    }

    public Integer getCountDuplicate() {
        return countDuplicate;
    }

    public void setExternalSystem(String externalSystem) {
        this.externalSystem = externalSystem;
    }

    public String getExternalSystem() {
        return externalSystem;
    }


}