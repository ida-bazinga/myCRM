/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.cuba.core.entity.BaseUuidEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author a.donskoy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_UNI_CONTACT")
@Entity(name = "crm$UniContact")
public class UniContact extends BaseUuidEntity {
    private static final long serialVersionUID = 6051392038396968259L;

    @Column(name = "NAME_RU", length = 500)
    protected String name_ru;

    @Column(name = "EMAIL", length = 500)
    protected String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EMAIL_ADD_TIME")
    protected Date emailAddTime;

    @Column(name = "EMAIL_REQUEST_IP", length = 15)
    protected String emailRequestIp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EMAIL_CONFIRM_TIME")
    protected Date emailConfirmTime;

    @Column(name = "EMAIL_CONFIRM_IP", length = 15)
    protected String emailConfirmIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_STATUS_ID")
    protected EmailStatus emailStatus;

    public void setEmailStatus(EmailStatus emailStatus) {
        this.emailStatus = emailStatus;
    }

    public EmailStatus getEmailStatus() {
        return emailStatus;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmailAddTime(Date emailAddTime) {
        this.emailAddTime = emailAddTime;
    }

    public Date getEmailAddTime() {
        return emailAddTime;
    }

    public void setEmailRequestIp(String emailRequestIp) {
        this.emailRequestIp = emailRequestIp;
    }

    public String getEmailRequestIp() {
        return emailRequestIp;
    }

    public void setEmailConfirmTime(Date emailConfirmTime) {
        this.emailConfirmTime = emailConfirmTime;
    }

    public Date getEmailConfirmTime() {
        return emailConfirmTime;
    }

    public void setEmailConfirmIp(String emailConfirmIp) {
        this.emailConfirmIp = emailConfirmIp;
    }

    public String getEmailConfirmIp() {
        return emailConfirmIp;
    }


    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    public String getName_ru() {
        return name_ru;
    }


}