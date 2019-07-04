package com.haulmont.thesis.crm.web.company;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.*;

import java.util.*;

import com.haulmont.cuba.gui.data.*;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.CallCampaignTarget;
import com.haulmont.thesis.crm.entity.ExtCompany;
import com.haulmont.thesis.crm.entity.OutboundCampaign;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelectCampaignFrame extends AbstractEditor {

    //protected GroupDatasource<ExtCompany, UUID> companiesDs;

    protected OutboundCampaign campaign;

    @Inject
    protected LookupField outboundCampaignLookup;

    @Inject
    private DataManager dataManager, dmCct;

    @Inject
    protected Button windowCommit;

    @Inject
    protected Label sizeLabel, successPhoneCountLabel, failedPhoneCountLabel, notPhoneCountLabel;

    @Inject
    protected Metadata metadata;

    @Override
    public void init(final Map<String, Object> params) {
        super.init(params);
        windowCommit.setEnabled(false);
        //companiesDs =  (GroupDatasource<ExtCompany, UUID>)params.get("collectionDataSource");
        final Set<ExtCompany> companies = (Set<ExtCompany>) params.get("selectedItems");
        //sizeLabel.setValue(String.format("Выбрано: %s компаний", companiesDs.size()));
        sizeLabel.setValue(String.format("Выбрано: %s компаний", companies.size()));
        addAction(new AbstractAction("windowCommit") {
            @Override
            public void actionPerform(Component component) {
                Integer successPhoneCount=0, failedPhoneCount=0, notPhoneCount=0;
                for (ExtCompany item : companies) {

                    List<CallCampaignTarget> target = getTarget(item, campaign);
                    if (target.size() == 0) {
                        View cpView = metadata.getViewRepository().getView(ExtContactPerson.class, "with-communication");
                        View view = metadata.getViewRepository().getView(ExtCompany.class, "_minimal")
                                .addProperty("contactPersons", cpView);
                        item = dataManager.reload(item, view);
                     //// TODO: 07.08.2016 Проверить что у контактов есть телефоны
                        if (item.getContactPersons() != null && item.getContactPersons().size() > 0){

                            CallCampaignTarget cct = metadata.create(CallCampaignTarget.class);
                            cct.setCompany(item);
                            cct.setOutboundCampaign(campaign);
                            cct.setNumberOfTries(0);
                            cct.setNumberOfFailedTries(0);
                            CommitContext commitContext = new CommitContext(cct);
                            dmCct.commit(commitContext);
                            successPhoneCount++;
                        }
                        else notPhoneCount++;
                    }
                    else failedPhoneCount++;
                }
                successPhoneCountLabel.setValue(String.format("Добавлено: %s компаний", successPhoneCount));
                failedPhoneCountLabel.setValue(String.format("Дублей: %s компаний",failedPhoneCount));
                notPhoneCountLabel.setValue(String.format("Отсутствуют телефоны у: %s компаний",notPhoneCount));
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Select");
            }
        });

        addAction(new AbstractAction("windowClose") {
            @Override
            public void actionPerform(Component component) {
                close("close");
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Close");
            }
        });

        outboundCampaignLookup.addListener(new ValueListener<Object>() {

            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                successPhoneCountLabel.setValue("");
                failedPhoneCountLabel.setValue("");
                if (value!=null){
                    windowCommit.setEnabled(true);
                    campaign = (OutboundCampaign)value;
                }
                else{
                    windowCommit.setEnabled(false);
                }
            }
        });
    }

    public List<CallCampaignTarget> getTarget(ExtCompany company, OutboundCampaign callCampaign)
    {
        LoadContext loadContext = new LoadContext(CallCampaignTarget.class).setView("_local");
        String params = String.format("select e from crm$CallCampaignTarget e where e.company.id = :company and e.outboundCampaign.id = :callCampaign");
        loadContext.setQueryString(params).setParameter("company", company).setParameter("callCampaign", callCampaign);
        return dataManager.loadList(loadContext);
    }

}