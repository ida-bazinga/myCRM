/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import java.util.List;
import javax.persistence.OneToMany;

/**
 * @author d.ivanov
 */
@NamePattern("%s|name_ru")
@Table(name = "CRM_ROOM_SEATING_TYPE")
@Entity(name = "crm$RoomSeatingType")
public class RoomSeatingType extends BaseUuidEntity {
    private static final long serialVersionUID = -2788408858122571030L;

    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @Column(name = "NAME_RU", nullable = false, length = 500)
    protected String name_ru;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROOM_ID")
    protected Room room;

    @Column(name = "MIN_CAPACITY", nullable = false)
    protected Integer minCapacity;

    @Column(name = "MAX_CAPACITY", nullable = false)
    protected Integer maxCapacity;

    @Column(name = "SEATING_TYPE", nullable = false)
    protected Integer seatingType;

    @Composition
    @OneToMany(mappedBy = "roomSeatingType")
    protected List<RoomSeatingTypeAttachment> roomSeatingTypeAttachment;

    @Column(name = "HAS_ATTACHMENTS", nullable = false)
    protected Boolean hasAttachments = false;

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }


    public void setRoomSeatingTypeAttachment(List<RoomSeatingTypeAttachment> roomSeatingTypeAttachment) {
        this.roomSeatingTypeAttachment = roomSeatingTypeAttachment;
    }

    public List<RoomSeatingTypeAttachment> getRoomSeatingTypeAttachment() {
        return roomSeatingTypeAttachment;
    }


    public void setSeatingType(SeatingType seatingType) {
        this.seatingType = seatingType == null ? null : seatingType.getId();
    }

    public SeatingType getSeatingType() {
        return seatingType == null ? null : SeatingType.fromId(seatingType);
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }

    public Integer getMinCapacity() {
        return minCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }


}