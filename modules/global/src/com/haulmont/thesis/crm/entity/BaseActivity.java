package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.thesis.core.entity.*;
import com.haulmont.thesis.crm.enums.ActivityDirectionEnum;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|description")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "ID")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "CRM_BASE_ACTIVITY")
@Entity(name = "crm$BaseActivity")
public abstract class BaseActivity extends TsCard implements BelongOrganization, HasDetailedDescription {
    private static final long serialVersionUID = 9135927380328714482L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "KIND_ID")
    protected ActivityKind kind;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME")
    protected Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME_PLAN")
    protected Date endTimePlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME_FACT")
    protected Date endTimeFact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESULT_ID")
    protected ActivityRes result;

    @Column(name = "DETAILS", length = 4000)
    protected String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected ExtProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected ExtEmployee owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    protected Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @Column(name = "ADDRESS", length = 1000)
    protected String address;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_PERSON_ID")
    protected ExtContactPerson contactPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID")
    protected BaseCampaign campaign;

    @Column(name = "DIRECTION")
    protected Integer direction;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
    public ExtProject getProject() {
        return project;
    }

    public void setProject(ExtProject project) {
        this.project = project;
    }

    public ExtEmployee getOwner() {
        return owner;
    }

    public void setOwner(ExtEmployee owner) {
        this.owner = owner;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setDirection(ActivityDirectionEnum direction) {
        this.direction = direction == null ? null : direction.getId();
    }

    public void setState(ActivityStateEnum state) {
        this.state = state == null ? null : state.getId();
    }

    public ActivityDirectionEnum getDirection() {
        return direction == null ? null : ActivityDirectionEnum.fromId(direction);
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setContactPerson(ExtContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public ExtContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCampaign(BaseCampaign campaign) {
        this.campaign = campaign;
    }

    public BaseCampaign getCampaign() {
        return campaign;
    }

    public void setResult(ActivityRes result) {
        this.result = result;
    }

    public ActivityRes getResult() {
        return result;
    }

    public void setEndTimeFact(Date endTimeFact) {
        this.endTimeFact = endTimeFact;
    }

    public Date getEndTimeFact() {
        return endTimeFact;
    }

    @MetaProperty(related = "endTimeFact")
    public Date getEndDateFact() {
        return getEndTimeFact() != null ? DateUtils.truncate(getEndTimeFact(), Calendar.DAY_OF_MONTH) : null;
    }

    public void setEndTimePlan(Date endTimePlan) {
        this.endTimePlan = endTimePlan;
    }

    public Date getEndTimePlan() {
        return endTimePlan;
    }

    @MetaProperty(related = "endTimePlan")
    public Date getEndDatePlan() {
        return getEndTimePlan() != null ? DateUtils.truncate(getEndTimePlan(), Calendar.DAY_OF_MONTH) : null;
    }

    public void setKind(ActivityKind kind) {
        this.kind = kind;
        this.category = kind;
    }

    public ActivityKind getKind() {
        return kind;
    }

    @Override
    public void setCategory(Category category) {
        if ((category != null) && !(category instanceof ActivityKind))
            throw new IllegalArgumentException("Category of activity must be instance of ActivityKind!");
        super.setCategory(category);
        this.kind = (ActivityKind) category;
    }

    @PostConstruct
    protected void init() {
        setState(ActivityStateEnum.NEW.getId());
        setCreateTime(AppBeans.get(TimeSource.class).currentTimestamp());
    }

    @Override
    public String getDetailedDescription(Locale locale, boolean includeState, boolean includeShortInfo) {
        StringBuilder result = new StringBuilder();
        String locState = getLocState(locale);

        if (StringUtils.isNotBlank(getDescription())){
            result.append(getDescription());
        }

        if (includeState && StringUtils.isNotBlank(locState)){
            result.append(" [")
                    .append(locState)
                    .append("]")
                    .append(" ");
        }

        if (includeShortInfo && getProject() != null) {
            int infoLength = MAX_SUBJECT_LENGTH - result.length();

            if (infoLength > MIN_SHORT_INFO_LENGTH) {
                String shortInfo = StringUtils.defaultIfBlank(getProject().getName(), "");

                result.append(" ")
                        .append(StringUtils.abbreviate(shortInfo, infoLength))
                        .append("-");
            }
        }
        return result.toString().trim();
    }

    // TODO: 03.04.2018 uncomment
    @Override
    public String getLocState() {
        if (getState() == null)
            return super.getLocState();
        return AppBeans.get(Messages.class).getMessage(ActivityStateEnum.fromId(getState()));
    }

    @Override
    public String toString() {
        return getInstanceName() + " [" + super.toString() + "]";
    }
}