package com.haulmont.thesis.crm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;
import com.haulmont.thesis.crm.enums.SoftPhoneStatus;
import com.haulmont.thesis.crm.parsers.SoftphoneProfileDecoder;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Kirill Khoroshilov
 */
@NamePattern("[%s] %s %s (%s)|lineNumber,lastName,firstName,status")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_SOFT_PHONE_SESSION")
@Entity(name = "crm$SoftPhoneSession")
public class SoftPhoneSession extends BaseUuidEntity {
    private static final long serialVersionUID = 2356799068781725842L;

    @JsonProperty("lastName")
    @Column(name = "LAST_NAME", length = 100)
    protected String lastName;

    @JsonProperty("firstName")
    @Column(name = "FIRST_NAME", length = 100)
    protected String firstName;

    @JsonProperty("lineNumber")
    @Column(name = "LINE_NUMBER", length = 10)
    protected String lineNumber;

    @JsonProperty("status")
    @Column(name = "STATUS")
    protected Integer status;

    @JsonProperty("activeProfileId")
    @Column(name = "ACTIVE_PROFILE_ID")
    protected String activeProfileId;

    @JsonProperty("activeProfileName")
    @Column(name = "ACTIVE_PROFILE")
    protected String activeProfile;

    @JsonProperty("profiles")
    @Column(name = "PROFILES_JSON_STRING", length = 2000)
    protected String profilesJsonString;

    @JsonProperty("vendor")
    @Column(name = "SOFTPHONE_VENDOR", length = 50)
    protected String softphoneVendor;

    @JsonIgnore
    @Column(name = "CAMPAIGN_ENABLED", nullable = false)
    protected Boolean campaignEnabled = false;

    @JsonIgnore
    @Column(name = "QUEUE_SIZE", nullable = false)
    protected Integer queueSize = 0;

    @JsonIgnore
    @Column(name = "IS_ACTIVE", nullable = false)
    protected Boolean isActive = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_ID")
    protected ExtEmployee operator;

    @JsonIgnore
    @Composition
    @OneToMany(mappedBy = "session")
    protected List<SoftPhoneEvent> events;

    @JsonIgnore
    @Transient
    protected List<SoftPhoneProfile> profiles;

    @JsonIgnore
    @Transient
    protected LinkedHashSet<CallStatus> activeCalls = new LinkedHashSet<>();

    @JsonIgnore
    @Transient
    protected CallStatus activeCall;

    public ExtEmployee getOperator() {
        return operator;
    }

    public void setOperator(ExtEmployee operator) {
        this.operator = operator;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setCampaignEnabled(Boolean campaignEnabled) {
        this.campaignEnabled = campaignEnabled;
    }

    public Boolean getCampaignEnabled() {
        return campaignEnabled;
    }

    public void setActiveCall(CallStatus activeCall) {
        this.activeCall = activeCall;
        if (activeCall != null){
            this.activeCalls.add(activeCall);
        }
    }

    public CallStatus getActiveCall() {
        return activeCall;
    }

    public void setActiveCalls(LinkedHashSet<CallStatus> activeCalls) {
        this.activeCalls = activeCalls;
    }

    public LinkedHashSet<CallStatus> getActiveCalls() {
        return activeCalls;
    }

    public List<SoftPhoneProfile> getProfiles() {
        if (StringUtils.isBlank(getProfilesJsonString().trim())){
            profiles = new ArrayList<>();
        } else{
            profiles = SoftphoneProfileDecoder.decode(getProfilesJsonString().trim());
        }
        return profiles;
    }

    public void setProfilesJsonString(String profilesJsonString) {
        this.profilesJsonString = profilesJsonString;
    }

    public String getProfilesJsonString() {
        return profilesJsonString;
    }

    public String getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(String activeProfileId) {
        this.activeProfileId = activeProfileId;
    }

    public SoftPhoneStatus getStatus() {
        return status == null ? null : SoftPhoneStatus.fromId(status);
    }

    public void setStatus(SoftPhoneStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setEvents(List<SoftPhoneEvent> events) {
        this.events = events;
    }

    public List<SoftPhoneEvent> getEvents() {
        return events;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setSoftphoneVendor(String softphoneVendor) {
        this.softphoneVendor = softphoneVendor;
    }

    public String getSoftphoneVendor() {
        return softphoneVendor;
    }

    public void setActiveProfile(SoftPhoneProfileName activeProfile) {
        this.activeProfile = activeProfile == null ? null : activeProfile.getId();
    }

    public SoftPhoneProfileName getActiveProfile() {
        return activeProfile == null ? null : SoftPhoneProfileName.fromId(activeProfile);
    }

    @Nullable
    public CallStatus getActiveCall(String callId) {
        if (!getActiveCalls().isEmpty()) {
            for(CallStatus callStatus : getActiveCalls()){
                if(callStatus.getCallId().equals(callId)){
                    return callStatus;
                }
            }
        }
        return null;
    }

    public void removeActiveCall(String callId) {
        CallStatus recordToRemove = getActiveCall(callId);
        if (recordToRemove != null){
            getActiveCalls().remove(recordToRemove);
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%s) {%s}", getLineNumber(), getLastName(), getFirstName(), getStatus(), getId());
    }

}