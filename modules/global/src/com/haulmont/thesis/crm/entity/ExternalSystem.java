/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.SoftDelete;
import java.util.Date;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author d.ivanov
 */
@NamePattern("%s|name")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_EXTERNAL_SYSTEM")
@Entity(name = "crm$ExternalSystem")
public class ExternalSystem extends BaseUuidEntity {
    private static final long serialVersionUID = 8200550222724515271L;

    @Column(name = "CODE")
    protected String code;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "CONNECTION_STRING", length = 4000)
    protected String connectionString;

    @Column(name = "LOGIN")
    protected String login;

    @Column(name = "PASSWORD")
    protected String password;

    @Column(name = "AUTHENTICATION_KEY", length = 4000)
    protected String authenticationKey;

    @Column(name = "ACTIVITY")
    protected Boolean activity;




    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }

    public Boolean getActivity() {
        return activity;
    }


}