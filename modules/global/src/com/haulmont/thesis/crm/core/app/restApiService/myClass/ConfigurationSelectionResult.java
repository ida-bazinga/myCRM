/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.restApiService.myClass;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import java.util.ArrayList;
import java.util.List;


@MetaClass(name="crm$ConfigurationSelectionResult")
public class ConfigurationSelectionResult extends AbstractNotPersistentEntity {

    @MetaProperty
    protected String rkey;


    @MetaProperty
    protected List<ConfigurationSelection> configurationSelection = new ArrayList<>();


    public String getRkey() {
        return rkey;
    }

    public void setRkey (String rkey) {
        this.rkey = rkey;
    }


    public List<ConfigurationSelection> getConfigurationSelection() {
        return configurationSelection;
    }

    public void setConfigurationSelection(List<ConfigurationSelection> configurationSelection) {
        this.configurationSelection = configurationSelection;
    }

}
