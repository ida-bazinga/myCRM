/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.thesis.crm.entity.Currency;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by k.khoroshilov on 25.03.2016.
 */
public interface NumberToWord {

    String NAME = "crm_NumberToWord";

    String bigDecimalToWord(BigDecimal value, Locale locale, Currency currency);

    String bigDecimalToWord(BigDecimal value, Locale locale, String currencyCode);

}
