/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.crm.core.app.bp.efdata.Item;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.Log1C;
import com.haulmont.thesis.crm.entity.Nomenclature;
import com.haulmont.thesis.crm.entity.StatusDocSales;

import javax.annotation.ManagedBean;
import java.util.HashMap;
import java.util.Map;

/**
 * @author d.ivanov
 */
@ManagedBean(INomenclatureService.NAME)
public class NomenclatureService implements INomenclatureService {

    private static Log1C log;
    private Work1C work1C;

    public boolean setNomenclatureOne(Log1C log)
    {
        NomenclatureService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return one();
    }

    private boolean one ()
    {
        try {
            // обращаемся к веб-сервису и выводим результат
            String Id1C = work1C.setNomenclature(xdto());
            // проверка запись в маппинг
            log.setExtId(Id1C);
            work1C.setIntegrationResolver(log.getEntityId(), log.getEntityName(), StatusDocSales.IN1C, Id1C);
            // запись в лог
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
    private Item xdto()
    {
        // 1 - получаю данные строки сущности из тезиса
        Nomenclature row = getNomenclature();
        // 2 - поиск соответсвий (маппинг)
        //************************
        IntegrationResolver integrationResolverNomenclature = work1C.getIntegrationResolverEntityId(log.getEntityId(), row.getMetaClass().getName());
        String ref = (integrationResolverNomenclature != null) ? integrationResolverNomenclature.getExtId() : "";
        //************************
        Item nomenclature = new Item();
        nomenclature.setRef(work1C.getNullToEmpty(ref));
        nomenclature.setNameRu(work1C.getNullToEmpty(row.getName_ru()));
        nomenclature.setNameFullRu(work1C.getNullToEmpty(row.getPrintName_ru()));
        nomenclature.setNameEn(work1C.getNullToEmpty(row.getPrintName_en()));
        nomenclature.setDescription(work1C.getNullToEmpty(row.getComment_ru()));
        nomenclature.setUnitCode(work1C.getNullToEmpty(row.getUnit().getCode()));
        nomenclature.setRfNDS(work1C.getNDS_1c(row.getTax().getCode()));
        nomenclature.setGrCode("УТ-00009836"); //"УТ-00002621"

        return nomenclature;
    }

    private Nomenclature getNomenclature() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", log.getEntityId());
        return work1C.buildQuery(Nomenclature.class, params);
    }

}