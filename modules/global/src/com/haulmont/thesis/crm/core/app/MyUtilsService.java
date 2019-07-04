/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.core.app
 */
package com.haulmont.thesis.crm.core.app;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author d.ivanov
 */
public interface MyUtilsService {
    String NAME = "crm_MyUtilsService";

    List<Object> getCostList(UUID spaceId, UUID projectId, UUID organizationId);
    boolean updateCompanyTxtSearch(UUID companyId);
    int getActCount(UUID ordId);
    boolean createTaskgScheduleCallDate(Map<String, Object> params);
    List<Object[]> getProductMap20();
}