/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Created by d.ivanov on 15.12.2016.
 */
@ManagedResource(description = "Сервис 1С БП3.0")
public interface ProcessingServices1cMBean {
    String NAME = "IProcessingServices1c";

    @ManagedOperation(description = "Отправка из лога_1С одну запись")
    String CatalogAndDocProcessing();
    @ManagedOperation(description = "Загрузить платежи из 1С в CRM")
    String PaymentProcessing();
    @ManagedOperation(description = "Пересчитать суммы платежей. Обязательно выполнить после загрузки платежей!")
    String CalculateSumPaymentProcessing();
    @ManagedOperation(description = "Получить статусы документов из 1С (проведен, удален)")
    String StatusProcessing();
    @ManagedOperation(description = "Отправить в 1С данные ПКО")
    String PKOProcessing();
    @ManagedOperation(description = "Обновить дату закрытого периода из 1С")
    String DataDisable();
    @ManagedOperation(description = "Получить статусы для расходных счетов и платежных поручений из 1С")
    String PayOrdStatusProcessing();

}
