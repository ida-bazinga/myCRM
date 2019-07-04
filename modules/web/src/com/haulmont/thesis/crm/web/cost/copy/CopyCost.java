/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.cost.copy;

import com.haulmont.cuba.core.app.DataService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.app.NumerationService;
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.entity.Cost;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.entity.Product;

import java.util.*;

/**
 * Created by d.ivanlov on 04.12.2018.
 */
public class CopyCost {

    protected Set<Entity> toCommit = new HashSet<>();
    protected Metadata metadata;
    protected DataManager dataManager;
    protected NumerationService ns;
    protected Date startDate;
    protected ExtProject project;
    protected Set<Cost> costSet;
    protected List<Object[]> productMap20;

    public CopyCost(Map<String, Object> params) {
        this.metadata = AppBeans.get(Metadata.class);
        this.dataManager = AppBeans.get(DataManager.class);
        this.ns = AppBeans.get(NumerationService.NAME);
        this.startDate = (Date) params.get("startDate");
        this.project = (ExtProject) params.get("project");
        this.costSet = ((Set<Cost>) params.get("costSet"));
        MyUtilsService myUtilsService = AppBeans.get(MyUtilsService.class);
        this.productMap20 = myUtilsService.getProductMap20();
    }

    public boolean copy() {
        for (Cost cost:this.costSet) {
            Cost copyCost = metadata.create(Cost.class);
            String num = ns.getNextNumber("Cost");
            if (num != null) {
                copyCost.setCode(num);
            }

            copyCost.setStartDate(this.startDate);
            copyCost.setProject(this.project);
            copyCost.setCostType(cost.getCostType());
            copyCost.setProduct(getProduct(cost));
            copyCost.setPrimaryCost(cost.getPrimaryCost());
            copyCost.setPrimaryCurrency(cost.getPrimaryCurrency());
            copyCost.setSecondaryCost(cost.getSecondaryCost());
            copyCost.setSecondaryCurrency(cost.getSecondaryCurrency());
            copyCost.setTerniaryCost(cost.getTerniaryCost());
            copyCost.setTerniaryCurrency(cost.getTerniaryCurrency());
            copyCost.setComment_ru(cost.getComment_ru());
            copyCost.setComment2_ru(cost.getComment2_ru());
            copyCost.setComment3(cost.getComment3());
            copyCost.setComment_en(cost.getComment_en());

            toCommit.add(copyCost);
        }
        return doCommit();
    }

    private boolean doCommit() {
        Set<Entity> isCommit = dataManager.commit(new CommitContext(toCommit));
        return !isCommit.isEmpty();
    }


    private Product getProduct(Cost cost) {

        for (Object[] object : this.productMap20) {
            UUID productId = cost.getProduct().getId();
            UUID product18Id = UUID.fromString(object[0].toString());
            UUID product20Id = UUID.fromString(object[1].toString());
            if (product18Id.equals(productId)) {

                LoadContext loadContext = new LoadContext(Product.class)
                        .setView("_local").setId(product20Id);
                return AppBeans.get(DataService.class).load(loadContext);
            }
       }

       return cost.getProduct();
    }








}
