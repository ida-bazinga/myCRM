/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.entity.Address;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

/**
 * Created by k.khoroshilov on 21.07.2016.
 */
@ManagedBean("crm_AddressListener")
public class AddressListener implements BeforeUpdateEntityListener<Address>, BeforeInsertEntityListener<Address> {

    @Inject
    protected Persistence persistence;
    @Inject
    protected Messages messages;

    protected Log log = LogFactory.getLog(AddressListener.class);

    @Override
    public void onBeforeUpdate(Address entity) {

        if (entity.getCountry() != null) {
            if("643".equals(entity.getCountry().getCode())) { //Только для России
                StringBuilder legalAddressBilder = new StringBuilder();
                if (entity.getZip() != null) {
                    legalAddressBilder.append(entity.getZip());
                }

                if (entity.getCountry() != null && StringUtils.isNotBlank(entity.getCountry().getName_ru())) {
                    if (legalAddressBilder.length() > 0) {
                        legalAddressBilder.append(", ");
                    }
                    legalAddressBilder.append(entity.getCountry().getName_ru());
                }

                if (entity.getRegion() != null && StringUtils.isNotBlank(entity.getRegion().getFullName_ru())) {
                    if (legalAddressBilder.length() > 0) {
                        legalAddressBilder.append(", ");
                    }
                    legalAddressBilder.append(entity.getRegion().getFullName_ru());
                }

                if (entity.getRegionDistrict() != null && StringUtils.isNotBlank(entity.getRegionDistrict().getFullName_ru())) {
                    if (legalAddressBilder.length() > 0) {
                        legalAddressBilder.append(", ");
                    }
                    legalAddressBilder.append(entity.getRegionDistrict().getFullName_ru());
                }

                if (entity.getCity() != null && StringUtils.isNotBlank(entity.getCity().getFullName_ru())) {
                    if (legalAddressBilder.length() > 0) {
                        legalAddressBilder.append(", ");
                    }
                    legalAddressBilder.append(entity.getCity().getFullName_ru());
                }

                if (entity.getAddress_ru() != null && StringUtils.isNotBlank(entity.getAddress_ru())) {
                    if (legalAddressBilder.length() > 0) {
                        legalAddressBilder.append(", ");
                    }
                    legalAddressBilder.append(entity.getAddress_ru());
                }

                entity.setName_ru(legalAddressBilder.toString());
                entity.setAddressFirstDoc(entity.getAddressFirstDoc() == null ? legalAddressBilder.toString() : entity.getAddressFirstDoc());
            }
        }
    }

    @Override
    public void onBeforeInsert(Address entity) {
        onBeforeUpdate(entity);
    }

}
