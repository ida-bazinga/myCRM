package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author a.donskoy
 */
@Listeners("crm_ResourceEntityListener")
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("Z")
@Table(name = "CRM_RESOURCE")
@Entity(name = "crm$Resource")
@NamePattern("%s|name_ru")
public abstract class Resource extends BaseUuidEntity implements SoftDelete, Updatable {
    private static final long serialVersionUID = -7245627500560880565L;
    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;

    @Column(name = "SERIAL_NUMBER", length = 50)
    protected String serialNumber;

    @Column(name = "REFERENCE", length = 2000)
    protected String reference;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    @Column(name = "IS_GROUP")
    protected Boolean isGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOURCE_TYPE_ID")
    protected ResourceType resourceType;

    @Column(name = "INVENTORY_NUMBER", length = 50)
    protected String inventoryNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    protected EquipmentState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_ID")
    protected Unit unit;

    @OneToMany(mappedBy = "resource")
    protected List<ResourceProperty> resourceProperty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected Resource parent;
    
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "resource")
    @Composition
    protected List<ResourceAttachment> resourceAttachments;

    @Column(name = "HAS_ATTACHMENTS")
    protected Boolean hasAttachments;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PHOTO_FILE_ID")
    protected FileDescriptor photo;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVATAR_FILE_ID")
    protected FileDescriptor avatar;

    @OneToMany(mappedBy = "resource")
    protected List<Provider> provider;

    @OneToMany(mappedBy = "resource")
    protected List<ResourceHistory> resourceHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVIDER_COMPANY_ID")
    protected ExtCompany providerCompany;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOT_ID")
    protected Resource root;

    @Column(name = "USE_LOADING_INFO", nullable = false)
    protected Boolean useLoadingInfo = false;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setUseLoadingInfo(Boolean useLoadingInfo) {
        this.useLoadingInfo = useLoadingInfo;
    }

    public Boolean getUseLoadingInfo() {
        return useLoadingInfo;
    }

    public void setRoot(Resource root) {
        this.root = root;
    }

    public Resource getRoot() {
        return root;
    }

    public void setProviderCompany(ExtCompany providerCompany) {
        this.providerCompany = providerCompany;
    }

    public ExtCompany getProviderCompany() {
        return providerCompany;
    }

    public void setResourceHistory(List<ResourceHistory> resourceHistory) {
        this.resourceHistory = resourceHistory;
    }

    public List<ResourceHistory> getResourceHistory() {
        return resourceHistory;
    }

    public void setProvider(List<Provider> provider) {
        this.provider = provider;
    }

    public List<Provider> getProvider() {
        return provider;
    }

    public void setAvatar(FileDescriptor avatar) {
        this.avatar = avatar;
    }

    public FileDescriptor getAvatar() {
        return avatar;
    }

    public void setPhoto(FileDescriptor photo) {
        this.photo = photo;
    }

    public FileDescriptor getPhoto() {
        return photo;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setResourceAttachments(List<ResourceAttachment> resourceAttachments) {
        this.resourceAttachments = resourceAttachments;
    }

    public List<ResourceAttachment> getResourceAttachments() {
        return resourceAttachments;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public Resource getParent() {
        return parent;
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

    public void setResourceProperty(List<ResourceProperty> resourceProperty) {
        this.resourceProperty = resourceProperty;
    }

    public List<ResourceProperty> getResourceProperty() {
        return resourceProperty;
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

    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setState(EquipmentState state) {
        this.state = state;
    }

    public EquipmentState getState() {
        return state;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

}