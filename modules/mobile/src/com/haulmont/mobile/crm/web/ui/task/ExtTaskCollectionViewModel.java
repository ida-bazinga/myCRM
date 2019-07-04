/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.mobile.crm.web.ui.task;

/**
 * Created by k.khoroshilov on 03.02.2017.
 */
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.mobile.web.MobileWebConfig;
import com.haulmont.mobile.web.repository.lazy.EntityLazyLoader;
import com.haulmont.mobile.web.repository.lazy.LazyCollectionRepository;
import com.haulmont.mobile.web.ui.task.TaskCollectionViewModel;
import com.vaadin.data.util.BeanItemContainer;

/**
 * @author k.khoroshilov
 * @version $Id: ExtTaskCollectionViewModel.java 03.02.2017 k.khoroshilov $
 */
public class ExtTaskCollectionViewModel extends TaskCollectionViewModel {

    protected void initEntityRepository() {
        Metadata metadata = AppBeans.get(Metadata.NAME);
        MetaClass metaClass = metadata.getClassNN(entityName);
        EntityLazyLoader lazyLoader = new EntityLazyLoader(metaClass, viewName);
        lazyLoader.setQueryString(getQuery());
        lazyLoader.setFilterEntity(filterEntity);
        entityRepository = new LazyCollectionRepository(lazyLoader);
        MobileWebConfig webConfig = AppBeans.get(Configuration.class).getConfig(MobileWebConfig.class);
        ((LazyCollectionRepository) entityRepository).setMaxResults(webConfig.getDefaultFetchSize());
        if (beanItemContainer == null)
            beanItemContainer = new BeanItemContainer(metaClass.getJavaClass());
    }

    @Override
    protected String getQuery() {
        return "select distinct t from crm$Task t order by t.num desc";
    }
}