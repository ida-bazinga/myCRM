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
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.HasDetailedDescription;
import com.haulmont.thesis.core.entity.SimpleDoc;
import com.haulmont.thesis.core.global.EntityCopyUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author p.chizhikov
 */

@NamePattern("%s|description")
@Listeners({"thesis_DocEntityListener", "crm_SalesInvoiceEntityListener"})
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@DiscriminatorValue("2100")
@Table(name = "CRM_INV_DOC")
@Entity(name = "crm$InvDoc")
@EnableRestore
@TrackEditScreenHistory
public class InvDoc extends Doc implements HasDetailedDescription{
    private static final long serialVersionUID = 6383685123167043701L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected ExtCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRACT_ID")
    protected Contract contract;

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

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "invDoc")
    protected List<InvoiceDetail> invoiceDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTEGRATION_RESOLVER_ID")
    protected IntegrationResolver integrationResolver;

    @Column(name = "PAYMENT_DESTINATION", length = 4000)
    protected String paymentDestination;

    @Column(name = "PAYMENT_VARIANT")
    protected Integer paymentVariant;

    @Column(name = "PAYMENT_TYPE")
    protected Integer paymentType;

    @Column(name = "IS_PLANNED")
    protected Boolean isPlanned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_DOC_ID")
    protected SimpleDoc budgetDoc;

    @Temporal(TemporalType.DATE)
    @Column(name = "PAYMENT_DATE")
    protected Date paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ITEM_ID")
    protected BugetItem budgetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_DEPARTMENT_ID")
    protected ExtDepartment budgetDepartment;

    @Column(name = "PRINT_SINGLE_LINE")
    protected Boolean printSingleLine;

    @Column(name = "PRINT_IN_ENGLISH")
    protected Boolean printInEnglish;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "invDoc")
    protected List<InvDocBugetDetail> invDocBugetDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ACCOUNT_ID")
    protected ExtContractorAccount companyAccount;

    @Column(name = "IS_PAYMENT_RUB", nullable = false)
    protected Boolean isPaymentRub = false;

    @Column(name = "TYPE_TRANSACTION")
    protected String typeTransaction;

    @Column(name = "KBK", length = 20)
    protected String kbk;

    @Column(name = "OKTMO", length = 11)
    protected String oktmo;

    public void setKbk(String kbk) {
        this.kbk = kbk;
    }

    public String getKbk() {
        return kbk;
    }

    public void setOktmo(String oktmo) {
        this.oktmo = oktmo;
    }

    public String getOktmo() {
        return oktmo;
    }


    public void setTypeTransaction(TypeTransactionDebiting typeTransaction) {
        this.typeTransaction = typeTransaction == null ? null : typeTransaction.getId();
    }

    public TypeTransactionDebiting getTypeTransaction() {
        return typeTransaction == null ? null : TypeTransactionDebiting.fromId(typeTransaction);
    }


    public void setIsPaymentRub(Boolean isPaymentRub) {
        this.isPaymentRub = isPaymentRub;
    }

    public Boolean getIsPaymentRub() {
        return isPaymentRub;
    }


    public List<InvDocBugetDetail> getInvDocBugetDetail() {
        return invDocBugetDetail;
    }

    public void setInvDocBugetDetail(List<InvDocBugetDetail> invDocBugetDetail) {
        this.invDocBugetDetail = invDocBugetDetail;
    }


    public void setCompanyAccount(ExtContractorAccount companyAccount) {
        this.companyAccount = companyAccount;
    }

    public ExtContractorAccount getCompanyAccount() {
        return companyAccount;
    }



    public void setPrintSingleLine(Boolean printSingleLine) {
        this.printSingleLine = printSingleLine;
    }

    public Boolean getPrintSingleLine() {
        return printSingleLine;
    }

    public void setPrintInEnglish(Boolean printInEnglish) {
        this.printInEnglish = printInEnglish;
    }

    public Boolean getPrintInEnglish() {
        return printInEnglish;
    }


    public void setPaymentVariant(PaymentVariantEnum paymentVariant) {
        this.paymentVariant = paymentVariant == null ? null : paymentVariant.getId();
    }

    public PaymentVariantEnum getPaymentVariant() {
        return paymentVariant == null ? null : PaymentVariantEnum.fromId(paymentVariant);
    }

    public void setPaymentType(PaymentsTypeEnum paymentType) {
        this.paymentType = paymentType == null ? null : paymentType.getId();
    }

    public PaymentsTypeEnum getPaymentType() {
        return paymentType == null ? null : PaymentsTypeEnum.fromId(paymentType);
    }

    public void setIsPlanned(Boolean isPlanned) {
        this.isPlanned = isPlanned;
    }

    public Boolean getIsPlanned() {
        return isPlanned;
    }

    public void setBudgetDoc(SimpleDoc budgetDoc) {
        this.budgetDoc = budgetDoc;
    }

    public SimpleDoc getBudgetDoc() {
        return budgetDoc;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setBudgetItem(BugetItem budgetItem) {
        this.budgetItem = budgetItem;
    }

    public BugetItem getBudgetItem() {
        return budgetItem;
    }

    public void setBudgetDepartment(ExtDepartment budgetDepartment) {
        this.budgetDepartment = budgetDepartment;
    }

    public ExtDepartment getBudgetDepartment() {
        return budgetDepartment;
    }


    public void setPaymentDestination(String paymentDestination) {
        this.paymentDestination = paymentDestination;
    }

    public String getPaymentDestination() {
        return paymentDestination;
    }


    public void setIntegrationResolver(IntegrationResolver integrationResolver) {
        this.integrationResolver = integrationResolver;
    }

    public IntegrationResolver getIntegrationResolver() {
        return integrationResolver;
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

    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    public List<InvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
    }


    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Contract getContract() {
        return contract;
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