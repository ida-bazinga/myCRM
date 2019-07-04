package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;

import javax.persistence.*;

/**
 * @author Kirill Khoroshilov, 2019
 */
@Listeners("crm_RoomLoadingCollisionEntityListener")
@NamePattern("%s|description")
@Table(name = "CRM_ROOM_LOADING_COLLISION")
@Entity(name = "crm$RoomLoadingCollision")
public class RoomLoadingCollision extends BaseUuidEntity {
    private static final long serialVersionUID = -2211458036798893684L;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOADING_INFO_ID")
    protected RoomResourceLoadingsInfo loadingInfo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COLLISION_INFO_ID")
    protected RoomResourceLoadingsInfo collisionInfo;

    @Column(name = "IS_COLLISION", nullable = false)
    protected Boolean isCollision = false;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLoadingInfo(RoomResourceLoadingsInfo loadingInfo) {
        this.loadingInfo = loadingInfo;
    }

    public RoomResourceLoadingsInfo getLoadingInfo() {
        return loadingInfo;
    }

    public void setCollisionInfo(RoomResourceLoadingsInfo collisionInfo) {
        this.collisionInfo = collisionInfo;
    }

    public RoomResourceLoadingsInfo getCollisionInfo() {
        return collisionInfo;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setIsCollision(Boolean isCollision) {
        this.isCollision = isCollision;
    }

    public Boolean getIsCollision() {
        return isCollision;
    }
}