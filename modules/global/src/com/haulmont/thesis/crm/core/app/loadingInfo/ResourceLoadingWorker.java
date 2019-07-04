package com.haulmont.thesis.crm.core.app.loadingInfo;

import com.haulmont.thesis.crm.entity.IRoomResourceLoadingsInfo;
import com.haulmont.thesis.crm.entity.Room;
import com.haulmont.thesis.crm.entity.RoomLoadingCollision;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;

/**
 * @author Kirill Khoroshilov, 2019
 */

public interface ResourceLoadingWorker {

    String NAME = "crm_ResourceLoadingWorker";

    @Nonnull
    Collection<IRoomResourceLoadingsInfo> getRoomLoadingsInfo(Date startDate, Date endDate, Collection<Room> rooms);

    @Nonnull
    Collection<IRoomResourceLoadingsInfo> getRoomLoadingsInfo(Date startDate, Date endDate, Collection<Room> rooms, Boolean isShowOptions);

    @Nullable
    Room getRoomWithDependencies(Room room);

    @Nonnull
    Collection<Room> getRoomWithDependencies(Collection<Room> rooms);

    @Nonnull
    Collection<RoomLoadingCollision> getCollisionInfo(Date startDate, Date endDate, Collection<Room> rooms);
}
