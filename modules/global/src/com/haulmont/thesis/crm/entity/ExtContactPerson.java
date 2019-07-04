/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.core.enums.SexEnum;
import org.apache.openjpa.persistence.Persistent;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author k.khoroshilov
 */
@Listeners("crm_ContactPersonListener")
@NamePattern("%s|name")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Z")
@Extends(ContactPerson.class)
@Entity(name = "crm$ContactPerson")
public class ExtContactPerson extends ContactPerson {
    private static final long serialVersionUID = -8063817442044237770L;

    @Column(name = "SEX")
    protected String sex;

    @Transient
    @MetaProperty
    protected String phone1;

    @Transient
    @MetaProperty
    protected String phone2;

    @Transient
    @MetaProperty
    protected String phone3;

    @Transient
    @MetaProperty
    protected String email1;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVATAR_FILE_ID")
    protected FileDescriptor avatar;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PHOTO_FILE_ID")
    protected FileDescriptor photo;

    @JoinTable(name = "CRM_CONTACT_ROLES_CONTACT_PERSON_LINK",
        joinColumns = @JoinColumn(name = "CONTACT_PERSON_ID"),
        inverseJoinColumns = @JoinColumn(name = "CONTACT_ROLES_ID"))
    @ManyToMany
    protected List<ContactRole> contactRoles;

    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "contactPerson")
    protected List<Communication> communications;

    @Persistent
    @Column(name = "ID_TS")
    protected UUID idTS;

    @Transient
    @MetaProperty
    protected String fullName;

    @Column(name = "POSITION_LONG", length = 1000)
    protected String positionLong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID")
    protected City city;

    @Column(name = "COMPANY_NAME", length = 500)
    protected String companyName;

    @Column(name = "IS_MAIL")
    protected Boolean isMail;

    @Column(name = "IS_PROCESSING")
    protected Boolean isProcessing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGION_ID")
    protected Region region;

    @Column(name = "IS_LOCKED")
    protected Boolean isLocked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LERND_ABOUT_EXHIBITION_ID")
    protected LearnedAboutExhibition lerndAboutExhibition;

    @JoinTable(name = "CRM_EXT_CONTACT_PERSON_EXT_PROJECT_LINK",
        joinColumns = @JoinColumn(name = "EXT_CONTACT_PERSON_ID"),
        inverseJoinColumns = @JoinColumn(name = "EXT_PROJECT_ID"))
    @ManyToMany
    protected Set<ExtProject> projects;

    @OneToMany(mappedBy = "contactPerson")
    protected Set<LineOfBusinessContactPerson> lineOfBusinessContactPerson;

    public void setLineOfBusinessContactPerson(Set<LineOfBusinessContactPerson> lineOfBusinessContactPerson) {
        this.lineOfBusinessContactPerson = lineOfBusinessContactPerson;
    }

    public Set<LineOfBusinessContactPerson> getLineOfBusinessContactPerson() {
        return lineOfBusinessContactPerson;
    }

    public void setLerndAboutExhibition(LearnedAboutExhibition lerndAboutExhibition) {
        this.lerndAboutExhibition = lerndAboutExhibition;
    }

    public LearnedAboutExhibition getLerndAboutExhibition() {
        return lerndAboutExhibition;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setIsMail(Boolean isMail) {
        this.isMail = isMail;
    }

    public Boolean getIsMail() {
        return isMail;
    }

    public void setIsProcessing(Boolean isProcessing) {
        this.isProcessing = isProcessing;
    }

    public Boolean getIsProcessing() {
        return isProcessing;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setProjects(Set<ExtProject> projects) {
        this.projects = projects;
    }

    public Set<ExtProject> getProjects() {
        return projects;
    }

    public void setPositionLong(String positionLong) {
        this.positionLong = positionLong;
    }

    public String getPositionLong() {
        return positionLong;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail1() {
        return email1;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setIdTS(UUID idTS) {
        this.idTS = idTS;
    }

    public UUID getIdTS() {
        return idTS;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone2() {
        return phone2;
    }

    public List<ContactRole> getContactRoles() {
        return contactRoles;
    }

    public void setContactRoles(List<ContactRole> contactRoles) {
        this.contactRoles = contactRoles;
    }

    public void setAvatar(FileDescriptor avatar) {
        this.avatar = avatar;
    }

    public FileDescriptor getAvatar() {
        return avatar;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex == null ? null : sex.getId();
    }

    public SexEnum getSex() {
        return sex == null ? null : SexEnum.fromId(sex);
    }

    public void setPhoto(FileDescriptor photo) {
        this.photo = photo;
    }

    public FileDescriptor getPhoto() {
        return photo;
    }

    public void setCommunications(List<Communication> communications) {
        this.communications = communications;
    }

    public List<Communication> getCommunications() {
        return communications;
    }

}