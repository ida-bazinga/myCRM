package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;

import javax.persistence.*;
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author a.donskoy
 */
@PrimaryKeyJoinColumn(name = "RESOURCE_LOADINGS_INFO_ID", referencedColumnName = "ID")
@DiscriminatorValue("PR")
@NamePattern("%s %s|room,project")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "CRM_PROJECT_ROOM")
@Entity(name = "crm$ProjectRoom")
public class ProjectRoom extends RoomResourceLoadingsInfo {
    private static final long serialVersionUID = 5601045501506180547L;

    @Column(name = "VARIANT")
    protected Integer variant;

    @Column(name = "STATUS")
    protected Integer status;

    public void setVariant(ProjectRoomVariantEnum variant) {
        this.variant = variant == null ? null : variant.getId();
    }

    public ProjectRoomVariantEnum getVariant() {
        return variant == null ? null : ProjectRoomVariantEnum.fromId(variant);
    }

    public void setStatus(ProjectRoomStatusEnum status) {
        this.status = status == null ? null : status.getId();
    }

    public ProjectRoomStatusEnum getStatus() {
        return status == null ? null : ProjectRoomStatusEnum.fromId(status);
    }
}