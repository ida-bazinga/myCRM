/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.infoUserNewOrders;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;

public interface IInfoUserNewOrdersMBean {
    String NAME = "IInfoUserNewOrdersMBean";

    @ManagedOperation(description = "Отправка сообщения указаному адресату")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "address", description = "address - email получателя."),
            @ManagedOperationParameter(name = "sqlParam", description = "sqlParam - параметр для процедуры.")
    })
    String send(String address, String sqlParam);
}
