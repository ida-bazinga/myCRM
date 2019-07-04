
package com.haulmont.thesis.crm.core.app.bp.efwebservice1c;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.haulmont.thesis.crm.core.app.bp.efdata.Cash;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Params" type="{http://efData}Cash"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "params"
})
@XmlRootElement(name = "setCash")
public class SetCash {

    @XmlElement(name = "Params", required = true)
    protected Cash params;

    /**
     * Gets the value of the params property.
     *
     * @return
     *     possible object is
     *     {@link Cash }
     *
     */
    public Cash getParams() {
        return params;
    }

    /**
     * Sets the value of the params property.
     *
     * @param value
     *     allowed object is
     *     {@link Cash }
     *
     */
    public void setParams(Cash value) {
        this.params = value;
    }

}
