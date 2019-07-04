package com.haulmont.thesis.crm.web.messageUnisender;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.thesis.crm.core.app.unisender.UnisenderTools;
import com.haulmont.thesis.crm.entity.MessageUnisender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;

public class MessageUnisenderBrowser extends AbstractLookup {


    @Named("mainDs")
    protected CollectionDatasource<MessageUnisender, UUID> mainDs;

    @Inject
    private DataManager dataManager;

    Log log = LogFactory.getLog(MessageUnisender.class);

    public void refreshUni() {
        UnisenderTools uni = new UnisenderTools();
        ArrayList<MessageUnisender> lists = uni.getMessages();
        String txtMesage;
        if (lists != null) {
            List<String> existingIds = new ArrayList<>();
            for (MessageUnisender item : getAllMessagesUnisender()) {
                existingIds.add(item.getCode());
            }
            for (int i = lists.size() - 1; i >= 0; i--) {
                if (existingIds.remove(lists.get(i).getCode())) {
                    lists.remove(i);
                }
            }
            try {
                dataManager.commit(new CommitContext(lists));
            } catch (SecurityException e) {
                log.error(e.getLocalizedMessage());
            }
            mainDs.refresh();
            txtMesage = "Загружено писем: " + lists.size();
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        } else {
            txtMesage = "Не удалось получить список писем";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        }
        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        return;
    }

    private List<MessageUnisender> getAllMessagesUnisender() {
        LoadContext loadContext = new LoadContext(MessageUnisender.class).setView("_local");
        loadContext.setQueryString("select e from crm$MessageUnisender e");
        return dataManager.loadList(loadContext);
    }
}