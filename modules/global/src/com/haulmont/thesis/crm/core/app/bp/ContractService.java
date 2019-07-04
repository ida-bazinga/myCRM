/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.crm.core.app.bp.efdata.Pact;
import com.haulmont.thesis.crm.entity.Currency;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.Log1C;
import com.haulmont.thesis.crm.entity.StatusDocSales;

import javax.annotation.ManagedBean;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author d.ivanov
 * (не используется в новой верии)
 */
@Deprecated
@ManagedBean(IContractService.NAME)
public class ContractService implements IContractService {

    private static Log1C  log;
    private static String entityNameCompany = "crm$Company";
    private Work1C work1C;

    public boolean setContractOne(Log1C log)
    {
        ContractService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return one();
    }

    private boolean one ()
    {
        try {
            // обращаемся к веб-сервису и выводим результат
            String Id1C = work1C.setContract(xdto());
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
    private Pact xdto()
    {
        // 1 - получаю данные строки сущности из тезиса
        Map<String, Object> params = new HashMap<>();
        params.put("view", "edit");
        params.put("id", log.getEntityId());
        Contract row = work1C.buildQuery(Contract.class, params);

        // 2 - поиск соответсвий (маппинг)
        //************************
        IntegrationResolver integrationResolverContract = work1C.getIntegrationResolverEntityId(log.getEntityId(), row.getMetaClass().getName());
        String ref = (integrationResolverContract != null) ? integrationResolverContract.getExtId() : "";

        UUID rowCompany = (row.getContractor() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getContractor().getId();
        IntegrationResolver integrationResolverCompany = work1C.getIntegrationResolverEntityId(rowCompany, entityNameCompany);
        String companyId = (integrationResolverCompany != null) ? integrationResolverCompany.getExtId() : "00000000-0000-0000-0000-000000000000";

        UUID rowProject = (row.getProject() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getProject().getId();
        IntegrationResolver integrationResolverProject = work1C.getIntegrationResolverEntityId(rowProject, row.getProject().getMetaClass().getName());
        String projectId = (integrationResolverProject != null) ? integrationResolverProject.getExtId() : "00000000-0000-0000-0000-000000000000";
        params.clear();
        params.put("view", "1c");
        params.put("name_en", row.getCurrency().getCode());
        Currency currency = work1C.buildQuery(Currency.class, params);
        //************************
        Pact contract = new Pact();
        contract.setRef(work1C.getNullToEmpty(ref));
        contract.setCode(row.getNumber());
        contract.setDate(work1C.dateToXMLGregorianCalendar(row.getDate()));
        contract.setRefProject(projectId);
        contract.setRefAccount(companyId);
        contract.setCurrencyCode(currency.getCode());
        contract.setDescription(String.format("%s %s", row.getDescription() , row.getComment()));

        return contract;
    }

}