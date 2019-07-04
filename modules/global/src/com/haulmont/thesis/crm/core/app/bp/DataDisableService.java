/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.app.ConfigStorageService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.crm.entity.ExtSystem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import java.text.SimpleDateFormat;

@ManagedBean(IDataDisableService.NAME)
public class DataDisableService implements IDataDisableService {

    protected Log logFactory = LogFactory.getLog(this.getClass());
    private Work1C work1C;
    private ConfigStorageService configStorageService;

    public boolean isUpdateDataDisable() {
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(ExtSystem.BP1CEFI);
        configStorageService = AppBeans.get(ConfigStorageService.NAME);

        return updateDateDisable();
    }

    private boolean updateDateDisable() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dateDisable = dateFormat.format(work1C.getDataDisable());
            String nameConfig = "crm.bp.dateQuartal";
            configStorageService.setDbProperty(nameConfig, dateDisable);
        } catch (Exception e) {
            logFactory.error(String.format("Error class DataDisableService:updateDateDisable. Description of the error: %s", e.toString()));
            return false;
        }
        return true;
    }
}
