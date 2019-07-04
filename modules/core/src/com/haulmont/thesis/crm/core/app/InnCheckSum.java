/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.entity.ExtCompany;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by d.ivanov on 10.05.2017.
 */
@ManagedBean(InnCheckSumMBean.NAME)
public class InnCheckSum implements InnCheckSumMBean {
    @Inject
    protected ValidINN validINN;
    @Inject
    private DataManager dataManager;

    protected Set<Entity> toCommit = new HashSet<>();

    @Authenticated
    public String innCheckSum(){
        int i = valInn();
        if (i > 0)
        {
            if(toCommit.size() > 0)
            {
                doCommit();
            }
        }

        return String.format("%s - контрагенты с ИНН; %s - не корректных ИНН.", i, toCommit.size());
    }
    
    private int valInn()
    {
        int i=0;
        for (ExtCompany company:getCompany()) {
            if(!validINN.isValidINN(company.getInn()))
            {
                company.setInn(null);
                toCommit.add(company);
            }
            i++;
        }

        return i;
    }

    private List<ExtCompany> getCompany()
    {
        LoadContext loadContext = new LoadContext(ExtCompany.class).setView("_local");
        loadContext.setQueryString("select e from df$Company e where e.inn is not null");
        return dataManager.loadList(loadContext);
    }

    private void doCommit()
    {
        dataManager.commit(new CommitContext(toCommit));
    }

}
