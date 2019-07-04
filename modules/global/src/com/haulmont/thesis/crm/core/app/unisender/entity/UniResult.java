/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.unisender.entity;

import java.io.Serializable;
import java.util.Date;

public class UniResult implements Serializable {
    protected String email_result;
    protected Date last_modified;

    public UniResult(String email_result, Date last_modified) {
        this.email_result = email_result;
        this.last_modified = last_modified;
    }

    public String getResult() {
        return email_result;
    }

    public void setResult(String email_result) {
        this.email_result = email_result;
    }

    public Date getDate() {
        return last_modified;
    }

    public void setDate(Date last_modified) {
        this.last_modified = last_modified;
    }

}

