
package com.haulmont.thesis.crm.core.app.bp.efdata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PayList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ListResult" type="{http://efData}Pay" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayList", propOrder = {
    "listResult"
})
public class PayList {

    @XmlElement(name = "ListResult", required = true, nillable = true)
    protected List<Pay> listResult;

    /**
     * Gets the value of the listResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pay }
     * 
     * 
     */
    public List<Pay> getListResult() {
        if (listResult == null) {
            listResult = new ArrayList<Pay>();
        }
        return this.listResult;
    }

}
