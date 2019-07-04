
package com.haulmont.thesis.crm.core.app.bp.efdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Item complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ref" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GrCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NameRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NameFullRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NameEn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UnitCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RfNDS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Item", propOrder = {
    "ref",
    "grCode",
    "nameRu",
    "nameFullRu",
    "nameEn",
    "description",
    "unitCode",
    "rfNDS"
})
public class Item {

    @XmlElement(required = true)
    protected String ref;
    @XmlElement(name = "GrCode", required = true)
    protected String grCode;
    @XmlElement(name = "NameRu", required = true)
    protected String nameRu;
    @XmlElement(name = "NameFullRu", required = true)
    protected String nameFullRu;
    @XmlElement(name = "NameEn", required = true)
    protected String nameEn;
    @XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "UnitCode", required = true)
    protected String unitCode;
    @XmlElement(name = "RfNDS", required = true)
    protected String rfNDS;

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
     * Gets the value of the grCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrCode() {
        return grCode;
    }

    /**
     * Sets the value of the grCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrCode(String value) {
        this.grCode = value;
    }

    /**
     * Gets the value of the nameRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameRu() {
        return nameRu;
    }

    /**
     * Sets the value of the nameRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameRu(String value) {
        this.nameRu = value;
    }

    /**
     * Gets the value of the nameFullRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameFullRu() {
        return nameFullRu;
    }

    /**
     * Sets the value of the nameFullRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameFullRu(String value) {
        this.nameFullRu = value;
    }

    /**
     * Gets the value of the nameEn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameEn() {
        return nameEn;
    }

    /**
     * Sets the value of the nameEn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameEn(String value) {
        this.nameEn = value;
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
     * Gets the value of the unitCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitCode() {
        return unitCode;
    }

    /**
     * Sets the value of the unitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitCode(String value) {
        this.unitCode = value;
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

}
