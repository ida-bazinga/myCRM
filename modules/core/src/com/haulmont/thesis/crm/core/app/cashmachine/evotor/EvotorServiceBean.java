/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.core.app.cashmachine.evotor;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.ws.EvotorManager;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.CashDocument;
import com.haulmont.thesis.crm.entity.CashMachine;
import com.haulmont.thesis.crm.entity.CashMachineProduct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;

/**
 * @author k.khoroshilov
 */
@Service(EvotorService.NAME)
public class EvotorServiceBean implements EvotorService {

    @Inject
    protected EvotorManager evotorManager;

    protected Log log = LogFactory.getLog(EvotorServiceBean.class);

    @Override
    public String getStoresInfo() throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return null;
        }
        return evotorManager.getStoreInfo();
    }


    @Override
    public List<CashMachine> getDevices() throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return null;
        }

        return evotorManager.getDevices();
    }

    @Override
    public void updateDevices() throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return;
        }

        evotorManager.updateDevices();
    }

    @Override
    public List<CashDocument> getSellDocs() throws ServiceNotAvailableException {
        return this.getSellDocs(null, null);
    }

    @Override
    public List<CashDocument> getSellDocs(Date beginDate, Date endDate) throws ServiceNotAvailableException {
        List<CashDocument> result = new ArrayList<>();
        for (UUID storeId : evotorManager.getStoreUuids()) {
            result.addAll(this.getSellDocs(storeId, beginDate, endDate));
        }
        return result;
    }

    @Override
    public List<CashDocument> getSellDocs(UUID storeId) throws ServiceNotAvailableException {
        return this.getSellDocs(storeId, null,null);
    }

    @Override
    public List<CashDocument> getSellDocs(UUID storeId, Date beginDate, Date endDate) throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return null;
        }
        return evotorManager.getSellDocs(storeId, beginDate, endDate);
    }


    @Override
    public List<CashMachineProduct> getProducts() throws ServiceNotAvailableException {
        List<CashMachineProduct> result = new ArrayList<>();

        for (UUID storeId : evotorManager.getStoreUuids()) {
            result.addAll(this.getProducts(storeId));
        }

        return result;
    }

    public List<CashMachineProduct> getProducts(UUID storeId) throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return null;
        }
        return evotorManager.getProducts(storeId);
    }


    @Override
    public void deleteProduct(UUID productId) throws ServiceNotAvailableException {
        Set<UUID> stores = evotorManager.getStoreUuids();
        for (UUID storeId : stores) {
            this.deleteProduct(productId, storeId);
        }
    }

    @Override
    public void deleteProduct(UUID storeId, @Nonnull UUID productId) throws ServiceNotAvailableException {
        Preconditions.checkNotNullArgument(productId);

        Set<UUID> list = new HashSet<>();
        list.add(productId);

        this.deleteProducts(storeId, list);
    }


    @Override
    public void deleteProducts() throws ServiceNotAvailableException {
        Set<UUID> stores = evotorManager.getStoreUuids();
        for (UUID storeId : stores) {
            this.deleteProducts(storeId);
        }
    }

    @Override
    public void deleteProducts(UUID storeId) throws ServiceNotAvailableException {
        this.deleteProducts(storeId, null);
    }

    @Override
    public void deleteProducts(@Nonnull Set<UUID> products) throws ServiceNotAvailableException {
        Preconditions.checkNotNullArgument(products);

        for (UUID storeId : evotorManager.getStoreUuids()) {
                this.deleteProducts(storeId, products);
        }
    }

    @Override
    public void deleteProducts(UUID storeId, Set<UUID> products) throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return;
        }
        evotorManager.deleteProducts(storeId, products);
    }

    @Override
    public void addProducts(List<CashMachineProduct> added) throws ServiceNotAvailableException {
        for (UUID storeId : evotorManager.getStoreUuids()) {
            this.addProducts(storeId, added);
        }
    }

    @Override
    public void addProducts(UUID storeId, List<CashMachineProduct> added) throws ServiceNotAvailableException {
        if (!AppContext.isStarted()) {
            log.warn("AppContext is not started");
            return;
        }
        evotorManager.addProducts(storeId, added);
    }
}