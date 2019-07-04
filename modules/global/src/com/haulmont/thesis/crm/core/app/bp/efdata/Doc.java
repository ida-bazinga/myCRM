
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
 * <p>Java class for Doc complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Doc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="refOrd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refInv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refAct" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refProject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="refAccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Contract" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TotalSum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="IsPosted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsDel" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="TDetail" type="{http://efData}TDetail" maxOccurs="99999"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Doc", propOrder = {
        "refOrd",
        "refInv",
        "refAct",
        "refProject",
        "code",
        "date",
        "refAccount",
        "contract",
        "currencyCode",
        "description",
        "totalSum",
        "isPosted",
        "isDel",
        "tDetail"
})
public class Doc {

    @XmlElement(required = true, nillable = true)
    protected String refOrd;
    @XmlElement(required = true, nillable = true)
    protected String refInv;
    @XmlElement(required = true, nillable = true)
    protected String refAct;
    @XmlElement(required = true, nillable = true)
    protected String refProject;
    @XmlElement(name = "Code", required = true)
    protected String code;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(required = true)
    protected String refAccount;
    @XmlElement(name = "Contract", required = true)
    protected String contract;
    @XmlElement(name = "CurrencyCode", required = true)
    protected String currencyCode;
    @XmlElement(name = "Description", required = true, nillable = true)
    protected String description;
    @XmlElement(name = "TotalSum", required = true)
    protected BigDecimal totalSum;
    @XmlElement(name = "IsPosted", required = true, type = Boolean.class, nillable = true)
    protected Boolean isPosted;
    @XmlElement(name = "IsDel", required = true, type = Boolean.class, nillable = true)
    protected Boolean isDel;
    @XmlElement(name = "TDetail", required = true, nillable = true)
    protected List<TDetail> tDetail;

    /**
     * Gets the value of the refOrd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefOrd() {
        return refOrd;
    }

    /**
     * Sets the value of the refOrd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefOrd(String value) {
        this.refOrd = value;
    }

    /**
     * Gets the value of the refInv property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefInv() {
        return refInv;
    }

    /**
     * Sets the value of the refInv property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefInv(String value) {
        this.refInv = value;
    }

    /**
     * Gets the value of the refAct property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefAct() {
        return refAct;
    }

    /**
     * Sets the value of the refAct property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefAct(String value) {
        this.refAct = value;
    }

    /**
     * Gets the value of the refProject property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefProject() {
        return refProject;
    }

    /**
     * Sets the value of the refProject property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefProject(String value) {
        this.refProject = value;
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
     * Gets the value of the isPosted property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsPosted() {
        return isPosted;
    }

    /**
     * Sets the value of the isPosted property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsPosted(Boolean value) {
        this.isPosted = value;
    }

    /**
     * Gets the value of the isDel property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsDel() {
        return isDel;
    }

    /**
     * Sets the value of the isDel property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsDel(Boolean value) {
        this.isDel = value;
    }

    /**
     * Gets the value of the tDetail property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tDetail property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTDetail().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDetail }
     *
     *
     */
    public List<TDetail> getTDetail() {
        if (tDetail == null) {
            tDetail = new ArrayList<TDetail>();
        }
        return this.tDetail;
    }

}