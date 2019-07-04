/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import javax.annotation.ManagedBean;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.thesis.crm.entity.BugetItem;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;

/**
 * @author a.donskoy
 */
@ManagedBean("crm_BudgetItem")
public class BudgetItemListener implements BeforeInsertEntityListener<BugetItem>, BeforeUpdateEntityListener<BugetItem> {

     @Inject
    protected Persistence persistence;

    protected Log log = LogFactory.getLog(CityListener.class);

    @Override
    public void onBeforeUpdate(BugetItem entity) {

        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (CollectionUtils.containsAny(fields, Arrays.asList("name_ru", "code"))) {
            StringBuilder title = new StringBuilder();

            if (StringUtils.isNotBlank(entity.getCode()))
            {
                  title.append(entity.getCode());
                               
            }

            if (StringUtils.isNotBlank(entity.getName_ru())){
                 if (title.length() > 0)
                	{
                    	title.append(" ");
                	}
                title.append(entity.getName_ru());
            }
            entity.setFullName_ru(title.toString());
        }

    }


    @Override
    public void onBeforeInsert(BugetItem entity) {
        onBeforeUpdate(entity);
    }

}



