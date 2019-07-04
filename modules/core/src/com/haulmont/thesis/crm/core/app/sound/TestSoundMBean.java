/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.sound;

import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;

public interface TestSoundMBean  {
    String NAME = "TestSoundMBean";


    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "path", description = "путь до файла")})
    void play(String path);

}
