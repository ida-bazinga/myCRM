/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.crm.core.app.bp.efdata.Account;
import com.haulmont.thesis.crm.core.app.bp.efdata.AccountHistory;
import com.haulmont.thesis.crm.core.app.bp.efdata.AccountInfo;
import com.haulmont.thesis.crm.entity.*;

import javax.annotation.ManagedBean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author d.ivanov
 */
@ManagedBean(IAccountService.NAME)
public class AccountService implements IAccountService {

    private static Log1C  log;
    private Work1C work1C;

    public boolean setAccountOne(Log1C log) {
        AccountService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return One();
    }

    private boolean One () {
        try {
            // обращаемся к веб-сервису и выводим результат
            String Id1C = work1C.setAccount(xdto());
            // проверка запись в маппинг
            log.setExtId(Id1C);
            work1C.setIntegrationResolver(log.getEntityId(), log.getEntityName(), StatusDocSales.IN1C, Id1C);
            // запись в лог
            work1C.setLog1c(log, false);
            return true;
        }
        catch (Exception e) {
            log.setError(e.toString());
            work1C.setLog1c(log, true);
            return false;
        }
    }

    //заполняю данными XDTO веб-сервиса
    private Account xdto () {
        // 1 - получаю данные строки сущности из тезиса
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", log.getEntityId());
        ExtCompany row = work1C.buildQuery(ExtCompany.class, params);
        //ExtCompany row = work1C.dataManagerLoad(ExtCompany.class, entityNameCompany, "1c", "id", log.getEntityId());
        // 2 - поиск соответсвий (маппинг)
        //************************
        IntegrationResolver integrationResolverCompany = work1C.getIntegrationResolverEntityId(log.getEntityId(), row.getMetaClass().getName());
        String ref = (integrationResolverCompany != null) ? integrationResolverCompany.getExtId() : "";
        String countryCode = (row.getExtLegalAddress() != null) ? row.getExtLegalAddress().getCountry().getCode() : "";
        String companyType = "ЮридическоеЛицо";
        String companyParentRef = "";
        if (row.getCompanyType().equals(CompanyTypeEnum.person)) {
            companyType = "ФизическоеЛицо";
        }
        if (row.getCompanyType().equals(CompanyTypeEnum.branch)) {
            companyType = "ОбособленноеПодразделение";
            IntegrationResolver integrationResolverCompanyParent = work1C.getIntegrationResolverEntityId(row.getParentCompany().getId(), row.getMetaClass().getName());
            companyParentRef = (integrationResolverCompanyParent != null) ? integrationResolverCompanyParent.getExtId() : "";
        }
        if (row.getCompanyType().equals(CompanyTypeEnum.government)) {
            companyType = "ГосударственныйОрган";
        }
        //************************
        Account account = new Account();
        account.setRef(work1C.getNullToEmpty(ref));
        account.setName(work1C.getNullToEmpty(row.getName()));
        account.setNameFull(work1C.getNullToEmpty(row.getFullName()));
        account.setINN(work1C.getNullToEmpty(row.getInn()));
        account.setKPP(work1C.getNullToEmpty(row.getKpp()));
        account.setCodeOKPO(work1C.getNullToEmpty(row.getOkpo()));
        account.setCountryCode(work1C.getNullToEmpty(countryCode));
        account.setRefParentAccount(companyParentRef);
        account.setType(companyType);
        //AccountInfo
        account.getAccountInfo().add(getAccountInfo("ЮрАдрес", work1C.validate(row.getExtLegalAddress()) ? row.getExtLegalAddress().getAddressFirstDoc() : ""));
        account.getAccountInfo().add(getAccountInfo("ФактАдрес", work1C.validate(row.getExtFactAddress()) ? row.getExtFactAddress().getAddressFirstDoc() : ""));
        //AccountHistory
        for (HistoryCompanyCells history : getHistoryCompanyCellsCurrent()) {
            account.getAccountHistory().add(getAccountHistory(history.getCompanyCells(), history.getStartDate(), history.getItem()));
        }
        if (account.getAccountHistory().size() == 0) {
            account.getAccountHistory().add(null);
        }

        return account;
    }

    private AccountInfo getAccountInfo (String type, String address) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setType(type);
        accountInfo.setValue(address);
        return accountInfo;
    }

    private AccountHistory getAccountHistory(CompanyCellsEnum companyCellsEnum, Date statrtDate, String value) {
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setCompanyCells(companyCellsEnum.getId());
        accountHistory.setDateStart(work1C.dateToXMLGregorianCalendar(statrtDate));
        accountHistory.setValue(value);
        return accountHistory;
    }

    protected List<HistoryCompanyCells> getHistoryCompanyCellsCurrent() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "historyCompanyCells");
        params.put("company.id", log.getEntityId());
        params.put("sort", "order by e.startDate");
        return work1C.buildQueryList(HistoryCompanyCells.class, params);
    }


}