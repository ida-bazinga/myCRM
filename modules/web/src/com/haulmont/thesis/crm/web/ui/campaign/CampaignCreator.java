package com.haulmont.thesis.crm.web.ui.campaign;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.app.NumerationService;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.core.entity.NumeratorType;
import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.entity.CampaignKind;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class CampaignCreator<T extends BaseCampaign> extends AbstractWindow implements CampaignCreatorWindow {

    protected T campaign;
    protected LookupField campaignKindField;
    protected CollectionDatasource<CampaignKind, UUID> campaignKindDs;

    @Inject
    protected Configuration configuration;
    @Inject
    protected UserSettingService userSettingService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Metadata metadata;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected OrgStructureService orgStructureService;
    @Inject
    protected NumerationService numerationService;
    @Inject
    protected DataSupplier dataSupplier;

    @WindowParam(name ="metaClassName")
    protected String campaignMetaClassName;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        findStandardComponents();

        initListeners();

        Map<String, Object> dsParams = Collections.<String, Object>singletonMap("entityType", campaignMetaClassName);
        campaignKindDs.refresh(dsParams);

        setCreatorCaption();
        addWindowActions();

        if (isEnableSaveSettings()) loadFrameSettings();

        getDialogParams().setWidth(500).setHeight(175);
    }

    protected void findStandardComponents() {
        campaignKindDs = getDsContext().getNN("campaignKindDs");
        campaignKindField = getComponentNN("campaignKind");
    }

    protected void addWindowActions() {
        Action okAction = new AbstractAction(Editor.WINDOW_COMMIT, configuration.getConfig(ClientConfig.class).getCommitShortcut()) {
            @Override
            public void actionPerform(Component component) {
                boolean campaignCreated = createCampaign();
                if (campaignCreated) {
                    if (isEnableSaveSettings())
                        saveFrameSettings();

                    close(Editor.WINDOW_COMMIT);
                }
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Ok");
            }
        };

        okAction.setEnabled(false);
        addAction(okAction);

        addAction(new AbstractAction(Editor.WINDOW_CLOSE) {
            @Override
            public void actionPerform(Component component) {
                close(Editor.WINDOW_CLOSE);
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Cancel");
            }
        });

        addAction(new AbstractAction("shortCloseAction", configuration.getConfig(ClientConfig.class).getCloseShortcut()) {
            @Override
            public void actionPerform(Component component) {
                close(Editor.WINDOW_CLOSE);
            }

        });
    }

    protected void initListeners(){
        campaignKindField.addListener(new ValueListener() {
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                Action okAction = getAction(Editor.WINDOW_COMMIT);
                if (okAction != null)
                    okAction.setEnabled(value != null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends BaseCampaign> V getCampaign() {
        return (V) campaign;
    }

    protected void loadFrameSettings() {
        if (StringUtils.isNotBlank(campaignMetaClassName)) {
            String campaignKindIdString = userSettingService.loadSetting(getNameSettings() + ".initCampaignKind");
            if (StringUtils.isNotBlank(campaignKindIdString)) {
                CampaignKind initCampaignKind = campaignKindById(campaignKindIdString);
                if (initCampaignKind != null) {
                    campaignKindField.setValue(initCampaignKind);
                }
            }
        }
    }

    public void saveFrameSettings() {
        CampaignKind campaignKind = campaignKindField.getValue();
        if (StringUtils.isNotBlank(campaignMetaClassName)) {
            userSettingService.saveSetting(
                    getNameSettings() + ".initCampaignKind",
                    campaignKind != null ? campaignKind.getId().toString() : ""
            );
        }
    }

    protected String getNameSettings() {
        return getFrame().getId() + "." + campaignMetaClassName;
    }

    // TODO: 25.03.2018 remove ???
    protected boolean isEnableSaveSettings() {
        return true;
    }

    protected CampaignKind campaignKindById(String campaignKindIdString) {
        if (StringUtils.isNotBlank(campaignKindIdString)) {
            LoadContext ctx = new LoadContext(CampaignKind.class)
                    .setId(UuidProvider.fromString(campaignKindIdString))
                    .setView("browse");
            return dataSupplier.load(ctx);
        }
        return null;
    }

    protected boolean createCampaign() {
        CampaignKind campaignKind = campaignKindField.getValue();
        if (campaignKind != null) {
            MetaClass metaClass = metadata.getClass(campaignKind.getEntityType());
            campaign = metadata.create(metaClass);
            campaign.setCreator(userSession.getUser());
            campaign.setSubstitutedCreator(userSession.getCurrentOrSubstitutedUser());
            campaign.setKind(dataSupplier.reload(campaignKind, "edit"));
            setCampaignDateTime();

            ExtEmployee employee = (ExtEmployee) orgStructureService.getEmployeeByUser(userSession.getCurrentOrSubstitutedUser());
            if (employee != null) {
                if (employee.getDepartment() != null) {
                    campaign.setOrganization(employee.getOrganization());
                    if (needToFillDepartment())
                        campaign.setDepartment(employee.getDepartment());
                }
                if (isKindContainsAndVisibleField(campaign.getKind(), "owner"))
                    campaign.setOwner(employee);
            }

            if (NumeratorType.ON_CREATE.equals(campaign.getKind().getNumeratorType())) {
                String num = numerationService.getNextNumber(campaign.getKind().getNumerator().getLocName().toString());
                if (num != null)
                    campaign.setNumber(num);
            }

        }
        return true;
    }

    protected boolean needToFillDepartment() {
        return isKindContainsAndVisibleField(campaign.getKind(), "department");
    }

    protected void setCreatorCaption() {
        String caption = "Caption.create";
        String captionDocument = "Caption.campaign";
        String packageName = metadata.getClassNN(campaignMetaClassName).getJavaClass().getPackage().getName();
        String propertyName = String.format("%s.detailedDescription.ACCUSATIVE", campaignMetaClassName.replaceFirst(".+\\$", ""));
        String localizedCaption = StringUtils.defaultIfEmpty(messages.getMessage(packageName, propertyName), getMessage(captionDocument));

        setCaption(getMessage(caption) + " " + localizedCaption + "...");
    }

    protected void setCampaignDateTime() {
        campaign.setDate(timeSource.currentTimestamp());
    }

    protected boolean isKindContainsAndVisibleField(CampaignKind campaignKind, String fieldName) {
        for (FieldInfo fieldInfo : campaignKind.getFields()) {
            if (fieldName.equals(fieldInfo.getName())) {
                return fieldInfo.getVisible();
            }
        }
        return false;
    }
}