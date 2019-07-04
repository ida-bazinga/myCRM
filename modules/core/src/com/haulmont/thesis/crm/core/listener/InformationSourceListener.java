/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.entity.InformationSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by k.khoroshilov on 21.07.2016.
 */
@ManagedBean("crm_InformationSourceListener")
public class InformationSourceListener implements BeforeUpdateEntityListener<InformationSource>, BeforeInsertEntityListener<InformationSource> {

    @Inject
    protected Persistence persistence;

    @Inject
    protected Messages messages;

    protected Log log = LogFactory.getLog(InformationSourceListener.class);

    @Override
    public void onBeforeUpdate(InformationSource entity) {

        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (CollectionUtils.containsAny(fields, Arrays.asList("infoType", "note", "webAddress", "project"))) {
            StringBuilder title = new StringBuilder();

            if (entity.getInfoType() != null) {
                title.append(messages.getMessage(entity.getInfoType()));
            }

            if (entity.getProject() != null){
                title.append(": ");
                title.append(entity.getProject().getName());
            }

            if (entity.getWebAddress() != null){
                title.append(": ");
                title.append(entity.getWebAddress().getLink());
            }

            String note = entity.getNote();
            if (StringUtils.isNotBlank(note)) {
                title.append(": ");
                title.append(StringUtils.trimToEmpty(note));
            }

            entity.setTitle(title.toString());
        }
    }

    @Override
    public void onBeforeInsert(InformationSource entity) {
        onBeforeUpdate(entity);
    }
}
