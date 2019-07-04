/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components.formatters;

import com.haulmont.cuba.gui.components.Formatter;

public class PostedFormatter implements Formatter<Boolean> {

    @Override
    public String format(Boolean value) {
        if (value != null) {
            if (value) {
                return "Да";
            } else {
                return "Нет";
            }
        } else {
            return "";
        }
    }
}
