/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app.restApiService
 */
package com.haulmont.thesis.crm.core.app.restApiService;

/**
 * @author d.ivanov
 */
public interface CreateRoomRequestService {
    String NAME = "crm_CreateRoomRequestService";

    Boolean createRoomRequestService(String param);
}