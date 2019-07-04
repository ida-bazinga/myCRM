/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;
import org.apache.openjpa.persistence.Persistent;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s (%s) %s|name,serialNumber,vendor")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CASH_MACHINE")
@Entity(name = "crm$CashMachine")
public class CashMachine extends BaseUuidEntity implements SoftDelete {
    private static final long serialVersionUID = 6108391764323380300L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "SERIAL_NUMBER", length = 50)
    protected String serialNumber;

    @Column(name = "VENDOR")
    protected String vendor;

    @Persistent
    @Column(name = "EVOTOR_ID")
    protected UUID evotorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXHIBIT_SPACE_ID")
    protected ExhibitSpace exhibitSpace;

    @OnDelete(DeletePolicy.DENY)
    @OneToMany(mappedBy = "cashMachine")
    protected List<CashDocument> cashDocuments;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }


    public void setCashDocuments(List<CashDocument> cashDocuments) {
        this.cashDocuments = cashDocuments;
    }

    public List<CashDocument> getCashDocuments() {
        return cashDocuments;
    }


    public UUID getEvotorId() {
        return evotorId;
    }

    public void setEvotorId(UUID evotorId) {
        this.evotorId = evotorId;
    }


    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setExhibitSpace(ExhibitSpace exhibitSpace) {
        this.exhibitSpace = exhibitSpace;
    }

    public ExhibitSpace getExhibitSpace() {
        return exhibitSpace;
    }

    public Boolean isDeleted() {
        return deleteTs != null;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    public Date getDeleteTs() {
        return deleteTs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}