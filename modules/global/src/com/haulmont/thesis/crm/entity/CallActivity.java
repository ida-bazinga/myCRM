package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import org.apache.openjpa.persistence.Persistent;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * @author k.khoroshilov
 */
@Listeners({"crm_ActivityEntityListener", "crm_CallActivityEntityListener"})
@NamePattern("%s|description")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("510")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "CRM_CALL_ACTIVITY")
@Entity(name = "crm$CallActivity")
@EnableRestore
@TrackEditScreenHistory
public class CallActivity extends BaseActivity {
    private static final long serialVersionUID = 6004880030472607673L;

    @Persistent
    @Column(name = "SOFTPHONE_SESSION_ID")
    protected UUID softphoneSessionId;

    @Column(name = "CALL_ID", length = 20)
    protected String callId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMUNICATION_ID")
    protected Communication communication;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONNECTION_START_TIME")
    protected Date connectionStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONNECTION_END_TIME")
    protected Date connectionEndTime;

    public void setSoftphoneSessionId(UUID softphoneSessionId) {
        this.softphoneSessionId = softphoneSessionId;
    }

    public UUID getSoftphoneSessionId() {
        return softphoneSessionId;
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public Communication getCommunication() {
        return communication;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public void setConnectionStartTime(Date connectionStartTime) {
        this.connectionStartTime = connectionStartTime;
    }

    public Date getConnectionStartTime() {
        return connectionStartTime;
    }

    public void setConnectionEndTime(Date connectionEndTime) {
        this.connectionEndTime = connectionEndTime;
    }

    public Date getConnectionEndTime() {
        return connectionEndTime;
    }

    @MetaProperty(related = {"connectionStartTime", "createTime"})
    public Date getPreparationSeconds() {
        if (getConnectionStartTime() != null) {
            return new Date(getConnectionStartTime().getTime() - getCreateTime().getTime());
        }else{
            return new Date(getCreateTime().getTime() - getCreateTime().getTime());
        }
    }

    @MetaProperty(related = {"connectionStartTime", "connectionEndTime", "createTime"})
    public Date getConnectingSeconds() {
        if (getConnectionStartTime() != null && getConnectionEndTime() != null) {
            return new Date(getConnectionEndTime().getTime() - getConnectionStartTime().getTime());
        } else {
            return new Date(getCreateTime().getTime() - getCreateTime().getTime());
        }
    }

    @MetaProperty(related = {"endTimeFact", "connectionEndTime", "createTime"})
    public Date getEditingSeconds() {
        if (getConnectionEndTime() != null) {
            return new Date(getEndTimeFact().getTime() - getConnectionEndTime().getTime());
        }else{
            return new Date(getCreateTime().getTime() - getCreateTime().getTime());
        }
    }

    @MetaProperty(related = {"endTimeFact", "createTime"})
    public Date getTotalSeconds() {
        if (getEndTimeFact() != null) {
            return new Date(getEndTimeFact().getTime() - getCreateTime().getTime());
        } else {
            return new Date(getCreateTime().getTime() - getCreateTime().getTime());
        }
    }
}