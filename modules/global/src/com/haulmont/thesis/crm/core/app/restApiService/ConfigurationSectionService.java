/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app
 */
package com.haulmont.thesis.crm.core.app.restApiService;

import com.haulmont.thesis.crm.core.app.restApiService.myClass.ConfigurationSelectionResult;

/**
 * @author d.ivanov
 */
public interface ConfigurationSectionService {
    String NAME = "crm_ConfigurationSectionService";


    ConfigurationSelectionResult configurationSearchService(String param);
}