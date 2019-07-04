package com.haulmont.thesis.crm.web.softphone.core;

import com.haulmont.thesis.crm.web.softphone.core.entity.IMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ManagedBean(SoftPhoneMessageBroadcaster.NAME)
public class SoftPhoneMessageBroadcaster implements Serializable {

    private static final long serialVersionUID = -5513654108753028318L;

    public static final String NAME = "crm_SoftPhoneMessageBroadcaster";
    private Log log = LogFactory.getLog(getClass());

    protected List<SoftPhoneBroadcastListener> broadcastListeners = new LinkedList<>();

    protected ExecutorService executorService = Executors.newSingleThreadExecutor();

    public synchronized void register(SoftPhoneBroadcastListener listener) {
        broadcastListeners.add(listener);
    }

    public synchronized void unregister(SoftPhoneBroadcastListener listener) {
        broadcastListeners.remove(listener);
    }

    public synchronized void broadcast(final IMessage message) {
        for(final SoftPhoneBroadcastListener listener : broadcastListeners)
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.receiveBroadcast(message);
                }
            });
    }

    public interface SoftPhoneBroadcastListener {
        void receiveBroadcast(IMessage message);
    }
}
