/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;


import org.springframework.jmx.export.annotation.*;

@ManagedResource(description = "Получить ИД 1С (Заказа, Счета, Акта)")
public interface IDocMBean {
    String NAME = "IDocMBean";

    @ManagedOperation(description = "Поиск документа из 1С по дате")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "entityName", description = "Название документа (ЗаявкаНаВыставку, СчетНаОплатуПокупателю, РеализацияТоваровУслуг)"),
            @ManagedOperationParameter(name = "sData", description = "ДатаН (формат даты: 20170101 10:00:00)"),
            @ManagedOperationParameter(name = "eData", description = "ДатаК (формат даты: 20170101 20:00:00)")
    })
    String docFindDate(String entityName, String sData, String eData);
}
