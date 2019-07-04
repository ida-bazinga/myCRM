/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.restApiService.myClass;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;


@MetaClass(name="crm$ConfigurationSelection")
public class ConfigurationSelection extends AbstractNotPersistentEntity {

    @MetaProperty
    protected Integer code;

    @MetaProperty
    protected Integer statusId;

    @MetaProperty
    protected String text;



    public int getCode() {
        return code;
    }


    public void setCode (Integer code) {
        this.code = code;
    }


    public int getStatusId() {
        return statusId;
    }

    public void setStatusId (Integer statusId) {
        this.statusId = statusId;
    }

    public String getText() {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }
}
