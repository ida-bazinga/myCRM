/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.gui.components;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;
import com.haulmont.thesis.crm.gui.components.SPIPanel;
import com.haulmont.thesis.crm.web.softphone.actions.SetActiveProfileAction;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import com.vaadin.ui.HorizontalLayout;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

public class WebSPIPanel extends WebAbstractComponent<HorizontalLayout> implements SPIPanel {

    private static final String EMBEDDED_SIZE = "20px";
    private static final String NO_SESSION_ICON = "VAADIN/themes/halo/components/spipanel/icons/phone_off.png";
    private static final String ACTIVE_CALL_WINDOW_NAME = "activeCallWindow";

    private int counter;
    private String lineNumber;

    protected Datasource<SoftPhoneSession> panelDatasource;

    public WebSPIPanel(){
        component = new HorizontalLayout();
    }

    @Override
    @Nullable
    public SoftPhoneSession getSoftphoneSession() {
        return this.panelDatasource != null ? this.panelDatasource.getItem() : null;
    }

    @Override
    @Nullable
    public UUID getSoftphoneSessionId(){
        return getSoftphoneSession() != null ? getSoftphoneSession().getId() : null;
    }

    @Override
    @Nullable
    public String getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    @Nullable
    public CallStatus getActiveCall() {
        return panelDatasource.getItem() != null ? panelDatasource.getItem().getActiveCall() : null;
    }

    @Override
    public void setActiveCall(CallStatus activeCall) {
        if (panelDatasource.getItem() != null){
            panelDatasource.getItem().setActiveCall(activeCall);
        }
    }

    @Override
    public Datasource<SoftPhoneSession> getDatasource() {
        return this.panelDatasource;
    }

    @Override
    public void setDatasource(Datasource<SoftPhoneSession> datasource) {
        this.panelDatasource = datasource;
        this.panelDatasource.addListener(new DsListenerAdapter<SoftPhoneSession>() {
            @Override
            public void itemChanged(Datasource<SoftPhoneSession> ds, @Nullable SoftPhoneSession prevItem, @Nullable SoftPhoneSession item) {
                refreshComponent(item);
            }

            @Override
            public void valueChanged(SoftPhoneSession source, String property, @Nullable Object prevValue, @Nullable Object value) {
                if ("activeProfile".equals(property)) {
                    SoftPhoneProfileName newValue = (SoftPhoneProfileName)value;
                    if (newValue == null)
                        return;

                    SetActiveProfileAction profileAction = new SetActiveProfileAction(getSoftphoneSessionId());
                    profileAction.setNewProfileName(newValue.getId());
                    profileAction.actionPerform(null);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(getSoftphoneSession() != null);
    }

    @Override
    public void repaintComponent() {
        refreshComponent(getSoftphoneSession());
    }

    protected void refreshComponent(SoftPhoneSession session) {
        component.removeAllComponents();
        counter = -1;
        BoxLayout composition = buildComposition(session);
        component.addComponent(WebComponentsHelper.unwrap(composition),0);
        component.setExpandRatio(WebComponentsHelper.unwrap(composition),0);
    }

    protected BoxLayout buildComposition(SoftPhoneSession session){
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.NAME);

        BoxLayout result = componentsFactory.createComponent(BoxLayout.HBOX);
        result.setSpacing(true);
        result.setWidth("100%");

        Label lineCaption = componentsFactory.createComponent(Label.NAME);
        lineCaption.setAlignment(Alignment.MIDDLE_LEFT);
        lineCaption.setStyleName("cuba-user-select-label");
        result.add(lineCaption, getCounter());

        if (session != null){
            lineCaption.setDatasource(this.panelDatasource, "lineNumber");

            LookupField profileField = componentsFactory.createComponent(LookupField.class);
            profileField.setDatasource(this.panelDatasource, "activeProfile");
            profileField.setNewOptionAllowed(false);
            profileField.setRequired(true);
            profileField.setOptionsList(SoftPhoneProfileName.getOptions());
            profileField.setValue(session.getActiveProfile());
            profileField.setStyleName("cuba-user-select-combobox");
            profileField.setWidth("150px");
            result.add(profileField, getCounter());

            Label targetsNameLabel = componentsFactory.createComponent(Label.NAME);
            targetsNameLabel.setValue("Q:" );
            targetsNameLabel.setAlignment(Alignment.MIDDLE_LEFT);
            targetsNameLabel.setStyleName("cuba-user-select-label");
            result.add(targetsNameLabel, getCounter());

            Label targetsLabel = componentsFactory.createComponent(Label.NAME);
            targetsLabel.setDatasource(this.panelDatasource, "queueSize");
            targetsLabel.setAlignment(Alignment.MIDDLE_LEFT);
            targetsLabel.setStyleName("cuba-user-select-label");
            result.add(targetsLabel, getCounter());

            CheckBox campaignEnabledField = componentsFactory.createComponent(CheckBox.NAME);
            campaignEnabledField.setDatasource(this.panelDatasource, "campaignEnabled");
            campaignEnabledField.setValue(session.getCampaignEnabled());
            campaignEnabledField.setAlignment(Alignment.MIDDLE_CENTER);
            campaignEnabledField.setRequired(true);
            campaignEnabledField.setDescription(AppBeans.get(Messages.class).getMainMessage("cti.campaignEnabledDescription"));
            result.add(campaignEnabledField, getCounter());

        }else{
            lineCaption.setValue(StringUtils.isNotBlank(getLineNumber()) ? getLineNumber() : "----");

            // TODO: 08.04.2018 May be convert to Button for invoke #WebSPIPanel.refreshComponent().
            Embedded embedded = componentsFactory.createComponent(Embedded.NAME);
            embedded.setType(Embedded.Type.IMAGE);
            embedded.setWidth(EMBEDDED_SIZE);
            embedded.setHeight(EMBEDDED_SIZE);
            embedded.setSource(getDefaultEmbeddedSource());
            result.add(embedded,getCounter());
        }
        return result;
    }

    private int getCounter(){
        counter++;
        return counter;
    }

    @Override
    public void setProfile(SoftPhoneProfileName value){
        SoftPhoneSession session = getSoftphoneSession();
        if (session != null){
            session.setActiveProfile(value);
        }
    }

    @Override
    public SoftPhoneProfileName getProfile(){
        return getSoftphoneSession() != null ? getSoftphoneSession().getActiveProfile() : null;
    };

    protected String getDefaultEmbeddedSource() {
        return ControllerUtils.getLocationWithoutParams() + SoftphoneConstants.NO_SESSION_ICON;
    }

}
