package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.thesis.crm.enums.BookingEventDetailStatusEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * @author d.ivanov
 */
@PrimaryKeyJoinColumn(name = "RESOURCE_LOADINGS_INFO_ID", referencedColumnName = "ID")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("BED")
@NamePattern("%s %s %s|room,area,bookingEvent")
@Table(name = "CRM_BOOKING_EVENT_DETAIL")
@Entity(name = "crm$BookingEventDetail")
public class BookingEventDetail extends RoomResourceLoadingsInfo implements Updatable {
    private static final long serialVersionUID = 2837389105509694673L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOOKING_EVENT_ID")
    protected BookingEvent bookingEvent;

    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setStatus(BookingEventDetailStatusEnum status) {
        this.status = status == null ? null : status.getId();
    }

    public BookingEventDetailStatusEnum getStatus() {
        return status == null ? null : BookingEventDetailStatusEnum.fromId(status);
    }

    public void setBookingEvent(BookingEvent bookingEvent) {
        this.bookingEvent = bookingEvent;
    }

    public BookingEvent getBookingEvent() {
        return bookingEvent;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }
}