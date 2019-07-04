/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components.validators;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.PatternValidator;
import org.dom4j.Element;

/**
 * Created by k.khoroshilov on 06.01.2017.
 */
public class WebAddressValidator extends PatternValidator {

    private static final String WEB_ADDRESS_NAME = "(https?:\\/\\/)+([А-яёЁA-z0-9]+(\\.)+)*([-А-яёЁA-z0-9_]+\\.)+([-А-яёЁA-z0-9_]+)(:[0-9]{2,10})*(\\/(.)*)*";

    public WebAddressValidator(Element element, String messagesPack) {
        super(WEB_ADDRESS_NAME);
        message = element.attributeValue("message");
        this.messagesPack = messagesPack;
    }

    public WebAddressValidator() {
        super(WEB_ADDRESS_NAME);
    }

    @Override
    public void validate(Object value) throws ValidationException {
        if (value != null) super.validate(value);
    }
}

