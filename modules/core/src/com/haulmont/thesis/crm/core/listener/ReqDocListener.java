/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.ReqDoc;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

import java.text.SimpleDateFormat;

/**
 * @author d.ivanov
 */
@Component("crm_ReqDocListener")
public class ReqDocListener implements BeforeInsertEntityListener<ReqDoc>, BeforeUpdateEntityListener<ReqDoc> {

    protected static SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void onBeforeInsert(ReqDoc entity) {
        StringBuilder sb = new StringBuilder();

        if (entity.getTemplate()) {
            if (entity.getTemplateName() != null) {
                sb.append(entity.getTemplateName());
                sb.append("/");
            }
        }

        if (entity.getDocKind() != null) {
            sb.append(entity.getDocKind().getName());
        }

        if (entity.getDocCategory() != null) {
            sb.append("/");
            sb.append(entity.getDocCategory().getName());
        }

        if (entity.getNumber() != null && !entity.getNumber().isEmpty()) {
            sb.append(" № ");
            sb.append(entity.getNumber());
        }

        if (entity.getDate() != null) {
            sb.append(" от ");
            sb.append(SDF.format(entity.getDate()));
        }

        entity.setTheme(sb.toString());
    }


    @Override
    public void onBeforeUpdate(ReqDoc entity) {
        StringBuilder sb = new StringBuilder();

        if (entity.getTemplate()) {
            if (entity.getTemplateName() != null) {
                sb.append(entity.getTemplateName());
                sb.append("/");
            }
        }

        if (entity.getDocKind() != null) {
            sb.append(entity.getDocKind().getName());
        }

        if (entity.getDocCategory() != null) {
            sb.append("/");
            sb.append(entity.getDocCategory().getName());
        }

        if (entity.getNumber() != null && !entity.getNumber().isEmpty()) {
            sb.append(" № ");
            sb.append(entity.getNumber());
        }

        if (entity.getDate() != null) {
            sb.append(" от ");
            sb.append(SDF.format(entity.getDate()));
        }

        entity.setTheme(sb.toString());
    }


}