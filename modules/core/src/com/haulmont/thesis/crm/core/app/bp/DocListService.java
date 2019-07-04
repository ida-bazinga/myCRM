/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.core.app.bp.efdata.Doc;
import com.haulmont.thesis.crm.core.app.bp.efdata.ParamsGeneric;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@ManagedBean(IDocMBean.NAME)
public class DocListService implements IDocMBean {

    private Work1C work1C;
    private XMLGregorianCalendar xmlStartDate;
    private XMLGregorianCalendar xmlEndDate;
    protected Log logFactory = LogFactory.getLog(this.getClass());


    @Authenticated
    public String docFindDate(String entityName, String sData, String eData) {
        String info = "Start class DocListService:docFindDate";
        try {
            work1C = AppBeans.get(Work1C.class);
            logFactory.info(info);
            xmlStartDate = dateStringToXmlGregorianCalendar(sData);
            xmlEndDate = dateStringToXmlGregorianCalendar(eData);
            List<Doc> docList = xdto(entityName);
            for (Doc doc:docList) {
                info = info + System.lineSeparator() +
                        String.format("код = %s; дата = %s; refOrd = %s; refInv = %s; refAct = %s;",
                                doc.getCode(), dateStringFormat(doc.getDate()), doc.getRefOrd(), doc.getRefInv(), doc.getRefAct());
            }

            return info;
        } catch (Exception e) {
            info = String.format("Error class DocListService:DocListService. Description of the error: %s", e.toString());
            logFactory.error(info);
            return info;
        }
    }

    @Authenticated
    public List<Doc> xdto (String tName) {
        if (work1C.validate(tName, xmlStartDate, xmlEndDate)) {
            ParamsGeneric paramsGeneric = new ParamsGeneric();
            paramsGeneric.setTName(tName);
            paramsGeneric.setIsDateFind(true);
            paramsGeneric.setSDate(xmlStartDate);
            paramsGeneric.setEDate(xmlEndDate);
            return work1C.getDocList(paramsGeneric).getListResult();
        }
        return null;
    }

    private XMLGregorianCalendar dateStringToXmlGregorianCalendar(String dateString) {
        try {
            DateFormat format = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
            Date date = format.parse(dateString);
            GregorianCalendar gCalendar = new GregorianCalendar();
            gCalendar.setTime(date);
            XMLGregorianCalendar xmlCalendar =  DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
            return xmlCalendar;

        } catch (Exception e) {
            return null;
        }

    }

    private String dateStringFormat(XMLGregorianCalendar xmlDate) {
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            Date date = work1C.XMLGregorianCalendarToDate(xmlDate);
            String  dateFormat = df.format(date);
            return dateFormat;
        } catch (Exception e) {
            return null;
        }
    }
}
