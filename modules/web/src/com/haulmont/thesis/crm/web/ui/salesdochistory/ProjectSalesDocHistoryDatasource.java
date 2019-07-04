/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.salesdochistory;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.GroupDatasourceImpl;
import com.haulmont.thesis.crm.entity.*;

import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by k.khoroshilov on 05.02.2017.
 */
public class ProjectSalesDocHistoryDatasource extends GroupDatasourceImpl<SaleDocsHistoryInfo, UUID> {

    //protected String[] datasourceNames = {"projectDs", "companyDs", "employeeDs", "contractorDs", "correspondentDs"};
    protected String[] datasourceNames = {"projectDs", "projectsDs"};
    protected boolean listenerIsAdded = false;

    @SuppressWarnings("unchecked")
    @Override
    protected void loadData(Map<String, Object> params) {
        data.clear();

        Datasource datasource = null;
        CollectionDatasource collectionDatasource = null;

        for (String name : datasourceNames) {
            if (collectionDatasource == null && datasource == null) {
                if (getDsContext().get(name) instanceof CollectionDatasource) {
                    collectionDatasource = getDsContext().get(name);
                    if (!listenerIsAdded) {
                        assert collectionDatasource != null;
                        collectionDatasource.addListener(new CollectionDsListenerAdapter() {
                            @Override
                            public void itemChanged(Datasource ds, @Nullable Entity prevItem, @Nullable Entity item) {
                                super.itemChanged(ds, prevItem, item);
                                refresh();
                            }
                        });
                        listenerIsAdded = true;
                    }
                } else if (getDsContext().get(name) != null) {
                    datasource = getDsContext().get(name);
                }
            }
        }

        ExtProject project = null;
        if (datasource != null) {
            project = (ExtProject) datasource.getItem();
        } else if (collectionDatasource != null) {
            project = (ExtProject) collectionDatasource.getItem();
        }

        if (project == null) return;

        Set<SaleDocsHistoryInfo> saleDocsHistoryInfos = saleDocsHistoryInfos(project);

        for (SaleDocsHistoryInfo info : saleDocsHistoryInfos) {
            data.put(info.getId(), info);
        }
    }

    protected Set<SaleDocsHistoryInfo> saleDocsHistoryInfos(ExtProject project) {
        Set<SaleDocsHistoryInfo> infoSet = new HashSet<>();
        List<OrdDoc> salesOrders = loadSalesOrders(project);
        for (OrdDoc salesOrder : salesOrders) {
            SaleDocsHistoryInfo info = metadata.create(SaleDocsHistoryInfo.class);
            info.setSalesOrder(salesOrder);
            info.setCurrency(salesOrder.getCurrency());
            info.setOrderFullSum(String.format("%,.2f %s",
                    (salesOrder.getFullSum() != null ) ? salesOrder.getFullSum() : BigDecimal.valueOf(0),
                    salesOrder.getCurrency().getName_ru()));
            info.setProject((ExtProject) salesOrder.getProject());
            info.setCustomer(salesOrder.getCompany());
            info.setPayer(salesOrder.getCompany());
            info.setContract(salesOrder.getContract());
            info.setSumActFullSum(String.format("%,.2f %s",  getSumActs(salesOrder), salesOrder.getCurrency().getName_ru()));
            info.setInvoices(getInvoicesString(salesOrder));

            infoSet.add(info);
        }
        return infoSet;
    }

    protected List<OrdDoc> loadSalesOrders(ExtProject project){
        LoadContext.Query query = new LoadContext.Query("select o from crm$OrdDoc o where o.project.id = :project")
                .setParameter("project", project);

        return dataSupplier.loadList(new LoadContext(OrdDoc.class)
                .setView("browse")
                .setQuery(query));
    }

    protected String getInvoicesString(OrdDoc salesOrder){
        LoadContext.Query query = new LoadContext.Query("select o from crm$InvDoc o where o.parentCard.id = :parent")
                .setParameter("parent", salesOrder);

        List<InvDoc> salesInvoices = dataSupplier.loadList(new LoadContext(InvDoc.class)
                .setView("_local")
                .setQuery(query));
        StringBuilder sb = new StringBuilder();

        for (InvDoc doc : salesInvoices){
            sb.append(doc.getNumber());
            // TODO: 06.02.2017 форматирование даты
            //sb.append(" ");
            //sb.append(doc.getDate());
            sb.append("; ");
        }
        return sb.toString();
    }

    protected BigDecimal getSumActs(OrdDoc salesOrder){
        LoadContext.Query query = new LoadContext.Query("select o from crm$AcDoc o where o.parentCard.id = :parent")
                .setParameter("parent", salesOrder);

        List<AcDoc> salesActs = dataSupplier.loadList(new LoadContext(AcDoc.class)
                .setView("_local")
                .setQuery(query));

        BigDecimal sa = BigDecimal.valueOf(0);
        for (AcDoc doc : salesActs){
            sa = sa.add(doc.getFullSum());
        }

        return sa;
    }

}
