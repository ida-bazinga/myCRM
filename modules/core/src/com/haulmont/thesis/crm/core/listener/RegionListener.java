/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.entity.Region;
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
@ManagedBean("crm_RegionListener")
public class RegionListener implements BeforeInsertEntityListener<Region> {

    @Inject
    protected Persistence persistence;

    protected Log log = LogFactory.getLog(RegionListener.class);

    @Override
    public void onBeforeInsert(Region entity) {

        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (CollectionUtils.containsAny(fields, Arrays.asList("name_ru", "addressPartType"))) {
            StringBuilder title = new StringBuilder();

            if (entity.getName_ru() != null){
                title.append(entity.getName_ru());
            }

            if (entity.getAddressPartType() != null){
                
                if (title.length() > 0)
                	{
                    	title.append(" ");
                	}
                if (StringUtils.isNotBlank(entity.getAddressPartType().getShortName_ru()))
                    {
                        title.append(entity.getAddressPartType().getShortName_ru());
                    } 
                else
                    {
                    	title.append(entity.getAddressPartType().getName_ru());
                    }
            }

            entity.setFullName_ru(title.toString());
        }
    }

}
