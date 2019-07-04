
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Budget complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Budget">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BDDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CFO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
@XmlType(name = "Budget", propOrder = {
        "bdds",
        "cfo",
        "sum",
        "refProject"
})
public class Budget {

    @XmlElement(name = "BDDS", required = true)
    protected String bdds;
    @XmlElement(name = "CFO", required = true)
    protected String cfo;
    @XmlElement(name = "Sum", required = true)
    protected BigDecimal sum;
    @XmlElement(required = true, nillable = true)
    protected String refProject;

    /**
     * Gets the value of the bdds property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBDDS() {
        return bdds;
    }

    /**
     * Sets the value of the bdds property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBDDS(String value) {
        this.bdds = value;
    }

    /**
     * Gets the value of the cfo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCFO() {
        return cfo;
    }

    /**
     * Sets the value of the cfo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCFO(String value) {
        this.cfo = value;
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
