/*
 * Copyright (c) 2017 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.AcDoc;
import com.haulmont.thesis.crm.entity.ActDoc;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

/**
 * @author k.khoroshilov
 */
@ManagedBean("crm_SalesActEntityListener")
public class SalesActEntityListener implements BeforeInsertEntityListener<AcDoc>, BeforeUpdateEntityListener<AcDoc> {

    @Inject
    protected Persistence persistence;
    @Inject
    protected Messages messages;

    @Override
    public void onBeforeUpdate(AcDoc entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (CollectionUtils.containsAny(fields, Arrays.asList("number", "company", "project", "currency", "fullSum"))) {
            StringBuilder builder = new StringBuilder();

            builder.append(messages.getTools().getEntityCaption(entity.getMetaClass()));

            if (entity.getNumber() != null) {
                builder.append(" № ");
                builder.append(entity.getNumber());
            }

            if (entity.getFullSum() != null && entity.getCurrency() != null) {
                builder.append(", ");
                builder.append(String.format("%,.2f %s",  entity.getFullSum().setScale(2, BigDecimal.ROUND_HALF_EVEN)
                        , entity.getCurrency().getName_ru()));
            }

            if (entity.getCompany() != null) {
                builder.append(", ");
                builder.append(entity.getCompany().getName());
            }

            if (entity.getProject() != null) {
                builder.append(" [");
                builder.append(entity.getProject().getName());
                builder.append("]");
            }
            entity.setTheme(builder.toString());
            entity.setDescription(builder.toString());
        }
    }

    @Override
    public void onBeforeInsert(AcDoc entity) {
        onBeforeUpdate(entity);
    }
}