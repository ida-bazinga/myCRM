/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components.formatters;

import com.haulmont.cuba.gui.components.Formatter;
import java.math.BigDecimal;

/**
 * Created by k.khoroshilov on 28.01.2017.
 */
public class CurrencyFormatter implements Formatter<BigDecimal> {
    @Override
    public String format(BigDecimal value) {
        return (value != null) ? String.format("%,.2f", value.setScale(2, BigDecimal.ROUND_HALF_EVEN)): String.format("%,.2f",BigDecimal.valueOf(0)) ;
    }
}
