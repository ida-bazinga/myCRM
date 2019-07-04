
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
 * <p>Java class for Pay complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Pay">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ref" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="TypeOperation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BankNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BankDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="refAccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BankAccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Posted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Organization" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BankOrganization" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BuhInv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PayDetail" type="{http://efData}PayDetail" maxOccurs="10"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Pay", propOrder = {
    "ref",
    "code",
    "date",
    "typeOperation",
    "bankNumber",
    "bankDate",
    "refAccount",
    "bankAccount",
    "description",
    "note",
    "sum",
    "currencyCode",
    "posted",
    "organization",
    "bankOrganization",
    "buhInv",
    "payDetail"
})
public class Pay {

    @XmlElement(required = true)
    protected String ref;
    @XmlElement(name = "Code", required = true)
    protected String code;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "TypeOperation", required = true)
    protected String typeOperation;
    @XmlElement(name = "BankNumber", required = true)
    protected String bankNumber;
    @XmlElement(name = "BankDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar bankDate;
    @XmlElement(required = true)
    protected String refAccount;
    @XmlElement(name = "BankAccount", required = true)
    protected String bankAccount;
    @XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "Note", required = true)
    protected String note;
    @XmlElement(name = "Sum", required = true)
    protected BigDecimal sum;
    @XmlElement(name = "CurrencyCode", required = true)
    protected String currencyCode;
    @XmlElement(name = "Posted")
    protected boolean posted;
    @XmlElement(name = "Organization", required = true)
    protected String organization;
    @XmlElement(name = "BankOrganization", required = true)
    protected String bankOrganization;
    @XmlElement(name = "BuhInv", required = true)
    protected String buhInv;
    @XmlElement(name = "PayDetail", required = true, nillable = true)
    protected List<PayDetail> payDetail;

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
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
     * Gets the value of the typeOperation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeOperation() {
        return typeOperation;
    }

    /**
     * Sets the value of the typeOperation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeOperation(String value) {
        this.typeOperation = value;
    }

    /**
     * Gets the value of the bankNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankNumber() {
        return bankNumber;
    }

    /**
     * Sets the value of the bankNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankNumber(String value) {
        this.bankNumber = value;
    }

    /**
     * Gets the value of the bankDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBankDate() {
        return bankDate;
    }

    /**
     * Sets the value of the bankDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBankDate(XMLGregorianCalendar value) {
        this.bankDate = value;
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
     * Gets the value of the bankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the value of the bankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankAccount(String value) {
        this.bankAccount = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the sum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSum() {
        return sum;
    }

    /**
     * Sets the value of the sum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSum(BigDecimal value) {
        this.sum = value;
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
     * Gets the value of the posted property.
     * 
     */
    public boolean isPosted() {
        return posted;
    }

    /**
     * Sets the value of the posted property.
     * 
     */
    public void setPosted(boolean value) {
        this.posted = value;
    }

    /**
     * Gets the value of the organization property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganization(String value) {
        this.organization = value;
    }

    /**
     * Gets the value of the bankOrganization property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankOrganization() {
        return bankOrganization;
    }

    /**
     * Sets the value of the bankOrganization property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankOrganization(String value) {
        this.bankOrganization = value;
    }

    /**
     * Gets the value of the buhInv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuhInv() {
        return buhInv;
    }

    /**
     * Sets the value of the buhInv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuhInv(String value) {
        this.buhInv = value;
    }

    /**
     * Gets the value of the payDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PayDetail }
     * 
     * 
     */
    public List<PayDetail> getPayDetail() {
        if (payDetail == null) {
            payDetail = new ArrayList<PayDetail>();
        }
        return this.payDetail;
    }

}
