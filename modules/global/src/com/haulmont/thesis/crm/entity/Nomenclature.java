/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.Composition;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.thesis.core.entity.Organization;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_NOMENCLATURE")
@Entity(name = "crm$Nomenclature")
@TrackEditScreenHistory
public class Nomenclature extends BaseLookup implements Versioned, SoftDelete, Updatable {
    private static final long serialVersionUID = 5347208703112976007L;


    @Column(name = "NOMENCLATURE_TYPE", nullable = false)
    protected Integer nomenclatureType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARACTERISTIC_TYPE_ID")
    protected CharacteristicType characteristicType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATALOG_SECTION_ID")
    protected Catalog catalogSection;

    @Column(name = "COMMENT_EN", length = 1000)
    protected String comment_en;

    @Column(name = "PRINT_NAME_RU", length = 1000)
    protected String printName_ru;

    @Column(name = "PRINT_NAME_EN", length = 1000)
    protected String printName_en;

    @Column(name = "PUBLIC_NAME_RU", length = 250)
    protected String publicName_ru;

    @Column(name = "PUBLIC_NAME_EN")
    protected String publicName_en;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_ID")
    protected Unit unit;

    @Column(name = "HAS_ATTACHMENTS")
    protected Boolean hasAttachments;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "nomenclature")
    protected List<Product> products;

    @Column(name = "IS_FRACTIONAL")
    protected Boolean isFractional;

    @Column(name = "NOT_IN_USE")
    protected Boolean notInUse;

    @OneToMany(mappedBy = "nomenclature")
    protected List<NomenclatureResource> nomenclatureResource;

    @JoinTable(name = "CRM_NOMENCLATURE_CATALOG_LINK",
        joinColumns = @JoinColumn(name = "NOMENCLATURE_ID"),
        inverseJoinColumns = @JoinColumn(name = "CATALOG_ID"))
    @ManyToMany
    protected List<Catalog> catalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

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


    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }


    public void setCatalog(List<Catalog> catalog) {
        this.catalog = catalog;
    }

    public List<Catalog> getCatalog() {
        return catalog;
    }


    public void setNomenclatureResource(List<NomenclatureResource> nomenclatureResource) {
        this.nomenclatureResource = nomenclatureResource;
    }

    public List<NomenclatureResource> getNomenclatureResource() {
        return nomenclatureResource;
    }


    public void setNotInUse(Boolean notInUse) {
        this.notInUse = notInUse;
    }

    public Boolean getNotInUse() {
        return notInUse;
    }


    public void setIsFractional(Boolean isFractional) {
        this.isFractional = isFractional;
    }

    public Boolean getIsFractional() {
        return isFractional;
    }


    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }


    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }


    public void setPrintName_ru(String printName_ru) {
        this.printName_ru = printName_ru;
    }

    public String getPrintName_ru() {
        return printName_ru;
    }

    public void setPrintName_en(String printName_en) {
        this.printName_en = printName_en;
    }

    public String getPrintName_en() {
        return printName_en;
    }

    public void setPublicName_ru(String publicName_ru) {
        this.publicName_ru = publicName_ru;
    }

    public String getPublicName_ru() {
        return publicName_ru;
    }

    public void setPublicName_en(String publicName_en) {
        this.publicName_en = publicName_en;
    }

    public String getPublicName_en() {
        return publicName_en;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_en() {
        return comment_en;
    }


    public void setNomenclatureType(NomenclatureTypeEnum nomenclatureType) {
        this.nomenclatureType = nomenclatureType == null ? null : nomenclatureType.getId();
    }

    public NomenclatureTypeEnum getNomenclatureType() {
        return nomenclatureType == null ? null : NomenclatureTypeEnum.fromId(nomenclatureType);
    }

    public void setCharacteristicType(CharacteristicType characteristicType) {
        this.characteristicType = characteristicType;
    }

    public CharacteristicType getCharacteristicType() {
        return characteristicType;
    }

    public void setCatalogSection(Catalog catalogSection) {
        this.catalogSection = catalogSection;
    }

    public Catalog getCatalogSection() {
        return catalogSection;
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