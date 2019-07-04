
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Account complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Account">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ref" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NameFull" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="INN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="KPP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CodeOKPO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CountryCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refParentAccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AccountInfo" type="{http://efData}AccountInfo" maxOccurs="5"/>
 *         &lt;element name="AccountHistory" type="{http://efData}AccountHistory" maxOccurs="100"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Account", propOrder = {
    "ref",
    "type",
    "name",
    "nameFull",
    "inn",
    "kpp",
    "codeOKPO",
    "countryCode",
    "refParentAccount",
    "accountInfo",
    "accountHistory"
})
public class Account {

    @XmlElement(required = true)
    protected String ref;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "NameFull", required = true)
    protected String nameFull;
    @XmlElement(name = "INN", required = true)
    protected String inn;
    @XmlElement(name = "KPP", required = true)
    protected String kpp;
    @XmlElement(name = "CodeOKPO", required = true)
    protected String codeOKPO;
    @XmlElement(name = "CountryCode", required = true)
    protected String countryCode;
    @XmlElement(required = true, nillable = true)
    protected String refParentAccount;
    @XmlElement(name = "AccountInfo", required = true, nillable = true)
    protected List<AccountInfo> accountInfo;
    @XmlElement(name = "AccountHistory", required = true, nillable = true)
    protected List<AccountHistory> accountHistory;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the nameFull property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameFull() {
        return nameFull;
    }

    /**
     * Sets the value of the nameFull property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameFull(String value) {
        this.nameFull = value;
    }

    /**
     * Gets the value of the inn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINN() {
        return inn;
    }

    /**
     * Sets the value of the inn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINN(String value) {
        this.inn = value;
    }

    /**
     * Gets the value of the kpp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKPP() {
        return kpp;
    }

    /**
     * Sets the value of the kpp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKPP(String value) {
        this.kpp = value;
    }

    /**
     * Gets the value of the codeOKPO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeOKPO() {
        return codeOKPO;
    }

    /**
     * Sets the value of the codeOKPO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeOKPO(String value) {
        this.codeOKPO = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the refParentAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefParentAccount() {
        return refParentAccount;
    }

    /**
     * Sets the value of the refParentAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefParentAccount(String value) {
        this.refParentAccount = value;
    }

    /**
     * Gets the value of the accountInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountInfo }
     * 
     * 
     */
    public List<AccountInfo> getAccountInfo() {
        if (accountInfo == null) {
            accountInfo = new ArrayList<AccountInfo>();
        }
        return this.accountInfo;
    }

    /**
     * Gets the value of the accountHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountHistory }
     * 
     * 
     */
    public List<AccountHistory> getAccountHistory() {
        if (accountHistory == null) {
            accountHistory = new ArrayList<AccountHistory>();
        }
        return this.accountHistory;
    }

}
