package com.haulmont.thesis.crm.web.emailCampaign;

import com.google.common.collect.Lists;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.EmailCampaign;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.crm.web.ui.basic.edit.AbstractCampaignCardEditor;
import com.haulmont.thesis.web.gui.components.ThesisWebSplitPanel;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmailCampaignEditor<T extends EmailCampaign> extends AbstractCampaignCardEditor<T> {

    protected CrmConfig config;

    @Inject
    private DataManager dataManager;
    @Inject
    protected ClientConfiguration configuration;



    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        config = configuration.getConfigCached(CrmConfig.class);
    }

    @Override
    protected void initDsListeners(){
        super.initDsListeners();
        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                if (Arrays.asList("project", "anyTxt").contains(property)) {
                    getItem().setName(campaignService.createName(getItem()));
                }
                if ("owner".equals(property)) {
                    clearDepartment(value);
                }
            }
        });
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        List<ExtEmployee> employee = loadEmployee();   //Сотрудники
        if (!employee.isEmpty())item.setOwner(employee.get(0));
    }

    @Override
    protected void postInit(){
        super.postInit();
        initComponent();
    }


    protected void initComponent() {
        if (getItem().getList_id() != null && !getItem().getList_id().isEmpty()) {
            getComponentNN("tagLabel").setVisible(false);
            getComponentNN("tag").setVisible(false);
        }
    }

    @Override
    protected void createCreateSubCardActions() {
        if (isDefaultCampaign()){
            createSubCardButton.setVisible(false);
        }else {
            super.createCreateSubCardActions();
        }
    }

    @Override
    protected void setMultiUploadEnabled() {
        multiUploadField.setVisible(false);
    }

    protected boolean isDefaultCampaign(){
        List<String> defaultCampaignNames = Lists.newArrayList(config.getDefaultIncomingCampaign(), config.getDefaultOutboundCampaign());
        return defaultCampaignNames.contains(getItem().getNumber());
    }

    private List<ExtEmployee> loadEmployee() { //Поиск организации по коду
        LoadContext loadContext = new LoadContext(Employee.class)
                .setView("_local");
        loadContext.setQueryString("select e from df$Employee e where e.user.id = :user")
                .setParameter("user", userSession.getCurrentOrSubstitutedUser());
        //.setParameter("user", AppBeans.get(UserSessionSource.class).getUserSession().getCurrentOrSubstitutedUser());
        return dataManager.loadList(loadContext);
    }

    @Override
    protected void initStartProcessButtonsFrame() {
        if (isDefaultCampaign()) {
            startProcessButtonsFrame.setVisible(false);
        }else {
            super.initStartProcessButtonsFrame();
        }
    }

    @Override
    protected void adjustForDefaultCampaign(){
        if (isDefaultCampaign()){
            removeTabIfExist(tabsheet, "processTab");
            getComponentNN("resolutionsPane").setVisible(false);
            ((ThesisWebSplitPanel) getComponentNN("split")).hide();
        }
    }
}