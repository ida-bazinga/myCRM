
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PayDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refBasisDoc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SDDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sum" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayDetail", propOrder = {
    "code",
    "refBasisDoc",
    "sdds",
    "sum"
})
public class PayDetail {

    @XmlElement(name = "Code", required = true)
    protected String code;
    @XmlElement(required = true)
    protected String refBasisDoc;
    @XmlElement(name = "SDDS", required = true)
    protected String sdds;
    @XmlElement(name = "Sum", required = true)
    protected BigDecimal sum;

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
     * Gets the value of the refBasisDoc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefBasisDoc() {
        return refBasisDoc;
    }

    /**
     * Sets the value of the refBasisDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefBasisDoc(String value) {
        this.refBasisDoc = value;
    }

    /**
     * Gets the value of the sdds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSDDS() {
        return sdds;
    }

    /**
     * Sets the value of the sdds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSDDS(String value) {
        this.sdds = value;
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

}
