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
@ManagedBean(IAcService.NAME)
public class AcService implements IAcService {

    private static Log1C  log;
    private static String entityNameOrd = "crm$OrdDoc";
    private static String entityNameInv = "crm$InvDoc";
    private Work1C work1C;

    public boolean setAcOne(Log1C log) {
        AcService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return one();
    }

    private boolean one()
    {
        try {
            //обращаемся к веб-сервису и выводим результат
            String Id1C = work1C.setAc(xdto());
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
        AcDoc row = work1C.buildQuery(AcDoc.class, params);
        String ref = (row.getIntegrationResolver() != null) ? row.getIntegrationResolver().getExtId() : "";
        // 2 - поиск соответсвий (маппинг)
        //************************
        UUID ordId = null, invId = null;
        if ("crm$OrdDoc".equals(row.getParentCard().getCategory().getEntityType())) {
            ordId = row.getParentCard().getId();
        } else {
            invId = row.getParentCard().getId();
            ordId = row.getParentCard().getParentCard().getId();
        }

        UUID rowOrd = (row.getParentCard() == null || ordId == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : ordId;
        IntegrationResolver integrationResolverOrd = work1C.getIntegrationResolverEntityId(rowOrd, entityNameOrd);
        String refOrd = (integrationResolverOrd != null) ? integrationResolverOrd.getExtId() : "00000000-0000-0000-0000-000000000000";

        UUID rowInv = (row.getParentCard() == null || invId == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : invId;
        IntegrationResolver integrationResolverInv = work1C.getIntegrationResolverEntityId(rowInv, entityNameInv);
        String refInv = (integrationResolverInv != null) ? integrationResolverInv.getExtId() : "00000000-0000-0000-0000-000000000000";

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
        String currencyCode = row.getCurrency() != null ? row.getCurrency().getCode() : "";
        //************************
        Doc doc = new Doc();
        doc.setRefOrd(work1C.getNullToEmpty(refOrd));
        doc.setRefInv(work1C.getNullToEmpty(refInv));
        doc.setRefAct(work1C.getNullToEmpty(ref));
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
        for (ActDetail rowd : row.getAcDetails()) {
            // 2 - поиск соответсвий (маппинг)
            //************************
            IntegrationResolver integrationResolverNomenclature = work1C.getIntegrationResolverEntityId(rowd.getProduct().getNomenclature().getId(), rowd.getProduct().getNomenclature().getMetaClass().getName());
            String nomenclatureId = work1C.validate(integrationResolverNomenclature) ? integrationResolverNomenclature.getExtId() : "00000000-0000-0000-0000-000000000000";
            //************************
            TDetail tDetail = new TDetail();
            tDetail.setRefNomenclature(nomenclatureId);
            tDetail.setDescription(work1C.getNullToEmpty(rowd.getAlternativeName_ru()));
            tDetail.setRfNDS(work1C.getNDS_1c(rowd.getProduct().getNomenclature().getTax().getCode()));
            tDetail.setAmount(rowd.getAmount());
            tDetail.setCost(rowd.getCost());
            tDetail.setSumNDS(rowd.getTaxSum());
            tDetail.setTotalSum(rowd.getTotalSum());
            tDetail.setIsAgency(rowd.getIsAgency() != null ? rowd.getIsAgency() : false);
            //агентские
            if (tDetail.isIsAgency()) {
                UUID detCompany = work1C.validate(rowd.getCompany()) ? rowd.getCompany().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
                IntegrationResolver irDetCompany = work1C.getIntegrationResolverEntityId(detCompany, rowd.getCompany().getMetaClass().getName());
                String detCompanyId = work1C.validate(irDetCompany) ? irDetCompany.getExtId() : "00000000-0000-0000-0000-000000000000";
                tDetail.setRefAccount(detCompanyId);
                tDetail.setContract(rowd.getContract().getNumber());
            }


            doc.getTDetail().add(tDetail);
        }
        return doc;
    }
}