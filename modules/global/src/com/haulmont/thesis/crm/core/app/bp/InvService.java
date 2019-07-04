/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.crm.core.app.bp.efdata.Doc;
import com.haulmont.thesis.crm.core.app.bp.efdata.TDetail;
import com.haulmont.thesis.crm.entity.*;

import javax.annotation.ManagedBean;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author d.ivanov
 */
@ManagedBean(IInvService.NAME)
public class InvService implements IInvService {

    private static Log1C  log;
    private static String entityNameOrd = "crm$OrdDoc";
    private Work1C work1C;

    public boolean setInvOne(Log1C log)
    {
        InvService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return one();
    }

    private boolean one()
    {
        try {
            //обращаемся к веб-сервису и выводим результат
            String Id1C = work1C.setInv(xdto());
            //проверка запись в маппинг
            log.setExtId(Id1C);
            work1C.setIntegrationResolver(log.getEntityId(), log.getEntityName(), StatusDocSales.IN1C, Id1C);
            //запись в лог
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
    private Doc xdto()
    {
        // 1 - получаю данные строки сущности из тезиса
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", log.getEntityId());
        InvDoc row = work1C.buildQuery(InvDoc.class, params);
        String ref = (row.getIntegrationResolver() != null) ? row.getIntegrationResolver().getExtId() : "";
        // 2 - поиск соответсвий (маппинг)
        //************************
        UUID rowOrd = (row.getParentCard() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getParentCard().getId();
        IntegrationResolver integrationResolverOrd = work1C.getIntegrationResolverEntityId(rowOrd, entityNameOrd);
        String refOrd = (integrationResolverOrd != null) ? integrationResolverOrd.getExtId() : "";

        UUID rowCompany = (row.getCompany() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getCompany().getId();
        IntegrationResolver integrationResolverCompany = work1C.getIntegrationResolverEntityId(rowCompany, row.getCompany().getMetaClass().getName());
        String companyId = (integrationResolverCompany != null) ? integrationResolverCompany.getExtId() : "00000000-0000-0000-0000-000000000000";
        /*
        UUID rowContract = (row.getContract() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getContract().getId();
        IntegrationResolver integrationResolverContract = getIntegrationResolverEntityId(rowContract, entityNameContract);
        String contractId = (integrationResolverContract != null) ? integrationResolverContract.getExtId() : "00000000-0000-0000-0000-000000000000";
        */
        UUID rowProject = (row.getProject() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getProject().getId();
        IntegrationResolver integrationResolverProject = work1C.getIntegrationResolverEntityId(rowProject, row.getProject().getMetaClass().getName());
        String projectId = (integrationResolverProject != null) ? integrationResolverProject.getExtId() : "00000000-0000-0000-0000-000000000000";
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        StringBuilder builderContract = new StringBuilder();
        if (row.getContract() != null) {
            builderContract.append(row.getContract().getNumber());
            builderContract.append("&");
            builderContract.append(formatDate.format(row.getContract().getDate() != null ? row.getContract().getDate() : row.getDate()));
            builderContract.append("&");
            builderContract.append(work1C.getNullToEmpty(row.getContract().getDescription()) + " " + work1C.getNullToEmpty(row.getContract().getComment()));
            builderContract.append("&");
            String[] m = row.getNumber().split("/");
            builderContract.append(String.format("офр-%s", m[0]));
            builderContract.append("&");
            builderContract.append(row.getContract().getDocCategory().getCode());
        }
        String currencyCode = (row.getCurrency() != null) ? row.getCurrency().getCode() : "";
        //************************
        Doc doc = new Doc();
        doc.setRefOrd(work1C.getNullToEmpty(refOrd));
        doc.setRefInv(work1C.getNullToEmpty(ref));
        //doc.setRefAct();
        doc.setRefProject(work1C.getNullToEmpty(projectId));
        doc.setCode(work1C.getCodeFormat(row.getNumber()));
        doc.setDate(work1C.dateToXMLGregorianCalendar(row.getDateTime()));
        doc.setRefAccount(companyId);
        doc.setContract(builderContract.toString());
        doc.setCurrencyCode(currencyCode);
        doc.setDescription(work1C.getNullToEmpty(row.getAdditionalDetail()));
        doc.setTotalSum(row.getFullSum());
        //doc.setIsPosted();
        doc.setIsDel(row.getIntegrationResolver().getDel());
        //строки
        for (InvoiceDetail rowd : row.getInvoiceDetails()) {
            // 2 - поиск соответсвий (маппинг)
            //************************
            IntegrationResolver integrationResolverNomenclature = work1C.getIntegrationResolverEntityId(rowd.getProduct().getNomenclature().getId(), rowd.getProduct().getNomenclature().getMetaClass().getName());
            String nomenclatureId = (integrationResolverNomenclature != null) ? integrationResolverNomenclature.getExtId() : "00000000-0000-0000-0000-000000000000";
            //************************
            TDetail tDetail = new TDetail();
            tDetail.setRefNomenclature(nomenclatureId);
            tDetail.setDescription(work1C.getNullToEmpty(rowd.getAlternativeName_ru()));
            tDetail.setRfNDS(work1C.getNDS_1c(rowd.getProduct().getNomenclature().getTax().getCode()));
            tDetail.setAmount(rowd.getAmount());
            tDetail.setCost(rowd.getCost());
            tDetail.setSumNDS(rowd.getTaxSum());
            tDetail.setTotalSum(rowd.getTotalSum());
            doc.getTDetail().add(tDetail);
        }
        return doc;
    }
}