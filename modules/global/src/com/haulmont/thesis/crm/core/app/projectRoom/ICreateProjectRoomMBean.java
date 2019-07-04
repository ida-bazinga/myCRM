/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.projectRoom;

import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.UUID;

@ManagedResource(description = "Создать помещения в проекте")
public interface ICreateProjectRoomMBean {
    String NAME = "ICreateProjectRoomMBean";
    boolean create(UUID bookingEventId);
    boolean remove(UUID bookingEventId);
}
