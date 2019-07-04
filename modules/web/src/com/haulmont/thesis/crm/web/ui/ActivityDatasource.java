/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.core.app.ThesisConstants;
import com.haulmont.thesis.core.config.ThesisConfig;
import com.haulmont.thesis.crm.entity.BaseActivity;
import com.haulmont.thesis.web.ui.CardBrowserDatasource;

/**
 * Created by k.khoroshilov on 19.02.2017.
 */
public class ActivityDatasource<T extends BaseActivity> extends CardBrowserDatasource<T> {

    @Override
    protected void prepareLoadContext(LoadContext context) {
        super.prepareLoadContext(context);
        if (AppBeans.get(Configuration.class).getConfig(ThesisConfig.class).getUseRecompile()) {
            context.getDbHints().put(ThesisConstants.OPTION_RECOMPILE, true);
        }
    }
}
