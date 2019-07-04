package com.haulmont.thesis.crm.web.ui.callcampaign;

import com.google.common.collect.Lists;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CallCampaign;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import com.haulmont.thesis.crm.enums.CampaignState;
import com.haulmont.thesis.crm.web.ui.basic.edit.AbstractCampaignCardEditor;
import com.haulmont.thesis.crm.web.ui.callcampaigntarget.CallCampaignTargetsFrame;
import com.haulmont.thesis.web.gui.components.ThesisWebSplitPanel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class CallCampaignEditor<T extends CallCampaign> extends AbstractCampaignCardEditor<T> {

    protected CrmConfig config;

    @Named("campaignFields")
    protected GridLayout mainFields;

    @Named("targetsDs")
    protected HierarchicalDatasource<CallCampaignTrgt, UUID> targetsDs;
    @Named("operatorsDs")
    protected CollectionDatasource<Employee, UUID> operatorsDs;

    CallCampaignTargetsFrame targetsFrame;

    @Inject
    protected ClientConfiguration configuration;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        config = configuration.getConfigCached(CrmConfig.class);

        if (operatorsDs != null) {
            addCounterOnTab(operatorsDs, "operatorsTab");
            operatorsDs.refresh();
        }
    }
    protected void findStandardComponents() {
        super.findStandardComponents();
    }

    @Override
    protected void initDsListeners(){
        super.initDsListeners();
        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                if (Arrays.asList("project", "date").contains(property)) {
                    getItem().setName(campaignService.createName(getItem()));
                }
                if ("owner".equals(property)) {
                    clearDepartment(value);
                }
                if ("startTimePlan".equals(property)) {
                    if (value != null && prevValue == null){
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime((Date) value);
                        if (calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.MINUTE) == 0){
                            calendar.set(Calendar.HOUR, 9);
                            calendar.set(Calendar.MINUTE, 0);
                            getItem().setStartTimePlan(calendar.getTime());
                        }
                    }
                }
                if ("endTimePlan".equals(property)) {
                    if (value != null && prevValue == null){
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime((Date) value);
                        if (calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.MINUTE) == 0){
                            calendar.set(Calendar.HOUR, 18);
                            calendar.set(Calendar.MINUTE, 0);
                            getItem().setEndTimePlan(calendar.getTime());
                        }
                    }
                }
            }
        });

        //targets tab is Lazy
        targetsDs.addListener(new CollectionDsListenerAdapter<CallCampaignTrgt>(){
            @Override
            public void collectionChanged(CollectionDatasource  ds, Operation operation, List<CallCampaignTrgt> items){
                super.collectionChanged(ds, operation, items);
                String newTabsCaption;
                if (ds.size() > 0) {
                    newTabsCaption = getMessage("targetsTab") + " (" + ((HierarchicalDatasource)ds).getRootItemIds().size() + ")";
                } else {
                    newTabsCaption = getMessage("targetsTab");
                }
                for (TabSheet.Tab tab : tabsheet.getTabs())
                    if ("targetsTab".equals(tab.getName()))
                        if (getTabIfExist(tabsheet, "targetsTab") != null) {
                            tab.setCaption(newTabsCaption);
                            break;
                        }
            }
        });
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setState(CampaignState.New.getId());
    }

    @Override
    protected void postInit(){
        super.postInit();

        if (accessData.getSecurityFrameEditable()) {
            TextField numField = mainFields.getComponentNN("number");
            numField.setEditable(true);
        }
    }

    @Override
    protected void initLazyTabs() {
        super.initLazyTabs();
        tabsheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                String tabName = newTab.getName();
                if ("targetsTab".equals(tabName)) {
                    targetsFrame = getComponent("targetsFrame");
                    if (targetsFrame != null){
                        targetsFrame.init(Collections.emptyMap());
                        targetsFrame.hideTableColumn("campaign");
                    }
                }
            }
        });
    }

    @Override
    protected void createCreateSubCardActions() {
        if (isDefaultCampaign()){
            createSubCardButton.setVisible(false);
        }else {
            super.createCreateSubCardActions();
        }
    }

    protected boolean isDefaultCampaign(){
        List<String> defaultCampaignNames = Lists.newArrayList(config.getDefaultIncomingCampaign(), config.getDefaultOutboundCampaign());
        return defaultCampaignNames.contains(getItem().getNumber());
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