/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.thesis.crm.core.app.bp.efdata.Vat;
import com.haulmont.thesis.crm.entity.AcDoc;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.Log1C;
import com.haulmont.thesis.crm.entity.StatusDocSales;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


/**
 * @author d.ivanov
 */
@ManagedBean(IVatService.NAME)
public class VatService implements IVatService {

    private static Log1C  log;
    private static String entityNameVat = "crm$VatDoc";
    private static AcDoc acDoc;
    private Work1C work1C;
    private boolean isVatNumber;

    @Inject
    private DataManager dataManager;

    public boolean setVatOne(Log1C log)
    {
        VatService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return one();
    }

    private boolean one()
    {
        try {
            //обращаемся к веб-сервису и выводим результат
            Vat vatXdto = xdto();
            Vat vat = work1C.setVat(vatXdto);
            if (!isVatNumber) {
                acDoc.setVatnumber(vat.getCode());
                dataManager.commit(new CommitContext(acDoc));
                //проверка запись в маппинг
                work1C.setIntegrationResolver(log.getEntityId(), log.getEntityName(), StatusDocSales.IN1C, vat.getRefVat());
            }
            //запись в лог
            log.setExtId(vat.getRefVat());
            work1C.setLog1c(log, false);

            return true;
        }
        catch (Exception e)
        {
            log.setError(e.toString());
            work1C.setLog1c(log, true);

            return false;
        }
    }

    //заполняю данными XDTO веб-сервиса
    private Vat xdto()
    {
        Vat vat = new Vat();
        acDoc = getAcDoc();
        String refAct = (acDoc.getIntegrationResolver() != null) ? acDoc.getIntegrationResolver().getExtId() : null;
        IntegrationResolver integrationResolverVat = work1C.getIntegrationResolverEntityId(log.getEntityId(), entityNameVat);
        String refVat = (integrationResolverVat != null) ? integrationResolverVat.getExtId() : null;
        isVatNumber = work1C.validate(acDoc.getVatnumber());
        if (isVatNumber) {
            vat.setCode(acDoc.getVatnumber());
            vat.setRefVat(refVat);
        }
        vat.setRefAct(refAct);
        vat.setDate(work1C.dateToXMLGregorianCalendar(acDoc.getDateTime()));
        return vat;
    }

    private AcDoc getAcDoc() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", log.getEntityId());
        return work1C.buildQuery(AcDoc.class, params);
    }
}