
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TDetail complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="refNomenclature" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RfNDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Cost" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="SumNDS" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="TotalSum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="IsAgency" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="refAccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Contract" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TDetail", propOrder = {
        "refNomenclature",
        "description",
        "rfNDS",
        "amount",
        "cost",
        "sumNDS",
        "totalSum",
        "isAgency",
        "refAccount",
        "contract"
})
public class TDetail {

    @XmlElement(required = true)
    protected String refNomenclature;
    @XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "RfNDS", required = true, nillable = true)
    protected String rfNDS;
    @XmlElement(name = "Amount", required = true)
    protected BigDecimal amount;
    @XmlElement(name = "Cost", required = true)
    protected BigDecimal cost;
    @XmlElement(name = "SumNDS", required = true)
    protected BigDecimal sumNDS;
    @XmlElement(name = "TotalSum", required = true)
    protected BigDecimal totalSum;
    @XmlElement(name = "IsAgency")
    protected boolean isAgency;
    @XmlElement(required = true, nillable = true)
    protected String refAccount;
    @XmlElement(name = "Contract", required = true, nillable = true)
    protected String contract;

    /**
     * Gets the value of the refNomenclature property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefNomenclature() {
        return refNomenclature;
    }

    /**
     * Sets the value of the refNomenclature property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefNomenclature(String value) {
        this.refNomenclature = value;
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
     * Gets the value of the amount property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * Gets the value of the cost property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * Sets the value of the cost property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setCost(BigDecimal value) {
        this.cost = value;
    }

    /**
     * Gets the value of the sumNDS property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getSumNDS() {
        return sumNDS;
    }

    /**
     * Sets the value of the sumNDS property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setSumNDS(BigDecimal value) {
        this.sumNDS = value;
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
     * Gets the value of the isAgency property.
     *
     */
    public boolean isIsAgency() {
        return isAgency;
    }

    /**
     * Sets the value of the isAgency property.
     *
     */
    public void setIsAgency(boolean value) {
        this.isAgency = value;
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

}
