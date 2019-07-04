/*
 * Copyright (c) 2016 com.haulmont.thesis.core.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Messages;

import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.enums.MorphologyCase;
import org.apache.commons.lang.StringUtils;
import com.haulmont.thesis.core.entity.HasDetailedDescription;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

/**
 * @author p.chizhikov
 */
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorValue;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.thesis.core.entity.Contract;
import java.math.BigDecimal;
import javax.persistence.Column;
import com.haulmont.thesis.core.global.EntityCopyUtils;

@Deprecated
@Listeners("thesis_DocEntityListener")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("4100")
@Table(name = "CRM_VAT_DOC")
@Entity(name = "crm$VatDoc")
@EnableRestore
@TrackEditScreenHistory
public class VatDoc extends Doc implements HasDetailedDescription{
    private static final long serialVersionUID = 623360937609639676L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACT_ID")
    protected AcDoc act;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @Column(name = "TAX_SUM", precision = 15, scale = 2)
    protected BigDecimal taxSum;

    @Column(name = "FULL_SUM", precision = 15, scale = 2)
    protected BigDecimal fullSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GENERAL_DIRECTOR_ID")
    protected ExtEmployee generalDirector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHIF_ACCOUNT_ID")
    protected ExtEmployee chifAccount;

    @Column(name = "ADDITIONAL_DETAIL", length = 500)
    protected String additionalDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTEGRATION_RESOLVER_ID")
    protected IntegrationResolver integrationResolver;

    @Column(name = "PRINT_SINGLE_LINE")
    protected Boolean printSingleLine;

    public void setPrintSingleLine(Boolean printSingleLine) {
        this.printSingleLine = printSingleLine;
    }

    public Boolean getPrintSingleLine() {
        return printSingleLine;
    }


    public void setIntegrationResolver(IntegrationResolver integrationResolver) {
        this.integrationResolver = integrationResolver;
    }

    public IntegrationResolver getIntegrationResolver() {
        return integrationResolver;
    }


    public void setAct(AcDoc act) {
        this.act = act;
    }

    public AcDoc getAct() {
        return act;
    }


    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTaxSum(BigDecimal taxSum) {
        this.taxSum = taxSum;
    }

    public BigDecimal getTaxSum() {
        return taxSum;
    }

    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }

    public void setGeneralDirector(ExtEmployee generalDirector) {
        this.generalDirector = generalDirector;
    }

    public ExtEmployee getGeneralDirector() {
        return generalDirector;
    }

    public void setChifAccount(ExtEmployee chifAccount) {
        this.chifAccount = chifAccount;
    }

    public ExtEmployee getChifAccount() {
        return chifAccount;
    }

    public void setAdditionalDetail(String additionalDetail) {
        this.additionalDetail = additionalDetail;
    }

    public String getAdditionalDetail() {
        return additionalDetail;
    }


    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }


    @Override
    public void copyFrom(Doc srcDoc, Set<com.haulmont.cuba.core.entity.Entity> toCommit, boolean copySignatures,
                         boolean onlyLastAttachmentsVersion, boolean useOriginalAttachmentCreatorAndCreateTs, boolean copyAllVersionMainAttachment) {
        super.copyFrom(srcDoc, toCommit, copySignatures, onlyLastAttachmentsVersion, useOriginalAttachmentCreatorAndCreateTs, copyAllVersionMainAttachment);
        Metadata metadata = AppBeans.get(Metadata.NAME);
        MetaClass metaClass = metadata.getClassNN(getClass());
        for (MetaProperty metaProperty : metaClass.getOwnProperties()) {
            setValue(metaProperty.getName(), srcDoc.getValue(metaProperty.getName()));
        }
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