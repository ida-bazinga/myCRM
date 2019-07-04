/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.thesis.crm.entity.ExtCompany;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.CallActivity;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author Kirill Khoroshilov
 */
@Component("crm_CallActivityEntityListener")
public class CallActivityEntityListener implements BeforeInsertEntityListener<CallActivity>, BeforeUpdateEntityListener<CallActivity> {

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeInsert(CallActivity entity) {
        if (entity.getEndTimeFact() == null){
            entity.setEndTimeFact(AppBeans.get(TimeSource.class).currentTimestamp());
        }
        onBeforeUpdate(entity);
    }

    @Override
    public void onBeforeUpdate(CallActivity entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);
        if (fields.contains("communication")){
            if ( entity.getCommunication() != null){
                entity.setAddress(entity.getCommunication().getInstanceName());
            }else{
                entity.setContactPerson(null);
                entity.setCompany(null);
            }
        }

        if (!fields.contains("contactPerson") && fields.contains("communication") && entity.getCommunication() != null
                && entity.getCommunication().getContactPerson() != null){
            entity.setContactPerson(entity.getCommunication().getContactPerson());
            if (entity.getCommunication().getContactPerson().getCompany() != null){
                entity.setCompany((ExtCompany) entity.getCommunication().getContactPerson().getCompany());
            }
        }

        if (fields.contains("contactPerson") && !fields.contains("company") && !fields.contains("communication")){
            if (entity.getContactPerson() != null){
                entity.setCompany((ExtCompany) entity.getContactPerson().getCompany());
                entity.setCommunication(null);
            }else{
                entity.setCommunication(null);
                entity.setCompany(null);
            }
        }

        if (fields.contains("company") && !fields.contains("contactPerson") && !fields.contains("communication")){
            if (entity.getCompany() != null){
                entity.setContactPerson(null);
                entity.setCommunication(null);
            }
        }
    }
}