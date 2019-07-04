/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.baseactivity;

import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.entity.BaseActivity;

public interface ActivityHolderWindow extends Window {

    <T extends BaseActivity> T getActivity();
}
