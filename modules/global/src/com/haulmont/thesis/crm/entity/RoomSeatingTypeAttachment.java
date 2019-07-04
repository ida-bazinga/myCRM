/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.workflow.core.entity.Attachment;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s|name")
@DiscriminatorValue("S")
@Entity(name = "crm$RoomSeatingTypeAttachment")
public class RoomSeatingTypeAttachment extends Attachment {
    private static final long serialVersionUID = -8832332950574201358L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_SEATING_TYPE_ID")
    protected RoomSeatingType roomSeatingType;

    public void setRoomSeatingType(RoomSeatingType roomSeatingType) {
        this.roomSeatingType = roomSeatingType;
    }

    public RoomSeatingType getRoomSeatingType() {
        return roomSeatingType;
    }


}