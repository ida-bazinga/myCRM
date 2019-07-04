/*
 * Copyright (c) 2016 com.haulmont.thesis.core.entity
 */
package com.haulmont.thesis.crm.entity;

import com.haulmont.chile.core.annotations.Composition;
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
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Contractor;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.HasDetailedDescription;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author p.chizhikov
 */

@Listeners({"thesis_DocEntityListener", "crm_SalesOrderEntityListener"})
@NamePattern("%s|description")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("1100")
@Table(name = "CRM_ORD_DOC")
@Entity(name = "crm$OrdDoc")
@EnableRestore
@TrackEditScreenHistory
public class OrdDoc extends Doc implements HasDetailedDescription{
    private static final long serialVersionUID = 5800942053887890708L;


    @Column(name = "NOTE", length = 1000)
    protected String note;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ordDoc")
    protected List<OrderDetail> orderDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACT_ID")
    protected Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected Currency currency;

    @Column(name = "TAX_SUM", precision = 15, scale = 2)
    protected BigDecimal taxSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    protected Tax tax;

    @Column(name = "FULL_SUM", precision = 15, scale = 2)
    protected BigDecimal fullSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTEGRATION_RESOLVER_ID")
    protected IntegrationResolver integrationResolver;

    @Column(name = "PRINT_SINGLE_LINE")
    protected Boolean printSingleLine;


    @Column(name = "PRINT_IN_ENGLISH")
    protected Boolean printInEnglish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ATTORNEY_ID")
    protected ExtContactPerson companyAttorney;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ACCOUNTABLE_ID")
    protected ExtContactPerson companyAccountable;

    @Column(name = "COMPANY_POWER_OF_ATTORNEY", length = 100)
    protected String companyPowerOfAttorney;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYER_ID")
    protected Contractor payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYER_ATTORNEY_ID")
    protected ExtContactPerson payerAttorney;

    @Column(name = "PAYER_POWER_OF_ATTORNEY", length = 100)
    protected String payerPowerOfAttorney;

    @Column(name = "TYPE_SALE", nullable = false)
    protected String typeSale;

    public void setTypeSale(TypeSaleEnum typeSale) {
        this.typeSale = typeSale == null ? null : typeSale.getId();
    }

    public TypeSaleEnum getTypeSale() {
        return typeSale == null ? null : TypeSaleEnum.fromId(typeSale);
    }


    public void setCompanyAttorney(ExtContactPerson companyAttorney) {
        this.companyAttorney = companyAttorney;
    }

    public ExtContactPerson getCompanyAttorney() {
        return companyAttorney;
    }


    public void setCompanyAccountable(ExtContactPerson companyAccountable) {
        this.companyAccountable = companyAccountable;
    }

    public ExtContactPerson getCompanyAccountable() {
        return companyAccountable;
    }

    public void setCompanyPowerOfAttorney(String companyPowerOfAttorney) {
        this.companyPowerOfAttorney = companyPowerOfAttorney;
    }

    public String getCompanyPowerOfAttorney() {
        return companyPowerOfAttorney;
    }

    public void setPayer(Contractor payer) {
        this.payer = payer;
    }

    public Contractor getPayer() {
        return payer;
    }

    public void setPayerAttorney(ExtContactPerson payerAttorney) {
        this.payerAttorney = payerAttorney;
    }

    public ExtContactPerson getPayerAttorney() {
        return payerAttorney;
    }

    public void setPayerPowerOfAttorney(String payerPowerOfAttorney) {
        this.payerPowerOfAttorney = payerPowerOfAttorney;
    }

    public String getPayerPowerOfAttorney() {
        return payerPowerOfAttorney;
    }


    public void setPrintInEnglish(Boolean printInEnglish) {
        this.printInEnglish = printInEnglish;
    }

    public Boolean getPrintInEnglish() {
        return printInEnglish;
    }


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


    public void setFullSum(BigDecimal fullSum) {
        this.fullSum = fullSum;
    }

    public BigDecimal getFullSum() {
        return fullSum;
    }


    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setTaxSum(BigDecimal taxSum) {
        this.taxSum = taxSum;
    }

    public BigDecimal getTaxSum() {
        return taxSum;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }


    public void setCompany(ExtCompany company) {
        this.company = company;
    }

    public ExtCompany getCompany() {
        return company;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return contract;
    }


    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }


    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }


    @Override
    public void copyFrom(Doc srcDoc, Set<com.haulmont.cuba.core.entity.Entity> toCommit, boolean copySignatures,
                         boolean onlyLastAttachmentsVersion, boolean useOriginalAttachmentCreatorAndCreateTs, boolean copyAllVersionMainAttachment) {
        OrdDoc orderSrcDoc = (OrdDoc) srcDoc;
        super.copyFrom(srcDoc, toCommit, copySignatures, onlyLastAttachmentsVersion, useOriginalAttachmentCreatorAndCreateTs, copyAllVersionMainAttachment);
        Metadata metadata = AppBeans.get(Metadata.NAME);
        MetaClass metaClass = metadata.getClassNN(getClass());
        if (CollectionUtils.isNotEmpty(orderSrcDoc.orderDetails)) {
            List<OrderDetail> detailList = new ArrayList<>();
            for (OrderDetail detailSrc : orderSrcDoc.getOrderDetails()) {
                OrderDetail detail = AppBeans.get(Metadata.class).create(OrderDetail.class);
                detail.setOrdDoc(this);
                detail.setProduct(detailSrc.getProduct());
                detail.setComment_ru(detailSrc.getComment_ru());
                detail.setAmount(detailSrc.getAmount());
                detail.setCost(detailSrc.getCost()); // Вызов сервиса определения цены Вычисление
                detail.setSumWithoutNds(detailSrc.getSumWithoutNds()); // Вычисление (На основе переопроеделенной цены через OrdDocTools)
                detail.setTaxSum(detailSrc.getTaxSum()); // Вычисление
                detail.setTax(detailSrc.getTax());
                detail.setTotalSum(detailSrc.getTotalSum()); // Вычисление
                detail.setAlternativeName_ru(detail.getProduct().getNomenclature().getPrintName_ru() + detail.getProduct().getNomenclature().getPublicName_ru());
                detail.setAlternativeName_en(detail.getProduct().getNomenclature().getPrintName_en());
                /*detail.setMarginPercent(null);
                detail.setDiscountPercent(null);
                detail.setMarginSum(null);
                detail.setDiscountSum(null);
                detail.setDiscountType(detailSrc.getDiscountType());*/
                detailList.add(detail);
                toCommit.add(detail);
            }
            orderDetails = detailList;
        }
        /*for (MetaProperty metaProperty : metaClass.getOwnProperties()) {
            setValue(metaProperty.getName(), srcDoc.getValue(metaProperty.getName()));
        }*/
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