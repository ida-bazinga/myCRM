/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.test;


import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Created by d.ivanov on 22.05.2018.
 */
@ManagedResource(description = "Тестирование сервисов")
public interface ITestServiceMBean {
    String NAME = "ITestServiceMBean";

    @ManagedOperation(description = "тест 1C Rest (Получение данных – метод GET)")
    String test1();

    @ManagedOperation(description = "тест 1C Rest (Создание объекта – метод POST)")
    String test2();

    @ManagedOperation(description = "тест 1C Rest (Обновление данных: " +
            "метод ***PATCH*** – в этом случае можно указывать только те свойств, которые необходимо обновить;  " +
            "метод PUT – в этом случае необходимо указывать все свойства сущности)")
    String test3();

    @ManagedOperation(description = "тест 1C Rest (Удаление данных – метод DELETE)")
    String test4();

    @ManagedOperation(description = "тест Генерация json с помощью Jackson (Example to readJson using TreeModel) ")
    String testBuildJsonAccount();

    @ManagedOperation(description = "Тест копирование помещений из \"Заявка на загрузку\" в \"Проект.помещения\"")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "bookingEventId", description = "bookingEventId - ИД заявки на загрузку")
    })
    boolean сreateProjectRoom(String bookingEventId);

    @ManagedOperation(description = "Тест. Установка статуса удаление - \"Заявка на загрузку\" и \"удаление - \"Помещений из проекта\"")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "bookingEventId", description = "bookingEventId - ИД заявки на загрузку")
    })
    boolean removeProjectRoom(String bookingEventId);

    @ManagedOperation(description = "тест UNISENDER json ")
    String testBuildJsonUnisender();

}
