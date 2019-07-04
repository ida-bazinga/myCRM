/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.chile.core.annotations.NamePattern;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.thesis.core.entity.TsProc;

/**
 * @author a.donskoy
 */
@Deprecated
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CAMPAIGN_UNISENDER")
@Entity(name = "crm$CampaignUnisender")
public class CampaignUnisender extends BaseLookup {
    private static final long serialVersionUID = -3813965336405550190L;

    @Column(name = "UNISENDER_ID")
    protected Integer unisender_id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    protected Date start_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected MessageCampaignStatus status;

    @Column(name = "MESSAGE_ID")
    protected Integer message_id;

    @Column(name = "LIST_ID")
    protected Integer list_id;

    @Column(name = "SENDER_NAME")
    protected String sender_name;

    @Column(name = "SENDER_EMAIL")
    protected String sender_email;

    @Column(name = "STATS_URL")
    protected String stats_url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_ID")
    protected TsProc proc;

    public void setProc(TsProc proc) {
        this.proc = proc;
    }

    public TsProc getProc() {
        return proc;
    }


    public MessageCampaignStatus getStatus() {
        return status;
    }

    public void setStatus(MessageCampaignStatus status) {
        this.status = status;
    }




    public void setUnisender_id(Integer unisender_id) {
        this.unisender_id = unisender_id;
    }

    public Integer getUnisender_id() {
        return unisender_id;
    }


    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }


    public Integer getList_id() {
        return list_id;
    }

    public void setList_id(Integer list_id) {
        this.list_id = list_id;
    }



    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public String getSender_email() {
        return sender_email;
    }

    public void setStats_url(String stats_url) {
        this.stats_url = stats_url;
    }

    public String getStats_url() {
        return stats_url;
    }
/*
    public CampaignUnisender(String subject, int unisender_id, String start_time, MessageCampaignStatus status, int message_id, int list_id, String sender_name, String sender_email, String stats_url) {
        this.name_ru = subject;
        this.code = String.valueOf(unisender_id);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(start_time);
            this.start_time = date;
        } catch (Exception e) {
        }
        this.status = status;
        this.message_id = message_id;
        this.list_id = list_id;
        this.sender_name = sender_name;
        this.sender_email = sender_email;
        this.stats_url = stats_url;
    }
*/
}