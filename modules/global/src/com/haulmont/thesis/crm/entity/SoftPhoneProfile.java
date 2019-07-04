/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;

/**
 * @author Kirill Khoroshilov
 */
@MetaClass(name = "crm$SoftPhoneProfile")
public class SoftPhoneProfile extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 7912357287168064756L;

    @JsonProperty("ProfileId")
    @MetaProperty
    protected String extSystemId;

    @JsonProperty("Name")
    @MetaProperty
    protected String name;

    @JsonProperty("CustomName")
    @MetaProperty
    protected String customName;

    @JsonProperty("ExtendedStatus")
    @MetaProperty
    protected String extendedStatus;

    @JsonProperty("IsActive")
    @MetaProperty
    protected Boolean isActive;

    @MetaProperty(related = "name")
    public SoftPhoneProfileName getLocalizedName() {
        return name == null ? null : SoftPhoneProfileName.fromId(name);
    }

    public void setExtSystemId(String extSystemId) {
        this.extSystemId = extSystemId;
    }

    public String getExtSystemId() {
        return extSystemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getCustomName() {
        return customName;
    }

    public void setExtendedStatus(String extendedStatus) {
        this.extendedStatus = extendedStatus;
    }

    public String getExtendedStatus() {
        return extendedStatus;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return getName();
    }
}