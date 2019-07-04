package com.haulmont.thesis.crm.core.app.loadingInfo;

import com.haulmont.thesis.crm.entity.Room;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 27.11.2018.
 */
public interface ResourceLoadingService {
    String NAME = "crm_ResourceLoadingService";

    @Nonnull
    Collection<Map<String, Object>> getRoomLoadingCollisions(Date startDate, Date endDate, Collection<Room> rooms);

    @Nonnull
    Collection<Map<String, Object>> getRoomLoadingInfo(Date startDate, Date endDate, Collection<Room> rooms, Boolean isShowOptions);
}