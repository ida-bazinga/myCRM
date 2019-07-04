package com.haulmont.thesis.crm.web.payOrd;

import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.web.ui.basic.browse.AbstractCardBrowser;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PayOrdBrowse<T extends PayOrd> extends AbstractCardBrowser<T> {

    @Inject
    private Work1C work1C;
    @Inject
    protected PopupButton print;
    @Inject
    protected ClientConfiguration configuration;
    @Inject
    private DataManager dataManager;

    protected String entityName;
    protected int priority;
    protected Organization org;
    protected Set<PayOrd> payOrdSet;
    protected Set<Entity> toCommit = new HashSet<>();
    protected Currency currencyRub;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1; //приоритет отправки в 1С
        work1C.setExtSystem(ExtSystem.BP1CEFI);
        org = configuration.getConfigCached(DefaultEntityConfig.class).getOrganizationDefault();
        currencyRub = configuration.getConfigCached(CrmConfig.class).getCurrencyRub();
    }


    //пометить к обмену
    public void exportButton() {

        if (!cardsTableSelected()) {
            return;
        }

        int i = 0;
        for (PayOrd item:payOrdSet) {
            if (item.getOrganization().equals(org) && item.getCurrency().equals(currencyRub) &&
            TypeTransactionDebiting.PAYSUPPLINER.equals(item.getTypeTransaction())) {
                Map<String, Object> params = new HashMap<>();
                params.put(item.getCompany().getMetaClass().getName(), item.getCompany().getId());
                params.put(item.getMetaClass().getName(), item.getId());
                Boolean result = work1C.regLog(params, priority);
                if (result) {
                    i++;
                }
            }
        }

        String txtMesage = String.format("Выбрано платежных поручений: %s, отправлено в 1С: %s", payOrdSet.size(), i);
        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
    }

    protected boolean cardsTableSelected() {
        payOrdSet = cardsTable.getSelected();
        if (payOrdSet.size() < 1) {
            showNotification(getMessage("Не выбрана строка!"), NotificationType.HUMANIZED);
            return false;
        }
        return true;
    }

    @Override
    protected void createPrintAction() {
        if (print != null) {
            print.setDescription(getMessage("print"));
            print.setVisible(CollectionUtils.isNotEmpty(print.getActions()));
        }
    }

    public void paidButton() {

        if (!cardsTableSelected()) {
            return;
        }

        for (PayOrd item: payOrdSet) {
           updateIRSDS(item.getIntegrationResolver());
           updateIRSDS(item.getInvDoc().getIntegrationResolver());
        }
        doCommit();
    }

    protected void updateIRSDS(IntegrationResolver ir) {
        ir.setStateDocSales(StatusDocSales.PAID);
        toCommit.add(ir);
    }

    private void doCommit() {
        if (toCommit.size() > 0) {
            dataManager.commit(new CommitContext(toCommit));
            cardsDs.refresh();
        }
    }

}