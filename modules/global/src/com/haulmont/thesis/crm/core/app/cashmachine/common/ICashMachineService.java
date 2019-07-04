/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.core.app.cashmachine.common;

import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.CashDocument;
import com.haulmont.thesis.crm.entity.CashMachine;
import com.haulmont.thesis.crm.entity.CashMachineProduct;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author k.khoroshilov
 */
public interface ICashMachineService {

    String getStoresInfo() throws ServiceNotAvailableException;

    List<CashMachine> getDevices() throws ServiceNotAvailableException;
    void updateDevices() throws ServiceNotAvailableException;


    List<CashDocument> getSellDocs() throws ServiceNotAvailableException;
    List<CashDocument> getSellDocs(UUID storeId) throws ServiceNotAvailableException;
    List<CashDocument> getSellDocs(Date beginDate, Date endDate) throws ServiceNotAvailableException;
    List<CashDocument> getSellDocs(UUID storeId, Date beginDate, Date endDate) throws ServiceNotAvailableException;


    List<CashMachineProduct> getProducts() throws ServiceNotAvailableException;
    List<CashMachineProduct> getProducts(UUID storeId) throws ServiceNotAvailableException;


    void deleteProduct(UUID productId) throws ServiceNotAvailableException;
    void deleteProduct(UUID storeId, UUID productId) throws ServiceNotAvailableException;


    void deleteProducts() throws ServiceNotAvailableException;
    void deleteProducts(UUID storeId) throws ServiceNotAvailableException;
    void deleteProducts(Set<UUID> products) throws ServiceNotAvailableException;
    void deleteProducts(UUID storeId, Set<UUID> products) throws ServiceNotAvailableException;

    void addProducts(UUID storeId, List<CashMachineProduct> added) throws ServiceNotAvailableException;
    void addProducts(List<CashMachineProduct> added) throws ServiceNotAvailableException;
}