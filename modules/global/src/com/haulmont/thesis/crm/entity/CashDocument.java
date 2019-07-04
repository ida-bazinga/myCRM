package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.haulmont.chile.core.annotations.MetaProperty;
import org.apache.commons.lang.time.DateUtils;
import org.apache.openjpa.persistence.Persistent;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.thesis.crm.enums.CashDocumentType;

/**
 * @author k.khoroshilov
 */
@NamePattern("[%s] %s %s %s|docType,receiptNumber,receiptCreationDateTime,receiptSum")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CASH_DOCUMENT")
@Entity(name = "crm$CashDocument")
public class CashDocument extends BaseUuidEntity implements SoftDelete {
    private static final long serialVersionUID = -8314939644520695603L;

    @Column(name = "DOC_TYPE")
    protected String docType;

    @Column(name = "DEVICE_ID", length = 50)
    protected String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CASH_MACHINE_ID")
    protected CashMachine cashMachine;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OPEN_DATE")
    protected Date openDate;

    @Column(name = "OPEN_USER_CODE")
    protected String openUserCode;

    @Persistent
    @Column(name = "OPEN_USER_UUID")
    protected UUID openUserUuid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CLOSE_DATE")
    protected Date closeDate;

    @Column(name = "CLOSE_USER_CODE")
    protected String closeUserCode;

    @Persistent
    @Column(name = "CLOSE_USER_UUID")
    protected UUID closeUserUuid;

    @Persistent
    @Column(name = "SESSION_UUID")
    protected UUID sessionUUID;

    @Column(name = "SESSION_NUMBER")
    protected Integer sessionNumber;

    @Column(name = "NUM")
    protected Integer number;

    @Column(name = "CLOSE_RESULT_SUM")
    protected Double closeResultSum;

    @Column(name = "CLOSE_SUM")
    protected Double closeSum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXHIBIT_SPACE_ID")
    protected ExhibitSpace exhibitSpace;

    @Column(name = "COMPLETE_INVENTORY")
    protected Boolean completeInventory;

    @Column(name = "CLIENT_NAME")
    protected String clientName;

    @Column(name = "CLIENT_PHONE", length = 20)
    protected String clientPhone;

    @Column(name = "COUPON_NUMBER", length = 50)
    protected String couponNumber;

    @Column(name = "CARD_PAYMENT_SUM")
    protected Double cardPaymentSum;

    @Column(name = "CASH_PAYMENT_SUM")
    protected Double cashPaymentSum;

    @Column(name = "CARD_PAYMENT_NUM", length = 50)
    protected String cardPaymentNum;

    @Column(name = "DOCUMENT_NUMBER")
    protected Integer documentNumber;

    @Column(name = "RECEIPT_NUMBER")
    protected Integer receiptNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RECEIPT_CREATION_DATE_TIME")
    protected Date receiptCreationDateTime;

    @Column(name = "RECEIPT_SUM")
    protected Double receiptSum;

    @Column(name = "QUANTITY_POSITION")
    protected Integer quantityPosition;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "cashDocument")
    protected List<CashDocumentPosition> positions;

    @Column(name = "VER", length = 10)
    protected String ver;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getVer() {
        return ver;
    }

    public void setReceiptCreationDateTime(Date receiptCreationDateTime) {
        this.receiptCreationDateTime = receiptCreationDateTime;
    }

    public Date getReceiptCreationDateTime() {
        return receiptCreationDateTime;
    }

    @MetaProperty(related = "receiptCreationDateTime")
    public Date getReceiptCreationDate() {
        return (receiptCreationDateTime == null) ? null : DateUtils.truncate(receiptCreationDateTime, Calendar.DAY_OF_MONTH);
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setDocType(CashDocumentType docType) {
        this.docType = docType == null ? null : docType.getId();
    }

    public CashDocumentType getDocType() {
        return docType == null ? null : CashDocumentType.fromId(docType);
    }

    public void setCashMachine(CashMachine cashMachine) {
        this.cashMachine = cashMachine;
    }

    public CashMachine getCashMachine() {
        return cashMachine;
    }

    public void setPositions(List<CashDocumentPosition> positions) {
        this.positions = positions;
    }

    public List<CashDocumentPosition> getPositions() {
        return positions;
    }

    public Boolean isDeleted() {
        return deleteTs != null;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    public Date getDeleteTs() {
        return deleteTs;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenUserCode(String openUserCode) {
        this.openUserCode = openUserCode;
    }

    public String getOpenUserCode() {
        return openUserCode;
    }

    public void setOpenUserUuid(UUID openUserUuid) {
        this.openUserUuid = openUserUuid;
    }

    public UUID getOpenUserUuid() {
        return openUserUuid;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseUserCode(String closeUserCode) {
        this.closeUserCode = closeUserCode;
    }

    public String getCloseUserCode() {
        return closeUserCode;
    }

    public void setCloseUserUuid(UUID closeUserUuid) {
        this.closeUserUuid = closeUserUuid;
    }

    public UUID getCloseUserUuid() {
        return closeUserUuid;
    }

    public void setSessionUUID(UUID sessionUUID) {
        this.sessionUUID = sessionUUID;
    }

    public UUID getSessionUUID() {
        return sessionUUID;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setCloseResultSum(Double closeResultSum) {
        this.closeResultSum = closeResultSum;
    }

    public Double getCloseResultSum() {
        return closeResultSum;
    }

    public void setCloseSum(Double closeSum) {
        this.closeSum = closeSum;
    }

    public Double getCloseSum() {
        return closeSum;
    }

    public void setExhibitSpace(ExhibitSpace exhibitSpace) {
        this.exhibitSpace = exhibitSpace;
    }

    public ExhibitSpace getExhibitSpace() {
        return exhibitSpace;
    }

    public void setCompleteInventory(Boolean completeInventory) {
        this.completeInventory = completeInventory;
    }

    public Boolean getCompleteInventory() {
        return completeInventory;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setCouponNumber(String couponNumber) {
        this.couponNumber = couponNumber;
    }

    public String getCouponNumber() {
        return couponNumber;
    }

    public void setCardPaymentSum(Double cardPaymentSum) {
        this.cardPaymentSum = cardPaymentSum;
    }

    public Double getCardPaymentSum() {
        return cardPaymentSum;
    }

    public void setCashPaymentSum(Double cashPaymentSum) {
        this.cashPaymentSum = cashPaymentSum;
    }

    public Double getCashPaymentSum() {
        return cashPaymentSum;
    }

    public void setCardPaymentNum(String cardPaymentNum) {
        this.cardPaymentNum = cardPaymentNum;
    }

    public String getCardPaymentNum() {
        return cardPaymentNum;
    }

    public void setReceiptNumber(Integer receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Integer getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptSum(Double receiptSum) {
        this.receiptSum = receiptSum;
    }

    public Double getReceiptSum() {
        return receiptSum;
    }

    public void setQuantityPosition(Integer quantityPosition) {
        this.quantityPosition = quantityPosition;
    }

    public Integer getQuantityPosition() {
        return quantityPosition;
    }

}