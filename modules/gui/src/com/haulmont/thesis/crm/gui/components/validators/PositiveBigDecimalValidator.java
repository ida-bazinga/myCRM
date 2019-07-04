/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components.validators;

import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.ValidationException;

import java.math.BigDecimal;

/**
 * Created by p.chizhikov on 09.03.2016.
 */
public class PositiveBigDecimalValidator implements Field.Validator {
    @Override
    public void validate(Object value) throws ValidationException {
        BigDecimal i = (BigDecimal) value;
        if (i.signum() <= 0)
            //TODO to messages
           throw new ValidationException("Значение должно быть больше нуля!");
    }
}
