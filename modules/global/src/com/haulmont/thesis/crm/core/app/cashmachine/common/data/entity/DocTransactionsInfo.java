/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.CommodityType;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.MeasureUnit;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.PaymentType;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.TaxRate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class DocTransactionsInfo extends AbstractTransactionInfo {

    private static Log log = LogFactory.getLog(DocTransactionsInfo.class);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @SerializedName("baseDocumentNumber")
    protected String baseDocumentNumber;

    @SerializedName("baseDocumentUUID")
    protected UUID baseDocumentUUID;

    @SerializedName("clientName")
    protected String clientName;

    @SerializedName("clientPhone")
    protected String clientPhone;

    @SerializedName("couponNumber")
    protected String couponNumber;

    @SerializedName("alcoholByVolume")
    protected double alcoholByVolume;

    @SerializedName("alcoholProductKindCode")
    protected int alcoholProductKindCode;

    @SerializedName("balanceQuantity")
    protected double balanceQuantity;

    @SerializedName("barcode")
    protected String barcode;

    @SerializedName("commodityCode")
    protected String commodityCode;

    @SerializedName("commodityUuid")
    protected UUID commodityUuid;

    @SerializedName("commodityName")
    protected String commodityName;

    @SerializedName("commodityType")
    protected CommodityType commodityType;

    @SerializedName("costPrice")
    protected double costPrice;

    @SerializedName("fprintSection")
    protected String fprintSection;

    @SerializedName("mark")
    protected String mark;

    @SerializedName("measureName")
    protected MeasureUnit measureName;

    @SerializedName("tareVolume")
    protected double tareVolume;

    @SerializedName("price")
    protected double price;

    @SerializedName("quantity")
    protected double quantity;

    @SerializedName("resultPrice")
    protected double resultPrice;

    @SerializedName("resultSum")
    protected double resultSum;

    @SerializedName("sum")
    protected double sum;

    @SerializedName("positionId")
    protected String positionId;

    //@SerializedName("extraKeys")
    //protected String extraKeys;

    @SerializedName("resultTaxSum")
    protected double resultTaxSum;

    @SerializedName("tax")
    protected TaxRate tax;

    @SerializedName("taxPercent")
    protected double taxPercent;

    @SerializedName("taxRateCode")
    protected String taxRateCode;

    @SerializedName("taxSum")
    protected double taxSum;

    @SerializedName("paymentType")
    protected PaymentType paymentType;

    @SerializedName("rrn")
    protected String rrn;

    @SerializedName("documentNumber")
    protected String documentNumber;

    @SerializedName("receiptNumber")
    protected String receiptNumber;

    @SerializedName("sessionNumber")
    protected String sessionNumber;

    @SerializedName("total")
    protected double total;

    public Date getCreationDate() {
        try {
            return sdf.parse(creationDate);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String getBaseDocumentNumber() {
        return baseDocumentNumber;
    }

    public UUID getBaseDocumentUUID() {
        return baseDocumentUUID;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getCouponNumber() {
        return couponNumber;
    }

    public double getAlcoholByVolume() {
        return alcoholByVolume;
    }

    public int getAlcoholProductKindCode() {
        return alcoholProductKindCode;
    }

    public double getBalanceQuantity() {
        return balanceQuantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public UUID getCommodityUuid() {
        return commodityUuid;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public CommodityType getCommodityType() {
        return commodityType;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public String getFprintSection() {
        return fprintSection;
    }

    public String getMark() {
        return mark;
    }

    public MeasureUnit getMeasureName() {
        return measureName;
    }

    public double getTareVolume() {
        return tareVolume;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getResultPrice() {
        return resultPrice;
    }

    public double getResultSum() {
        return resultSum;
    }

    public double getSum() {
        return sum;
    }

    public String getPositionId() {
        return positionId;
    }

    //public String getExtraKeys() {
    //    return extraKeys;
    //}

    public double getResultTaxSum() {
        return resultTaxSum;
    }

    public TaxRate getTax() {
        return tax;
    }

    public double getTaxPercent() {
        return taxPercent;
    }

    public String getTaxRateCode() {
        return taxRateCode;
    }

    public double getTaxSum() {
        return taxSum;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getRrn() {
        return rrn;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public double getTotal() {
        return total;
    }

}


