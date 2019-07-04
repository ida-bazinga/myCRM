/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.core.entity.Project;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import com.haulmont.cuba.core.entity.annotation.OnDelete;
import java.util.List;
import com.haulmont.chile.core.annotations.Composition;
import java.util.Set;

/**
 * @author p.chizhikov
 */
@NamePattern("%s|name")
@Extends(Project.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("E")
@Entity(name = "crm$Project")

public class ExtProject extends Project {
    private static final long serialVersionUID = 3780735063440876417L;

    @Column(name = "FULL_NAME_RU", length = 500)
    protected String fullName_ru;

    @Column(name = "FULL_NAME_EN", length = 500)
    protected String fullName_en;

    @Column(name = "NAME_EN", length = 100)
    protected String name_en;

    @Column(name = "CODE", length = 50)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSIBLE_ID")
    protected Employee responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected ProjectType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THEMES_ID")
    protected ProjectTheme themes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RUEF_CLASSIFIER_ID")
    protected RuefClassifier ruefClassifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXHIBIT_SPACE_ID")
    protected ExhibitSpace exhibitSpace;

    @Column(name = "APPROVED_UFI")
    protected Boolean approvedUfi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZER_ID")
    protected ExtCompany organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORMAT_ID")
    protected ProjectFormat format;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_START_PLAN")
    protected Date dateStartPlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_FINISH_PLAN")
    protected Date dateFinishPlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_START_FACT")
    protected Date dateStartFact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_FINISH_FACT")
    protected Date dateFinishFact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSTALLATION_DATE_PLAN")
    protected Date installationDatePlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSTALLATION_DATE_FACT")
    protected Date installationDateFact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEINSTALLATION_DATE_PLAN")
    protected Date deinstallationDatePlan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEINSTALLATION_DATE_FACT")
    protected Date deinstallationDateFact;

    @Column(name = "TOTAL_DURATION")
    protected Integer totalDuration;

    @Column(name = "WEB", length = 250)
    protected String web;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    @Column(name = "COMMENT_EN", length = 4000)
    protected String comment_en;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERRITORY_CLASSIFIER_ID")
    protected TerritorialClassifier territoryClassifier;

    @Column(name = "PLANNED_GROSS_AREA")
    protected BigDecimal plannedGrossArea;

    @Column(name = "EVENT_DAYS_DURATION")
    protected Integer eventDaysDuration;

    @Column(name = "INSTALLATION_DAYS_DURATION")
    protected Integer installationDaysDuration;

    @Column(name = "DEINSTALLATION_DAYS_DURATION")
    protected Integer deinstallationDaysDuration;

    @Column(name = "ID1_C", length = 50)
    protected String id1c;

    @Column(name = "IS_GROUP", nullable = false)
    protected Boolean isGroup = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_PROJECT_ID")
    protected ExtProject parentProject;






    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "extProject")
    protected List<ProjectTeam> teams;




    @JoinTable(name = "CRM_EXT_PROJECT_LINE_OF_BUSINESS_LINK",
        joinColumns = @JoinColumn(name = "EXT_PROJECT_ID"),
        inverseJoinColumns = @JoinColumn(name = "LINE_OF_BUSINESS_ID"))
    @ManyToMany
    protected List<LineOfBusiness> linesOfBusiness;


    @OneToMany(mappedBy = "project")
    protected Set<ProjectRoom> room;

    @JoinTable(name = "CRM_EXT_CONTACT_PERSON_EXT_PROJECT_LINK",
        joinColumns = @JoinColumn(name = "EXT_PROJECT_ID"),
        inverseJoinColumns = @JoinColumn(name = "EXT_CONTACT_PERSON_ID"))
    @ManyToMany
    protected Set<ExtContactPerson> extContactPersons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_PROJECT_ID")
    protected ExtProject lastProject;

    public void setLastProject(ExtProject lastProject) {
        this.lastProject = lastProject;
    }

    public ExtProject getLastProject() {
        return lastProject;
    }


    public void setExtContactPersons(Set<ExtContactPerson> extContactPersons) {
        this.extContactPersons = extContactPersons;
    }

    public Set<ExtContactPerson> getExtContactPersons() {
        return extContactPersons;
    }


    public void setRoom(Set<ProjectRoom> room) {
        this.room = room;
    }

    public Set<ProjectRoom> getRoom() {
        return room;
    }


    public void setLinesOfBusiness(List<LineOfBusiness> linesOfBusiness) {
        this.linesOfBusiness = linesOfBusiness;
    }

    public List<LineOfBusiness> getLinesOfBusiness() {
        return linesOfBusiness;
    }


    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


    public void setTeams(List<ProjectTeam> teams) {
        this.teams = teams;
    }

    public List<ProjectTeam> getTeams() {
        return teams;
    }


    public ExtProject getParentProject() {
        return parentProject;
    }

    public void setParentProject(ExtProject parentProject) {
        this.parentProject = parentProject;
    }



    public ExtCompany getOrganizer() {
        return organizer;
    }

    public void setOrganizer(ExtCompany organizer) {
        this.organizer = organizer;
    }


    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }


    public void setFullName_ru(String fullName_ru) {
        this.fullName_ru = fullName_ru;
    }

    public String getFullName_ru() {
        return fullName_ru;
    }

    public void setFullName_en(String fullName_en) {
        this.fullName_en = fullName_en;
    }

    public String getFullName_en() {
        return fullName_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setResponsible(Employee responsible) {
        this.responsible = responsible;
    }

    public Employee getResponsible() {
        return responsible;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public ProjectType getType() {
        return type;
    }

    public void setThemes(ProjectTheme themes) {
        this.themes = themes;
    }

    public ProjectTheme getThemes() {
        return themes;
    }

    public void setRuefClassifier(RuefClassifier ruefClassifier) {
        this.ruefClassifier = ruefClassifier;
    }

    public RuefClassifier getRuefClassifier() {
        return ruefClassifier;
    }

    public void setExhibitSpace(ExhibitSpace exhibitSpace) {
        this.exhibitSpace = exhibitSpace;
    }

    public ExhibitSpace getExhibitSpace() {
        return exhibitSpace;
    }

    public void setApprovedUfi(Boolean approvedUfi) {
        this.approvedUfi = approvedUfi;
    }

    public Boolean getApprovedUfi() {
        return approvedUfi;
    }

    public void setFormat(ProjectFormat format) {
        this.format = format;
    }

    public ProjectFormat getFormat() {
        return format;
    }

    public void setDateStartPlan(Date dateStartPlan) {
        this.dateStartPlan = dateStartPlan;
    }

    public Date getDateStartPlan() {
        return dateStartPlan;
    }

    public void setDateFinishPlan(Date dateFinishPlan) {
        this.dateFinishPlan = dateFinishPlan;
    }

    public Date getDateFinishPlan() {
        return dateFinishPlan;
    }

    public void setDateStartFact(Date dateStartFact) {
        this.dateStartFact = dateStartFact;
    }

    public Date getDateStartFact() {
        return dateStartFact;
    }

    public void setDateFinishFact(Date dateFinishFact) {
        this.dateFinishFact = dateFinishFact;
    }

    public Date getDateFinishFact() {
        return dateFinishFact;
    }

    public void setInstallationDatePlan(Date installationDatePlan) {
        this.installationDatePlan = installationDatePlan;
    }

    public Date getInstallationDatePlan() {
        return installationDatePlan;
    }

    public void setInstallationDateFact(Date installationDateFact) {
        this.installationDateFact = installationDateFact;
    }

    public Date getInstallationDateFact() {
        return installationDateFact;
    }

    public void setDeinstallationDatePlan(Date deinstallationDatePlan) {
        this.deinstallationDatePlan = deinstallationDatePlan;
    }

    public Date getDeinstallationDatePlan() {
        return deinstallationDatePlan;
    }

    public void setDeinstallationDateFact(Date deinstallationDateFact) {
        this.deinstallationDateFact = deinstallationDateFact;
    }

    public Date getDeinstallationDateFact() {
        return deinstallationDateFact;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getWeb() {
        return web;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_en() {
        return comment_en;
    }

    public void setTerritoryClassifier(TerritorialClassifier territoryClassifier) {
        this.territoryClassifier = territoryClassifier;
    }

    public TerritorialClassifier getTerritoryClassifier() {
        return territoryClassifier;
    }

    public void setPlannedGrossArea(BigDecimal plannedGrossArea) {
        this.plannedGrossArea = plannedGrossArea;
    }

    public BigDecimal getPlannedGrossArea() {
        return plannedGrossArea;
    }

    public void setEventDaysDuration(Integer eventDaysDuration) {
        this.eventDaysDuration = eventDaysDuration;
    }

    public Integer getEventDaysDuration() {
        return eventDaysDuration;
    }

    public void setInstallationDaysDuration(Integer installationDaysDuration) {
        this.installationDaysDuration = installationDaysDuration;
    }

    public Integer getInstallationDaysDuration() {
        return installationDaysDuration;
    }

    public void setDeinstallationDaysDuration(Integer deinstallationDaysDuration) {
        this.deinstallationDaysDuration = deinstallationDaysDuration;
    }

    public Integer getDeinstallationDaysDuration() {
        return deinstallationDaysDuration;
    }
}