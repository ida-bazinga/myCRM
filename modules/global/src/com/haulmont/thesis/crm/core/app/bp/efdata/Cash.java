
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Cash complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Cash">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="RfType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RfNDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SumNDS" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="TotalSum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="refProject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cash", propOrder = {
        "date",
        "rfType",
        "rfNDS",
        "sumNDS",
        "totalSum",
        "refProject"
})
public class Cash {

    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "RfType", required = true)
    protected String rfType;
    @XmlElement(name = "RfNDS", required = true)
    protected String rfNDS;
    @XmlElement(name = "SumNDS", required = true)
    protected BigDecimal sumNDS;
    @XmlElement(name = "TotalSum", required = true)
    protected BigDecimal totalSum;
    @XmlElement(required = true)
    protected String refProject;

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

}
