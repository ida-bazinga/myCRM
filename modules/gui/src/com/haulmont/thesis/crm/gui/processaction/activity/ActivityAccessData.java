/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.processaction.activity;

import com.haulmont.thesis.crm.entity.BaseActivity;

public interface ActivityAccessData <T extends BaseActivity> {

    void setItem(T item);

    boolean getSaveEnabled();

    boolean getGlobalEditable();
}