/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Versioned;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.core.entity.Company;
import com.haulmont.thesis.core.entity.Project;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|title_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_PRODUCT")
@Entity(name = "crm$Product")
@TrackEditScreenHistory
public class Product extends BaseUuidEntity implements Versioned, SoftDelete, Updatable {
    private static final long serialVersionUID = 8418964086097757777L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Column(name = "TITLE_EN", length = 1500)
    protected String title_en;

    @Column(name = "TITLE_RU", length = 1500)
    protected String title_ru;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    @Column(name = "COMMENT_EN", length = 4000)
    protected String comment_en;

    @Column(name = "MAX_QUANTITY", precision = 15, scale = 3)
    protected BigDecimal maxQuantity;

    @Column(name = "MIN_QUANTITY", precision = 15, scale = 3)
    protected BigDecimal minQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOMENCLATURE_ID")
    protected Nomenclature nomenclature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARACTERISTIC_ID")
    protected Characteristic characteristic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXHIBIT_SPACE_ID")
    protected ExhibitSpace exhibitSpace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ORGANIZER_ID")
    protected Company eventOrganizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_TYPE_ID")
    protected ProductType productType;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    @OrderBy("startDate")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "product")
    protected List<Cost> costs;

    @Column(name = "NOT_IN_USE")
    protected Boolean notInUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECONDARY_UNIT_ID")
    protected Unit secondaryUnit;

    @Column(name = "TOURISM_PRODUCT_TYPE")
    protected Integer tourismProductType;

    @Version
    @Column(name = "VERSION")
    protected Integer version;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;



    public void setTourismProductType(TourismProductTypeEnum tourismProductType) {
        this.tourismProductType = tourismProductType == null ? null : tourismProductType.getId();
    }

    public TourismProductTypeEnum getTourismProductType() {
        return tourismProductType == null ? null : TourismProductTypeEnum.fromId(tourismProductType);
    }


    public void setSecondaryUnit(Unit secondaryUnit) {
        this.secondaryUnit = secondaryUnit;
    }

    public Unit getSecondaryUnit() {
        return secondaryUnit;
    }


    public void setNotInUse(Boolean notInUse) {
        this.notInUse = notInUse;
    }

    public Boolean getNotInUse() {
        return notInUse;
    }


    public BigDecimal getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maxQuantity = maxQuantity;
    }


    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }


    public void setTitle_ru(String title_ru) {
        this.title_ru = title_ru;
    }


    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


    public void setCosts(List<Cost> costs) {
        this.costs = costs;
    }

    public List<Cost> getCosts() {
        return costs;
    }


    public void setCharacteristic(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    public Characteristic getCharacteristic() {
        return characteristic;
    }

    public void setExhibitSpace(ExhibitSpace exhibitSpace) {
        this.exhibitSpace = exhibitSpace;
    }

    public ExhibitSpace getExhibitSpace() {
        return exhibitSpace;
    }

    public void setEventOrganizer(Company eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public Company getEventOrganizer() {
        return eventOrganizer;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductType getProductType() {
        return productType;
    }
    
    public void setNomenclature(Nomenclature nomenclature) {
        this.nomenclature = nomenclature;
    }

    public Nomenclature getNomenclature() {
        
        //this.title_en = String.format("%s %s", nomenclature != null ? nomenclature.getPublicName_en() : "", characteristic != null ? characteristic.getName_en() : "" );
    	//this.title_ru = String.format("%s %s", nomenclature != null ? nomenclature.getPublicName_ru() : "", characteristic != null ? characteristic.getName_ru() : "" );
    	return nomenclature;
    }

    
    public String getCode() {
        return code;
    }

   
    public String getTitle_en() {
        return title_en;
        //return String.format("%s %s", nomenclature != null ? nomenclature.getPublicName_en() : "", characteristic != null ? characteristic.getName_en() : "" );
    }

    
    public String getTitle_ru() {
        return title_ru;
        //return String.format("%s %s", nomenclature != null ? nomenclature.getPublicName_ru() : "", characteristic != null ? characteristic.getName_ru() : "" );
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_en() {
        return comment_en;
    }
    
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

    public Integer getVersion() {
        return version;
    }


}