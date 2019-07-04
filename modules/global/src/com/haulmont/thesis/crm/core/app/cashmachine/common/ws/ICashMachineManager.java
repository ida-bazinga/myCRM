/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.ws;

import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.CashDocument;
import com.haulmont.thesis.crm.entity.CashMachine;
import com.haulmont.thesis.crm.entity.CashMachineProduct;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public interface ICashMachineManager {

    String getStoreInfo() throws ServiceNotAvailableException;
    Set<UUID> getStoreUuids() throws ServiceNotAvailableException;

    List<CashMachine> getDevices() throws ServiceNotAvailableException;
    void updateDevices() throws ServiceNotAvailableException;

    List<CashDocument> getSellDocs(@Nonnull UUID storeId, Date beginDate, Date endDate)throws ServiceNotAvailableException;

    List<CashMachineProduct> getProducts(@Nonnull UUID storeId) throws ServiceNotAvailableException;

    void deleteProducts(@Nonnull UUID storeId, Set<UUID> products) throws ServiceNotAvailableException;

    void addProducts(@Nonnull UUID storeId, @Nonnull List<CashMachineProduct> added) throws ServiceNotAvailableException;
}
