/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author d.ivanov
 */
@Table(name = "CRM_ROOM_GEOLOCATION")
@Entity(name = "crm$RoomGeolocation")
public class RoomGeolocation extends BaseUuidEntity {
    private static final long serialVersionUID = 1741842250433343605L;

    @Column(name = "GEOLOCATION_TYPE", nullable = false)
    protected String geolocationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @Column(name = "POINT_X", nullable = false)
    protected Integer pointX;

    @Column(name = "POINT_Y", nullable = false)
    protected Integer pointY;

    @Column(name = "LENGTH", nullable = false)
    protected Integer length;

    @Column(name = "WIDTH", nullable = false)
    protected Integer width;

    @Column(name = "LOCATION_ID")
    protected Integer locationId;

    public void setLocationId(LocationIdEnum locationId) {
        this.locationId = locationId == null ? null : locationId.getId();
    }

    public LocationIdEnum getLocationId() {
        return locationId == null ? null : LocationIdEnum.fromId(locationId);
    }


    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getWidth() {
        return width;
    }


    public void setGeolocationType(GeolocationTypeEnum geolocationType) {
        this.geolocationType = geolocationType == null ? null : geolocationType.getId();
    }

    public GeolocationTypeEnum getGeolocationType() {
        return geolocationType == null ? null : GeolocationTypeEnum.fromId(geolocationType);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setPointX(Integer pointX) {
        this.pointX = pointX;
    }

    public Integer getPointX() {
        return pointX;
    }

    public void setPointY(Integer pointY) {
        this.pointY = pointY;
    }

    public Integer getPointY() {
        return pointY;
    }


}