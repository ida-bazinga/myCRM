/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.core.entity.Company;
import org.apache.openjpa.persistence.Persistent;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author p.chizhikov
 */
@NamePattern("%s %s|name,inn")
@Extends(Company.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Z")
@Entity(name = "crm$Company")
public class ExtCompany extends Company {
    private static final long serialVersionUID = 8785433080270592241L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEGAL_FORM_ID")
    protected FormOfIncorporation legalForm;

    @Column(name = "COMPANY_TYPE")
    protected String companyType;

    @Column(name = "ALTERNATIVE_NAME", length = 1000)
    protected String alternativeName;

    @Temporal(TemporalType.TIME)
    @Column(name = "CALL_FROM")
    protected Date callFrom;

    @Temporal(TemporalType.TIME)
    @Column(name = "CALL_TO")
    protected Date callTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_LEGAL_ADDRESS_ID")
    protected Address extLegalAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_FACT_ADDRESS_ID")
    protected Address extFactAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_POSTAL_ADDRESS_ID")
    protected Address extPostalAddress;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Column(name = "REPEAT_LEGAL_ADDRESS_TOP")
    protected Boolean repeatLegalAddressTop;

    @Column(name = "REPEAT_LEGAL_ADDRESS_TOF")
    protected Boolean repeatLegalAddressTof;

    @Column(name = "IS_NOT_ACTUAL")
    protected Boolean isNotActual;

    @Column(name = "REASON_FOR_IRRELEVANCE", length = 1000)
    protected String reasonForIrrelevance;

    @Column(name = "IS_RESIDENT")
    protected Boolean isResident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_OF_REGISTRATION_ID")
    protected Country countryOfRegistration;

    @Column(name = "EGRIP", length = 15)
    protected String egrip;

    @Column(name = "REG_NO", length = 100)
    protected String regNo;

    @Column(name = "ID1C_ACCOUNT", length = 50)
    protected String id1cAccount;

    @Column(name = "ID1C_PARTNER", length = 50)
    protected String id1cPartner;

    @Lob
    @Column(name = "NOTES")
    protected String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMPANY_ID")
    protected ExtCompany parentCompany;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "company")
    protected List<WebAddress> web;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "company")
    protected List<EmailAddress> emails;

    @JoinTable(name = "CRM_COMPANY_LINE_OF_BUSINESS_LINK",
        joinColumns = @JoinColumn(name = "COMPANY_ID"),
        inverseJoinColumns = @JoinColumn(name = "LINE_OF_BUSINESS_ID"))
    @ManyToMany
    protected List<LineOfBusiness> linesOfBusiness;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "company")
    protected List<Activity> activities;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "company")
    protected List<CompanyProjects> projects;

    @JoinTable(name = "CRM_COMPANY_INFORMATION_SOURCE_LINK",
        joinColumns = @JoinColumn(name = "COMPANY_ID"),
        inverseJoinColumns = @JoinColumn(name = "INFORMATION_SOURCE_ID"))
    @ManyToMany
    protected List<InformationSource> informationSources;

    @JoinTable(name = "CRM_COMPANY_OKVD_LINK",
        joinColumns = @JoinColumn(name = "COMPANY_ID"),
        inverseJoinColumns = @JoinColumn(name = "OKVD_ID"))
    @ManyToMany
    protected List<Okvd> okvds;

    @Column(name = "WEB_ADDRESS", length = 1000)
    protected String webAddress;

    @Persistent
    @Column(name = "ID_TS")
    protected UUID idTS;

	@Column(name = "TXTSEARCH", length = 8000)
    protected String txtsearch;

    @Column(name = "PRINT_IN_ENGLISH")
    protected Boolean printInEnglish;

    @OnDelete(DeletePolicy.DENY)
    @OneToMany(mappedBy = "company")
    protected List<CallCampaignTarget> targets;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "extCompany")
    protected List<ExtTask> tasks;

    @Column(name = "ACTIVITY_STATUS")
    protected Integer activityStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "ACTUALIZATION_DATE")
    protected Date actualizationDate;

    @JoinTable(name = "CRM_COMPANY_COM_TURNOVER_TYPE_EXT_COMPANY_LINK",
        joinColumns = @JoinColumn(name = "EXT_COMPANY_ID"),
        inverseJoinColumns = @JoinColumn(name = "COMPANY_COM_TURNOVER_TYPE_ID"))
    @ManyToMany
    protected List<CompanyComTurnoverType> companyComTurnoverTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_SCALE_ID")
    protected CompanyScale companyScale;
    @OneToMany(mappedBy = "company")
    protected List<LineOfBusinessCompaniesFlags> linesOfBusinessFlags;

    @Transient
    @MetaProperty
    protected List<BaseActivity> allActivities;

    @Transient
    @MetaProperty
    protected List<CallCampaignTrgt> callCampaignTargets;

    @Transient
    @MetaProperty
    protected List<EmailCampaignTarget> emailCampaignTargets;

    public void setEmailCampaignTargets(List<EmailCampaignTarget> emailCampaignTargets) {
        this.emailCampaignTargets = emailCampaignTargets;
    }

    public List<EmailCampaignTarget> getEmailCampaignTargets() {
        return emailCampaignTargets;
    }


    public void setAllActivities(List<BaseActivity> allActivities) {
        this.allActivities = allActivities;
    }

    public List<BaseActivity> getAllActivities() {
        return allActivities;
    }

    public void setCallCampaignTargets(List<CallCampaignTrgt> callCampaignTargets) {
        this.callCampaignTargets = callCampaignTargets;
    }

    public List<CallCampaignTrgt> getCallCampaignTargets() {
        return callCampaignTargets;
    }

    public void setTxtsearch(String txtsearch) {
        this.txtsearch = txtsearch;
    }

    public String getTxtsearch() {
        return txtsearch;
	}

    public void setLinesOfBusinessFlags(List<LineOfBusinessCompaniesFlags> linesOfBusinessFlags) {
        this.linesOfBusinessFlags = linesOfBusinessFlags;
    }

    public List<LineOfBusinessCompaniesFlags> getLinesOfBusinessFlags() {
        return linesOfBusinessFlags;
    }

    public void setCompanyScale(CompanyScale companyScale) {
        this.companyScale = companyScale;
    }

    public CompanyScale getCompanyScale() {
        return companyScale;
    }

    public void setCompanyComTurnoverTypes(List<CompanyComTurnoverType> companyComTurnoverTypes) {
        this.companyComTurnoverTypes = companyComTurnoverTypes;
    }

    public List<CompanyComTurnoverType> getCompanyComTurnoverTypes() {
        return companyComTurnoverTypes;
    }

    public void setActivityStatus(CompanyActivityStatusEnum activityStatus) {
        this.activityStatus = activityStatus == null ? null : activityStatus.getId();
    }

    public CompanyActivityStatusEnum getActivityStatus() {
        return activityStatus == null ? null : CompanyActivityStatusEnum.fromId(activityStatus);
    }

    public void setActualizationDate(Date actualizationDate) {
        this.actualizationDate = actualizationDate;
    }

    public Date getActualizationDate() {
        return actualizationDate;
    }

    public void setTasks(List<ExtTask> tasks) {
        this.tasks = tasks;
    }

    public List<ExtTask> getTasks() {
        return tasks;
    }

    public void setTargets(List<CallCampaignTarget> targets) {
        this.targets = targets;
    }

    public List<CallCampaignTarget> getTargets() {
        return targets;
    }

    public void setPrintInEnglish(Boolean printInEnglish) {
        this.printInEnglish = printInEnglish;
    }

    public Boolean getPrintInEnglish() {
        return printInEnglish;
    }

    public void setIdTS(UUID idTS) {
        this.idTS = idTS;
    }

    public UUID getIdTS() {
        return idTS;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public List<InformationSource> getInformationSources() {
        return informationSources;
    }

    public void setInformationSources(List<InformationSource> informationSources) {
        this.informationSources = informationSources;
    }

    public void setOkvds(List<Okvd> okvds) {
        this.okvds = okvds;
    }

    public List<Okvd> getOkvds() {
        return okvds;
    }

    public List<LineOfBusiness> getLinesOfBusiness() {
        return linesOfBusiness;
    }

    public void setLinesOfBusiness(List<LineOfBusiness> linesOfBusiness) {
        this.linesOfBusiness = linesOfBusiness;
    }

    public void setProjects(List<CompanyProjects> projects) {
        this.projects = projects;
    }

    public List<CompanyProjects> getProjects() {
        return projects;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setParentCompany(ExtCompany parentCompany) {
        this.parentCompany = parentCompany;
    }

    public ExtCompany getParentCompany() {
        return parentCompany;
    }

    public CompanyTypeEnum getCompanyType() {
        return companyType == null ? null : CompanyTypeEnum.fromId(companyType);
    }

    public void setCompanyType(CompanyTypeEnum companyType) {
        this.companyType = companyType == null ? null : companyType.getId();
    }

    public void setExtLegalAddress(Address extLegalAddress) {
        this.extLegalAddress = extLegalAddress;
    }

    public Address getExtLegalAddress() {
        return extLegalAddress;
    }

    public void setWeb(List<WebAddress> web) {
        this.web = web;
    }

    public List<WebAddress> getWeb() {
        return web;
    }

    public void setEmails(List<EmailAddress> emails) {
        this.emails = emails;
    }

    public List<EmailAddress> getEmails() {
        return emails;
    }

    public void setLegalForm(FormOfIncorporation legalForm) {
        this.legalForm = legalForm;
    }

    public FormOfIncorporation getLegalForm() {
        return legalForm;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public void setCallFrom(Date callFrom) {
        this.callFrom = callFrom;
    }

    public Date getCallFrom() {
        return callFrom;
    }

    public void setCallTo(Date callTo) {
        this.callTo = callTo;
    }

    public Date getCallTo() {
        return callTo;
    }

    public void setExtFactAddress(Address extFactAddress) {
        this.extFactAddress = extFactAddress;
    }

    public Address getExtFactAddress() {
        return extFactAddress;
    }

    public void setExtPostalAddress(Address extPostalAddress) {
        this.extPostalAddress = extPostalAddress;
    }

    public Address getExtPostalAddress() {
        return extPostalAddress;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setRepeatLegalAddressTop(Boolean repeatLegalAddressTop) {
        this.repeatLegalAddressTop = repeatLegalAddressTop;
    }

    public Boolean getRepeatLegalAddressTop() {
        return repeatLegalAddressTop;
    }

    public void setRepeatLegalAddressTof(Boolean repeatLegalAddressTof) {
        this.repeatLegalAddressTof = repeatLegalAddressTof;
    }

    public Boolean getRepeatLegalAddressTof() {
        return repeatLegalAddressTof;
    }

    public void setIsNotActual(Boolean isNotActual) {
        this.isNotActual = isNotActual;
    }

    public Boolean getIsNotActual() {
        return isNotActual;
    }

    public void setReasonForIrrelevance(String reasonForIrrelevance) {
        this.reasonForIrrelevance = reasonForIrrelevance;
    }

    public String getReasonForIrrelevance() {
        return reasonForIrrelevance;
    }

    public void setIsResident(Boolean isResident) {
        this.isResident = isResident;
    }

    public Boolean getIsResident() {
        return isResident;
    }

    public void setCountryOfRegistration(Country countryOfRegistration) {
        this.countryOfRegistration = countryOfRegistration;
    }

    public Country getCountryOfRegistration() {
        return countryOfRegistration;
    }

    public void setEgrip(String egrip) {
        this.egrip = egrip;
    }

    public String getEgrip() {
        return egrip;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setId1cAccount(String id1cAccount) {
        this.id1cAccount = id1cAccount;
    }

    public String getId1cAccount() {
        return id1cAccount;
    }

    public void setId1cPartner(String id1cPartner) {
        this.id1cPartner = id1cPartner;
    }

    public String getId1cPartner() {
        return id1cPartner;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }
}