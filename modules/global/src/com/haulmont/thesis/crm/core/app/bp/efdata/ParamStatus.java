
package com.haulmont.thesis.crm.core.app.bp.efdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParamStatus complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ParamStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ref" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsPosted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsDel" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateStr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamStatus", propOrder = {
        "tName",
        "ref",
        "isPosted",
        "isDel",
        "status",
        "dateStr"
})
public class ParamStatus {

    @XmlElement(name = "TName", required = true)
    protected String tName;
    @XmlElement(required = true)
    protected String ref;
    @XmlElement(name = "IsPosted")
    protected boolean isPosted;
    @XmlElement(name = "IsDel")
    protected boolean isDel;
    @XmlElement(required = true)
    protected String status;
    @XmlElement(required = true, nillable = true)
    protected String dateStr;

    /**
     * Gets the value of the tName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTName() {
        return tName;
    }

    /**
     * Sets the value of the tName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTName(String value) {
        this.tName = value;
    }

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
     * Gets the value of the isPosted property.
     *
     */
    public boolean isIsPosted() {
        return isPosted;
    }

    /**
     * Sets the value of the isPosted property.
     *
     */
    public void setIsPosted(boolean value) {
        this.isPosted = value;
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
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the dateStr property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDateStr() {
        return dateStr;
    }

    /**
     * Sets the value of the dateStr property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDateStr(String value) {
        this.dateStr = value;
    }

}
