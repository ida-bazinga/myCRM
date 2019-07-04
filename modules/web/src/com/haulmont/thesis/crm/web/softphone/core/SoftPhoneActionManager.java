/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core;

import com.haulmont.thesis.crm.web.softphone.core.entity.ActionMessage;
import com.haulmont.thesis.crm.web.softphone.core.listener.SoftphoneActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ManagedBean(SoftPhoneActionManager.NAME)
public class SoftPhoneActionManager implements Serializable {
    final static String NAME = "crm_SoftPhoneActionManager";

    private static final long serialVersionUID = -7136924833734993649L;

    private Log log = LogFactory.getLog(getClass());

    protected SoftphoneActionListener actionListener;

    protected ExecutorService executorService = Executors.newSingleThreadExecutor();

    public synchronized void setListener(SoftphoneActionListener listener) {
        actionListener = listener;
    }

    public synchronized void removeListener() {
        actionListener= null;
    }


    public synchronized void invoke(@Nonnull final ActionMessage message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                actionListener.invokeAction(message);
            }
        });
    }
}
