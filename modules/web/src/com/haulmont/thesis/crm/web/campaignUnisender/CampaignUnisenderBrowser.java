package com.haulmont.thesis.crm.web.campaignUnisender;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.thesis.crm.core.app.unisender.UnisenderTools;
import com.haulmont.thesis.crm.entity.CampaignUnisender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CampaignUnisenderBrowser extends AbstractLookup {

    @Named("mainDs")
    protected CollectionDatasource<CampaignUnisender, UUID> mainDs;

    @Inject
    private DataManager dataManager;

    Log log = LogFactory.getLog(CampaignUnisender.class);

    public void refreshUni() {
        UnisenderTools uni = new UnisenderTools();
        ArrayList<CampaignUnisender> lists = uni.getCampaigns();
        List<String> existingIds = new ArrayList<>();
        for (CampaignUnisender item : mainDs.getItems()) {
            existingIds.add(item.getCode());
        }
        for (int i = lists.size()-1; i >= 0; i--) {
            if (existingIds.remove(lists.get(i).getCode()))
            {
                lists.remove(i);
            }
        }
        try {
            dataManager.commit(new CommitContext(lists));
        } catch (SecurityException e) {
            log.error(e.getLocalizedMessage());
        }
        mainDs.refresh();
        String txtMesage = "Загружено кампаний: " + lists.size();
        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        return;
    }

}