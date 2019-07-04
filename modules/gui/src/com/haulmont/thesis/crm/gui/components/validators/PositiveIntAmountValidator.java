/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components.validators;

        import com.haulmont.cuba.gui.components.Field;
        import com.haulmont.cuba.gui.components.ValidationException;

/**
 * Created by p.chizhikov on 12.03.2016.
 */
public class PositiveIntAmountValidator implements Field.Validator{
    @Override
    public void validate(Object value) throws ValidationException {
        Integer i = (Integer) value;
        if (i < 1)
            //TODO to messages
            throw new ValidationException("Значение не может быть меньше единицы!");
    }
}
