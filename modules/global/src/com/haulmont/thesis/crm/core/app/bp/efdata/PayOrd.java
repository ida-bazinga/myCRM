
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PayOrd complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayOrd">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RfType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="refPayOrd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refAccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ABIKSWIFT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ANumInv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Contract" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TotalSum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="RfNDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NDSSum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="PaymentDestination" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsDel" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Budget" type="{http://efData}Budget" maxOccurs="100"/>
 *         &lt;element name="KBK" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OKTMO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayOrd", propOrder = {
    "rfType",
    "number",
    "date",
    "refPayOrd",
    "refAccount",
    "abikswift",
    "aNumInv",
    "currencyCode",
    "contract",
    "totalSum",
    "rfNDS",
    "ndsSum",
    "paymentDestination",
    "isDel",
    "budget",
    "kbk",
    "oktmo"
})
public class PayOrd {

    @XmlElement(name = "RfType", required = true, nillable = true)
    protected String rfType;
    @XmlElement(name = "Number", required = true, nillable = true)
    protected String number;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(required = true, nillable = true)
    protected String refPayOrd;
    @XmlElement(required = true)
    protected String refAccount;
    @XmlElement(name = "ABIKSWIFT", required = true, nillable = true)
    protected String abikswift;
    @XmlElement(name = "ANumInv", required = true, nillable = true)
    protected String aNumInv;
    @XmlElement(name = "CurrencyCode", required = true)
    protected String currencyCode;
    @XmlElement(name = "Contract", required = true, nillable = true)
    protected String contract;
    @XmlElement(name = "TotalSum", required = true)
    protected BigDecimal totalSum;
    @XmlElement(name = "RfNDS", required = true)
    protected String rfNDS;
    @XmlElement(name = "NDSSum", required = true)
    protected BigDecimal ndsSum;
    @XmlElement(name = "PaymentDestination", required = true)
    protected String paymentDestination;
    @XmlElement(name = "IsDel")
    protected boolean isDel;
    @XmlElement(name = "Budget", required = true, nillable = true)
    protected List<Budget> budget;
    @XmlElement(name = "KBK", required = true, nillable = true)
    protected String kbk;
    @XmlElement(name = "OKTMO", required = true, nillable = true)
    protected String oktmo;

    /**
     * Gets the value of the rfType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfType() {
        return rfType;
    }

    /**
     * Sets the value of the rfType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfType(String value) {
        this.rfType = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the refPayOrd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefPayOrd() {
        return refPayOrd;
    }

    /**
     * Sets the value of the refPayOrd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefPayOrd(String value) {
        this.refPayOrd = value;
    }

    /**
     * Gets the value of the refAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefAccount() {
        return refAccount;
    }

    /**
     * Sets the value of the refAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefAccount(String value) {
        this.refAccount = value;
    }

    /**
     * Gets the value of the abikswift property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getABIKSWIFT() {
        return abikswift;
    }

    /**
     * Sets the value of the abikswift property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setABIKSWIFT(String value) {
        this.abikswift = value;
    }

    /**
     * Gets the value of the aNumInv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getANumInv() {
        return aNumInv;
    }

    /**
     * Sets the value of the aNumInv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setANumInv(String value) {
        this.aNumInv = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the contract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContract() {
        return contract;
    }

    /**
     * Sets the value of the contract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContract(String value) {
        this.contract = value;
    }

    /**
     * Gets the value of the totalSum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotalSum() {
        return totalSum;
    }

    /**
     * Sets the value of the totalSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotalSum(BigDecimal value) {
        this.totalSum = value;
    }

    /**
     * Gets the value of the rfNDS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfNDS() {
        return rfNDS;
    }

    /**
     * Sets the value of the rfNDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfNDS(String value) {
        this.rfNDS = value;
    }

    /**
     * Gets the value of the ndsSum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNDSSum() {
        return ndsSum;
    }

    /**
     * Sets the value of the ndsSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNDSSum(BigDecimal value) {
        this.ndsSum = value;
    }

    /**
     * Gets the value of the paymentDestination property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentDestination() {
        return paymentDestination;
    }

    /**
     * Sets the value of the paymentDestination property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentDestination(String value) {
        this.paymentDestination = value;
    }

    /**
     * Gets the value of the isDel property.
     * 
     */
    public boolean isIsDel() {
        return isDel;
    }

    /**
     * Sets the value of the isDel property.
     * 
     */
    public void setIsDel(boolean value) {
        this.isDel = value;
    }

    /**
     * Gets the value of the budget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the budget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBudget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Budget }
     * 
     * 
     */
    public List<Budget> getBudget() {
        if (budget == null) {
            budget = new ArrayList<Budget>();
        }
        return this.budget;
    }

    /**
     * Gets the value of the kbk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKBK() {
        return kbk;
    }

    /**
     * Sets the value of the kbk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKBK(String value) {
        this.kbk = value;
    }

    /**
     * Gets the value of the oktmo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOKTMO() {
        return oktmo;
    }

    /**
     * Sets the value of the oktmo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOKTMO(String value) {
        this.oktmo = value;
    }

}
