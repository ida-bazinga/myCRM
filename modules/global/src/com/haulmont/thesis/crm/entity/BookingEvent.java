package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.HasDetailedDescription;
import com.haulmont.thesis.core.global.EntityCopyUtils;
import com.haulmont.thesis.crm.enums.YearEnum;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * @author d.ivanov
 */
@Listeners({"thesis_DocEntityListener", "crm_BookingEventEntityListener"})
@NamePattern("%s %s %s|docKind,company,name")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@Table(name = "CRM_BOOKING_EVENT")
@DiscriminatorValue("6100")
@Entity(name = "crm$BookingEvent")
@EnableRestore
@TrackEditScreenHistory
public class BookingEvent extends Doc implements HasDetailedDescription {
    private static final long serialVersionUID = 7581805762204332999L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXHIBIT_SPACE_ID")
    protected ExhibitSpace exhibitSpace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THEMES_ID")
    protected ProjectTheme themes;

    @Column(name = "NAME", length = 100)
    protected String name;

    @Column(name = "FULL_NAME_RU", length = 500)
    protected String fullName_ru;

    @Column(name = "FULL_NAME_EN", length = 500)
    protected String fullName_en;

    @Temporal(TemporalType.DATE)
    @Column(name = "INSTALLATION_DATE")
    protected Date installationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEINSTALLATION_DATE")
    protected Date deinstallationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_START")
    protected Date dateStart;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FINISH")
    protected Date dateFinish;

    @Column(name = "AREA", precision = 15, scale = 2)
    protected BigDecimal area;


    @Temporal(TemporalType.DATE)
    @Column(name = "OPTION_DATE")
    protected Date optionDate;

    @Column(name = "YEAR_")
    protected Integer year;

    @Temporal(TemporalType.DATE)
    @Column(name = "PUBLISH_ALLOWED_DATE")
    protected Date publishAllowedDate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "bookingEvent")
    protected Set<BookingEventDetail> bookingEventDetail;

    public void setPublishAllowedDate(Date publishAllowedDate) {
        this.publishAllowedDate = publishAllowedDate;
    }

    public Date getPublishAllowedDate() {
        return publishAllowedDate;
    }

    public YearEnum getYear() {
        return year == null ? null : YearEnum.fromId(year);
    }

    public void setYear(YearEnum year) {
        this.year = year == null ? null : year.getId();
    }

    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setBookingEventDetail(Set<BookingEventDetail> bookingEventDetail) {
        this.bookingEventDetail = bookingEventDetail;
    }

    public Set<BookingEventDetail> getBookingEventDetail() {
        return bookingEventDetail;
    }

    public void setExhibitSpace(ExhibitSpace exhibitSpace) {
        this.exhibitSpace = exhibitSpace;
    }

    public ExhibitSpace getExhibitSpace() {
        return exhibitSpace;
    }

    public void setThemes(ProjectTheme themes) {
        this.themes = themes;
    }

    public ProjectTheme getThemes() {
        return themes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    public Date getInstallationDate() {
        return installationDate;
    }

    public void setDeinstallationDate(Date deinstallationDate) {
        this.deinstallationDate = deinstallationDate;
    }

    public Date getDeinstallationDate() {
        return deinstallationDate;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public Date getDateFinish() {
        return dateFinish;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getArea() {
        return area;
    }



    public void setOptionDate(Date optionDate) {
        this.optionDate = optionDate;
    }

    public Date getOptionDate() {
        return optionDate;
    }

    @MetaProperty(related = "optionDate")
    public Boolean getIsOption() {
        return getOptionDate() != null;
    }

    @MetaProperty(related = "publishAllowedDate")
    public Boolean getIsPublishAllowed() {
        return getPublishAllowedDate() != null;
    }

    @Override
    public void copyFrom(Doc srcDoc, Set<com.haulmont.cuba.core.entity.Entity> toCommit, boolean copySignatures,
                         boolean onlyLastAttachmentsVersion, boolean useOriginalAttachmentCreatorAndCreateTs, boolean copyAllVersionMainAttachment) {
        super.copyFrom(srcDoc, toCommit, copySignatures, onlyLastAttachmentsVersion, useOriginalAttachmentCreatorAndCreateTs, copyAllVersionMainAttachment);
        Metadata metadata = AppBeans.get(Metadata.NAME);
        MetaClass metaClass = metadata.getClassNN(getClass());
        EntityCopyUtils.copyProperties(srcDoc, this, metaClass.getOwnProperties(), toCommit);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String getDetailedDescription(Locale locale, boolean includeState, boolean includeShortInfo) {
        Messages messages = AppBeans.get(Messages.NAME);
        String dateFormat = Datatypes.getFormatStrings(locale).getDateFormat();
        String description;
        String locState = getLocState(locale);

        description =
                getDocKind().getName() + " "
                        + (StringUtils.isBlank(getNumber()) ? "" : messages.getMessage(Doc.class, "notification.number", locale) + " " + getNumber() + " ")
                        + (getDate() == null ? "" : messages.getMessage(Doc.class, "notification.from", locale) + " "
                        + new SimpleDateFormat(dateFormat).format(getDate()))
                        + (includeState && StringUtils.isNotBlank(locState) ? " [" + locState + "]" : "");


        if (includeShortInfo && (StringUtils.isNotBlank(getTheme()) || StringUtils.isNotBlank(getComment()))) {
            int length = description.length();
            int infoLength = MAX_SUBJECT_LENGTH - length;

            if (infoLength < MIN_SHORT_INFO_LENGTH) return description;

            String shortInfo = StringUtils.defaultIfBlank(getTheme(), getComment());
            return description + " - " + StringUtils.abbreviate(shortInfo, infoLength) + " - ";
        } else {
            return description;
        }
    }


}