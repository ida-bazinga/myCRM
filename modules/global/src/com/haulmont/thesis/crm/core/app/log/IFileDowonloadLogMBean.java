/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.log;

import org.springframework.jmx.export.annotation.*;


@ManagedResource(description = "Лог загрузки данных в БД")
public interface IFileDowonloadLogMBean {
    String NAME = "IFileDowonloadLogMBean";

    @ManagedOperation(description = "Параметры записи в файл")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "fileName", description = "Название файла"),
            @ManagedOperationParameter(name = "message", description = "Текст")
    })

    String dowonloadLog(String fileName, String message);

    @ManagedOperation(description = "Параметры чтения файла")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "fileName", description = "Название файла")
    })
    String readFileScanner(String fileName);

    @ManagedOperation(description = "Список файлов в папке \"logs\" c расширением \".txt\"")
    String logsFiles();

    /*
    @ManagedOperation(description = "Cкачать файл")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "fileName", description = "Название файла")
    })
    String downloadClientFile(String fileName);
     */

}
