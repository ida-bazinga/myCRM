/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.crm.core.app.dadata.DaData;
import com.haulmont.thesis.crm.core.app.dadata.entity.*;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.entity.Address;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by d.ivanov on 21.03.2017.
 */
@ManagedBean(ICreateCompanyAndAddressDaData.NAME)
public class CreateCompanyAndAddressDaData implements ICreateCompanyAndAddressDaData {

    @Inject
    protected Metadata metadata;
    @Inject
    private CrmConfig config;
    @Inject
    private DataManager dataManager;

    private DaData daData;
    private String inn;
    protected Log log = LogFactory.getLog(CreateCompanyAndAddressDaData.class);

    public ExtCompany getCompany(String inn)
    {
        conectDaData("p");
        this.inn = inn;
        return createNewCompany();
    }

    public Address getAddress(String txtAddress)
    {
        try {
            conectDaData("s");
            com.haulmont.thesis.crm.core.app.dadata.entity.Address addressDadata = daData.cleanAddress(txtAddress);
            return loadAddress(addressDadata);

        }
        catch (Exception e)
        {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    private void conectDaData(String type)
    {
        ExternalSystem row = config.getExternelSystemDaDataStandart();
        if (!"s".equals(type)) {
            row = config.getExternelSystemDaDataSuggestion();
        }

        if (row != null) {
            daData = new DaData(row.getLogin(), row.getPassword(), row.getConnectionString());
        }
    }

    private Address loadAddress(com.haulmont.thesis.crm.core.app.dadata.entity.Address addressDadata)
    {
        if (addressDadata != null) {
            //Address address = getAdress(addressDadata.getFiasId());
            //if (address == null) {
            Address address = createAddress(addressDadata);
            //}
            return address;
        }
        else
        {
            return null;
        }
    }

    private ExtCompany createNewCompany()
    {
        try {
            ExtCompany extCompany = metadata.create(ExtCompany.class);
            final SuggestCompany suggestCompany = daData.companyByInn(this.inn);
            if (suggestCompany != null) {

                boolean isLegal = ("LEGAL".equals(suggestCompany.getSuggestCompanyData().getType()) ? true : false);
                extCompany.setCompanyType(isLegal ? CompanyTypeEnum.legal : CompanyTypeEnum.person);
                extCompany.setName(getShortNameOpf(suggestCompany)); //suggestCompany.getValue()
                extCompany.setLegalForm(suggestCompany.getSuggestCompanyData().getSuggestCompanyOpf() != null ? getFormOfIncorporation(suggestCompany.getSuggestCompanyData().getSuggestCompanyOpf().getOpf_code()) : null);
                extCompany.setFullName(suggestCompany.getSuggestCompanyData().getSuggestCompanyName().getName_full_with_opf());
                extCompany.setKpp(suggestCompany.getSuggestCompanyData().getKpp());
                extCompany.setOgrn(isLegal ? suggestCompany.getSuggestCompanyData().getOgrn() : "");
                extCompany.setOkpo(suggestCompany.getSuggestCompanyData().getOkpo());
                extCompany.setInn(suggestCompany.getSuggestCompanyData().getInn());
                extCompany.setActivityStatus(getActivityStatusEnum(suggestCompany.getSuggestCompanyData().getSuggestCompanyState().getState_status()));
                extCompany.setActualizationDate(new Date(suggestCompany.getSuggestCompanyData().getSuggestCompanyState().getState_actuality_date()));
                //адрес
                Address address = loadAddress(suggestCompany.getSuggestCompanyData().getSuggestAddres().getAddress_data());
                if(address == null) {
                    address = getAddress(suggestCompany.getSuggestCompanyData().getSuggestAddres().getAddress_value());
                }
                if(address != null) {
                    extCompany.setExtFactAddress(address);
                    extCompany.setExtLegalAddress(address);
                }
                //конец адрес
                //контактное лицо
                if (suggestCompany.getSuggestCompanyData().getSuggestCompanyManagement() != null) {
                    String managementName = suggestCompany.getSuggestCompanyData().getSuggestCompanyManagement().getManagement_name();
                    String managementPost = suggestCompany.getSuggestCompanyData().getSuggestCompanyManagement().getManagement_post();
                    List<ContactPerson> contactPersonList = new ArrayList<>();
                    //contactPersonList.add(createContactPerson(managementName, managementPost, extCompany));
                    contactPersonList.add(createContactPerson(managementName, managementPost));
                    extCompany.setContactPersons(contactPersonList);
                }
                //конец контактное лицо
                //оквед
                if (suggestCompany.getSuggestCompanyData().getOkved() != null &&
                        suggestCompany.getSuggestCompanyData().getOkvedType() != null ) {
                    Okvd okvd = getOkvd(suggestCompany.getSuggestCompanyData().getOkved(), suggestCompany.getSuggestCompanyData().getOkvedType());
                    if (okvd != null) {
                        List<Okvd> okvdList = new ArrayList<>();
                        okvdList.add(okvd);
                        extCompany.setOkvds(okvdList);
                    }
                }
                //конец оквед
            }
            return extCompany;
        }
        catch (Exception e)
        {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    private FormOfIncorporation getFormOfIncorporation(String name) {
        LoadContext loadContext = new LoadContext(FormOfIncorporation.class).setView("_local");
        loadContext.setQueryString("select e from crm$FormOfIncorporation e where e.code = :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private CompanyActivityStatusEnum getActivityStatusEnum(String name)
    {
        if ("ACTIVE".equals(name))
        {
            return CompanyActivityStatusEnum.ACTIVE;
        }
        if ("LIQUIDATING".equals(name))
        {
            return CompanyActivityStatusEnum.LIQUIDATING;
        }
        if("LIQUIDATED".equals(name))
        {
            return CompanyActivityStatusEnum.LIQUIDATED;
        }
        return null;
    }

    private Address createAddress(com.haulmont.thesis.crm.core.app.dadata.entity.Address addressDadata)
    {
        Address address = metadata.create(Address.class);
        address.setCode(addressDadata.getFiasId());
        address.setAddress_ru(bildAddress(addressDadata));
        address.setOkato(addressDadata.getOkato());
        address.setZip(addressDadata.getPostalCode());
        address.setCountry(getCountry(addressDadata.getCountry()));
        String city = addressDadata.getCity();
        String region = addressDadata.getRegion();
        if (city != null && region != null) {
            address.setRegion(getRegion(region));
            if (!city.equals(region)) {
                address.setCity(getCity(city));
            }
        }
        if (city == null || region == null) {
            if (city != null) {
                address.setCity(getCity(city));
            }
            if (region != null) {
                address.setRegion(getRegion(region));
            }
        }
        address.setRegionDistrict(addressDadata.getArea() != null ? getRegionDistrict(addressDadata.getArea()) : null);
        address.setName_ru(addressDadata.getResult() != null ? addressDadata.getResult() : bildAddressName(address));
        address.setAddressFirstDoc(addressEGRUL(addressDadata, address));
        address = dataManager.commit(address);
        return address;
    }

    private String addressEGRUL(com.haulmont.thesis.crm.core.app.dadata.entity.Address addressDadata, Address address) {
        if (inn != null && !inn.isEmpty()) {
            return addressDadata.getEGRUL();
        }
        return address.getName_ru();
    }

    private ExtContactPerson createContactPerson(Object... args)
    {
        ExtContactPerson contactPerson = metadata.create(ExtContactPerson.class);
        contactPerson.setLastName(args[0].toString());
        contactPerson.setPositionLong(args[1] != null ? args[1].toString() : "");
        //contactPerson.setCompany((ExtCompany) args[2]);
        return contactPerson;
    }

    private Country getCountry(String name) {
        LoadContext loadContext = new LoadContext(Country.class).setView("_local");
        loadContext.setQueryString("select e from crm$Country e where e.name_ru = :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private City getCity(String name) {
        LoadContext loadContext = new LoadContext(City.class).setView("_local");
        loadContext.setQueryString("select e from crm$City e where e.name_ru = :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private Region getRegion(String name) {
        LoadContext loadContext = new LoadContext(Region.class).setView("_local");
        name = "(?i)%" + name + "%";
        loadContext.setQueryString("select e from crm$Region e where e.name_ru like :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private RegionDistrict getRegionDistrict(String name) {
        LoadContext loadContext = new LoadContext(RegionDistrict.class).setView("_local");
        name = "(?i)%" + name + "%";
        loadContext.setQueryString("select e from crm$RegionDistrict e where e.name_ru like :name").setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private Okvd getOkvd(String name, String type) {
        String code = "2001".equals(type) ? name : String.format("(2) %s", name);
        LoadContext loadContext = new LoadContext(Okvd.class).setView("_local");
        loadContext.setQueryString("select e from crm$Okvd e where e.code = :code").setParameter("code", code);
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

        if (addressDadata.getFlatType() != null && addressDadata.getFlat() != null) {
            if (bildStreetAndHouseAndBlock.length() > 0) {
                bildStreetAndHouseAndBlock.append(", ");
                bildStreetAndHouseAndBlock.append(addressDadata.getFlatType());
                bildStreetAndHouseAndBlock.append(". ");
                bildStreetAndHouseAndBlock.append(addressDadata.getFlat());
            }
        }

        if(addressDadata.getUnparsedParts() != null) {
            bildStreetAndHouseAndBlock.append(", ");
            bildStreetAndHouseAndBlock.append(addressDadata.getUnparsedParts().toLowerCase());
        }
        return bildStreetAndHouseAndBlock.toString();
    }

    public String bildAddressName(Address address) {

        StringBuilder legalAddressBilder = new StringBuilder();
        if (address.getZip() != null) {
            legalAddressBilder.append(address.getZip());
        }

        if (address.getCountry() != null && StringUtils.isNotBlank(address.getCountry().getName_ru())) {
            if (legalAddressBilder.length() > 0) {
                legalAddressBilder.append(", ");
            }
            legalAddressBilder.append(address.getCountry().getName_ru());
        }

        if (address.getRegion() != null && StringUtils.isNotBlank(address.getRegion().getFullName_ru())) {
            if (legalAddressBilder.length() > 0) {
                legalAddressBilder.append(", ");
            }
            legalAddressBilder.append(address.getRegion().getFullName_ru());
        }

        if (address.getRegionDistrict() != null && StringUtils.isNotBlank(address.getRegionDistrict().getFullName_ru())) {
            if (legalAddressBilder.length() > 0) {
                legalAddressBilder.append(", ");
            }
            legalAddressBilder.append(address.getRegionDistrict().getFullName_ru());
        }

        if (address.getCity() != null && StringUtils.isNotBlank(address.getCity().getFullName_ru())) {
            if (legalAddressBilder.length() > 0) {
                legalAddressBilder.append(", ");
            }
            legalAddressBilder.append(address.getCity().getFullName_ru());
        }

        if (address.getAddress_ru() != null && StringUtils.isNotBlank(address.getAddress_ru())) {
            if (legalAddressBilder.length() > 0) {
                legalAddressBilder.append(", ");
            }
            legalAddressBilder.append(address.getAddress_ru());
        }

        return  legalAddressBilder.toString();
    }

    private String getShortNameOpf(SuggestCompany suggestCompany) {
       StringBuilder shortNameOpf = new StringBuilder();

       if (suggestCompany.getSuggestCompanyData() != null) {
           if (suggestCompany.getSuggestCompanyData().getSuggestCompanyName() != null) {
               if (suggestCompany.getSuggestCompanyData().getSuggestCompanyName().getName_short() != null) {
                   shortNameOpf.append(suggestCompany.getSuggestCompanyData().getSuggestCompanyName().getName_short());
                   if (suggestCompany.getSuggestCompanyData().getSuggestCompanyOpf() != null) {
                       shortNameOpf.append(", ");
                       shortNameOpf.append(suggestCompany.getSuggestCompanyData().getSuggestCompanyOpf().getOpf_short());
                   }
               } else {
                   shortNameOpf.append(suggestCompany.getSuggestCompanyData().getSuggestCompanyName().getName_full());
               }
           }
       }
       return shortNameOpf.toString();
    }


}
