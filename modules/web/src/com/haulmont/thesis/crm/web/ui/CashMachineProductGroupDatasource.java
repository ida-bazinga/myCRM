/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.GroupDatasourceImpl;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.EvotorService;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.CashMachineProduct;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 10.05.2017.
 */
public class CashMachineProductGroupDatasource extends GroupDatasourceImpl<CashMachineProduct, UUID> {

    private Log log = LogFactory.getLog(CashMachineProductGroupDatasource.class);

    @Override
    protected void loadData(Map<String, Object> params) {
        detachListener(data.values());
        data.clear();

        EvotorService evotorService = AppBeans.get(EvotorService.NAME);

        try {
            for (CashMachineProduct entity : evotorService.getProducts()) {
                if (BooleanUtils.isNotFalse((Boolean) params.get("hideGroups"))){
                    if (BooleanUtils.isNotTrue(entity.getIsGroup())){
                        data.put(entity.getId(), entity);
                        attachListener(entity);
                    }
                }else{
                    data.put(entity.getId(), entity);
                    attachListener(entity);
                }
            }
        } catch (ServiceNotAvailableException e) {
            log.error(e.getMessage());
        }
    }
}
