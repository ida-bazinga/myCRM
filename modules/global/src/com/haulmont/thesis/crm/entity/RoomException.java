/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s (%s)|room,roomConflicting")
@Table(name = "CRM_ROOM_EXCEPTION")
@Entity(name = "crm$RoomException")
public class RoomException extends BaseUuidEntity {
    private static final long serialVersionUID = 2716766717048592756L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROOM_CONFLICTING_ID")
    protected Room roomConflicting;

    public void setRoomConflicting(Room roomConflicting) {
        this.roomConflicting = roomConflicting;
    }

    public Room getRoomConflicting() {
        return roomConflicting;
    }


    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }


}