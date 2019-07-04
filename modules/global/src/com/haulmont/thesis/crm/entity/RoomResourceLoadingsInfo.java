package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * @author Kirill Khoroshilov, 2019
 */
@Listeners("crm_RoomLoadingEntityListener")
@NamePattern("%s|room")
@Table(name = "CRM_ROOM_RESOURCE_LOADINGS_INFO")
@Entity(name = "crm$RoomResourceLoadingsInfo")
public class RoomResourceLoadingsInfo extends BaseUuidEntity implements IRoomResourceLoadingsInfo{
    private static final long serialVersionUID = -4194697868902153768L;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @Column(name = "AREA", nullable = false,precision = 15, scale = 2)
    protected BigDecimal area = BigDecimal.ZERO;

    @Temporal(TemporalType.DATE)
    @Column(name = "INSTALLATION_DATE", nullable = false)
    protected Date installationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEINSTALLATION_DATE", nullable = false)
    protected Date deinstallationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "OPTION_DATE")
    protected Date optionDate;

    @Column(name = "IS_EARLY_INSTALLATION", nullable = false)
    protected Boolean isEarlyInstallation = false;

    @Column(name = "IS_LATE_DEINSTALLATION", nullable = false)
    protected Boolean isLateDeinstallation = false;

    @OneToMany(mappedBy = "collisionInfo")
    protected Collection<RoomLoadingCollision> collisions;

    public void setProject(ExtProject project) {
        this.project = project;
    }

    @Override
    public ExtProject getProject() {
        return project;
    }

    public void setCollisions(Collection<RoomLoadingCollision> collisions) {
        this.collisions = collisions;
    }

    public Collection<RoomLoadingCollision> getCollisions() {
        return collisions;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public Room getRoom() {
        return room;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    @Override
    public BigDecimal getArea() {
        return area;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    @Override
    public Date getInstallationDate() {
        return installationDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setDeinstallationDate(Date deinstallationDate) {
        this.deinstallationDate = deinstallationDate;
    }

    @Override
    public Date getDeinstallationDate() {
        return deinstallationDate;
    }

    public void setOptionDate(Date optionDate) {
        this.optionDate = optionDate;
    }

    @Override
    public Date getOptionDate() {
        return optionDate;
    }

    public void setIsEarlyInstallation(Boolean isEarlyInstallation) {
        this.isEarlyInstallation = isEarlyInstallation;
    }

    public Boolean getIsEarlyInstallation() {
        return isEarlyInstallation;
    }

    @Override
    public Boolean isEarlyInstallation() {
        return isEarlyInstallation;
    }

    public void setIsLateDeinstallation(Boolean isLateDeinstallation) {
        this.isLateDeinstallation = isLateDeinstallation;
    }

    public Boolean getIsLateDeinstallation() {
        return isLateDeinstallation;
    }

    @Override
    public Boolean isLateDeinstallation() {
        return isLateDeinstallation;
    }

    @Override
    @MetaProperty()
    public Boolean isPhantom(){ return false;}

    @Override
    @MetaProperty(related = "optionDate")
    public Boolean isOption() {
        return this.optionDate != null;
    }

}