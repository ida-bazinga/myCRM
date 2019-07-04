
package com.haulmont.thesis.crm.core.app.bp.efdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ParamsGeneric complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ParamsGeneric">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ref" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="EDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="isDateFind" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamsGeneric", propOrder = {
        "tName",
        "ref",
        "code",
        "sDate",
        "eDate",
        "isDateFind"
})
public class ParamsGeneric {

    @XmlElement(name = "TName", required = true)
    protected String tName;
    @XmlElement(required = true, nillable = true)
    protected String ref;
    @XmlElement(name = "Code", required = true, nillable = true)
    protected String code;
    @XmlElement(name = "SDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sDate;
    @XmlElement(name = "EDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eDate;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean isDateFind;

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
     * Gets the value of the sDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getSDate() {
        return sDate;
    }

    /**
     * Sets the value of the sDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setSDate(XMLGregorianCalendar value) {
        this.sDate = value;
    }

    /**
     * Gets the value of the eDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getEDate() {
        return eDate;
    }

    /**
     * Sets the value of the eDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setEDate(XMLGregorianCalendar value) {
        this.eDate = value;
    }

    /**
     * Gets the value of the isDateFind property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsDateFind() {
        return isDateFind;
    }

    /**
     * Sets the value of the isDateFind property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsDateFind(Boolean value) {
        this.isDateFind = value == null ? false : value;
    }

}
