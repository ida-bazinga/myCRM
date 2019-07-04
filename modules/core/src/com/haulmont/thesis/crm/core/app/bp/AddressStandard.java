/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.core.app.dadata.DaData;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by d.ivanov on 24.03.2017.
 */
@ManagedBean(AddressStandard.NAME)
public class AddressStandard implements AddressStandardMBean {

    @Inject
    private DataManager dataManager;
    @Inject
    private CrmConfig config;

    private Log log = LogFactory.getLog(AddressStandard.class);
    private DaData daData;
    private List<Address> addressList;
    protected Set<Entity> toCommit = new HashSet<>();

    @Authenticated
    public String addressStandard() {
        try {
            if(validParams())
            {
                newAddressDaData();
            }
            return "Address standardization is successful!";
        } catch (Exception e) {
            return ExceptionUtils.getStackTrace(e);
        }
    }

    private List<Address> getListAddress()
    {
        LoadContext loadContext = new LoadContext(Address.class).setView("browse");
        loadContext.setQueryString("select a from crm$Address a where a.name_ru is not null and a.code not like '%-%'").setMaxResults(100);
        return dataManager.loadList(loadContext);
    }


    private boolean validParams() throws Exception {

        ExternalSystem row = config.getExternelSystemDaDataStandart();
        if (row != null) {
            daData = new DaData(row.getLogin(), row.getPassword(), row.getConnectionString());
        }
        if (daData == null)
        {
            log.error("There are no settings to work with the service DaData");
            return false;
        }
        addressList = getListAddress();
        if (addressList == null)
        {
            log.error("No records for the treatment service DaData");
            return false;
        }
        if (addressList.size() == 0)
        {
            log.error("No records for the treatment service DaData");
            return false;
        }
        return true;
    }


    private void newAddressDaData() throws Exception {

        for (Address addres:addressList)
        {
            String firstAddress = addres.getName_ru();
            com.haulmont.thesis.crm.core.app.dadata.entity.Address address = daData.cleanAddress(addres.getName_ru());
            addres.setCode(address.getFiasId());
            addres.setAddress_ru(bildAddress(address));
            addres.setOkato(address.getOkato());
            addres.setZip(address.getPostalCode());
            addres.setCountry(getCountry(address.getCountry()));
            addres.setCity(getCity(address.getCity()));
            addres.setRegion(getRegion(address.getRegion()));
            addres.setRegionDistrict(getRegionDistrict(address.getArea()));
            addres.setComment_ru(String.format("%s %s", addres.getComment_ru(), firstAddress).trim());
            toCommit.add(addres);
        }
        dataManager.commit(new CommitContext(toCommit));
    }

    private Country getCountry(String name) {
        LoadContext loadContext = new LoadContext(Country.class).setView("_local");
        loadContext.setQueryString("select e from crm$Country e where e.name_ru like :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private City getCity(String name) {
        LoadContext loadContext = new LoadContext(City.class).setView("_local");
        loadContext.setQueryString("select e from crm$City e where e.name_ru like :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private Region getRegion(String name) {
        LoadContext loadContext = new LoadContext(Region.class).setView("_local");
        loadContext.setQueryString("select e from crm$Region e where e.name_ru like :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private RegionDistrict getRegionDistrict(String name) {
        LoadContext loadContext = new LoadContext(RegionDistrict.class).setView("_local");
        loadContext.setQueryString("select e from crm$RegionDistrict e where e.name_ru like :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private String bildAddress (com.haulmont.thesis.crm.core.app.dadata.entity.Address addressDadata)
    {
        StringBuilder bildStreetAndHouseAndBlock = new StringBuilder();
        if (addressDadata.getStreetType() != null && addressDadata.getStreet() != null) {
            bildStreetAndHouseAndBlock.append(addressDadata.getStreetType());
            bildStreetAndHouseAndBlock.append(". ");
            bildStreetAndHouseAndBlock.append(addressDadata.getStreet());
        }
        if (addressDadata.getHouseType() != null && addressDadata.getHouseType() != null) {
            if (bildStreetAndHouseAndBlock.length() > 0) {
                bildStreetAndHouseAndBlock.append(", ");
                bildStreetAndHouseAndBlock.append(addressDadata.getHouseType());
                bildStreetAndHouseAndBlock.append(". ");
                bildStreetAndHouseAndBlock.append(addressDadata.getHouse());
            }
        }
        if (addressDadata.getBlockType() != null && addressDadata.getBlock() != null) {
            if (bildStreetAndHouseAndBlock.length() > 0) {
                bildStreetAndHouseAndBlock.append(", ");
                bildStreetAndHouseAndBlock.append(addressDadata.getBlockType());
                bildStreetAndHouseAndBlock.append(". ");
                bildStreetAndHouseAndBlock.append(addressDadata.getBlock());
            }
        }
        if(addressDadata.getUnparsedParts() != null)
        {
            bildStreetAndHouseAndBlock.append(", ");
            bildStreetAndHouseAndBlock.append(addressDadata.getUnparsedParts().toLowerCase());
        }
        return bildStreetAndHouseAndBlock.toString();
    }

}
