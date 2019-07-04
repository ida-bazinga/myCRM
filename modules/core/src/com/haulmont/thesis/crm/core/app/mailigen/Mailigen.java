/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.mailigen;

        import javax.annotation.ManagedBean;

@ManagedBean(IMailigenMBean.NAME)
public class Mailigen implements IMailigenMBean {


    public boolean getIsStatus() {
        return true;
    }
}
