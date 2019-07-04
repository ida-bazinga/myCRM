package com.haulmont.thesis.crm.web.ui.baseactivity;

import com.google.common.collect.Lists;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UuidProvider;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityCreator<T extends BaseActivity> extends AbstractWindow implements ActivityHolderWindow {
    protected T activity;
    protected LookupField activityKindField;
    protected CollectionDatasource<ActivityKind, UUID> activityKindDs;

    @Inject
    protected Configuration configuration;
    @Inject
    protected UserSettingService userSettingService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected OrgStructureService orgStructureService;

    @WindowParam(name ="metaClassName")
    protected String activityMetaClassName;

    @WindowParam(name ="contactPerson")
    protected ExtContactPerson contactPerson;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        findStandardComponents();

        initListeners();

        Map<String, Object> dsParams = new HashMap<>();
        dsParams.put("entityType", activityMetaClassName);
        dsParams.put("excludeKindCodes", Lists.newArrayList("campaign-scheduled-target" , "campaign-outgoing-call"));
        activityKindDs.refresh(dsParams);

        setCreatorCaption();
        addWindowActions();

        if (isEnableSaveSettings()) loadFrameSettings();

        getDialogParams().setWidth(400).setHeight(200);
    }

    protected void findStandardComponents() {
        activityKindDs = getDsContext().getNN("activityKindDs");
        activityKindField = getComponentNN("activityKind");
    }

    protected void addWindowActions() {
        Action okAction = new AbstractAction(Editor.WINDOW_COMMIT, configuration.getConfig(ClientConfig.class).getCommitShortcut()) {
            @Override
            public void actionPerform(Component component) {
                boolean activityCreated = createActivity();
                if (activityCreated) {
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
        activityKindField.addListener(new ValueListener() {
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                Action okAction = getAction(Editor.WINDOW_COMMIT);
                if (okAction != null)
                    okAction.setEnabled(value != null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends BaseActivity> V getActivity() {
        return (V) activity;
    }

    protected void loadFrameSettings() {
        if (StringUtils.isNotBlank(activityMetaClassName)) {
            String activityKindIdString = userSettingService.loadSetting(getNameSettings() + ".initActivityKind");
            if (StringUtils.isNotBlank(activityKindIdString)) {
                ActivityKind initActivityKind = activityKindById(activityKindIdString);
                if (initActivityKind != null) {
                    activityKindField.setValue(initActivityKind);
                }
            }
        }
    }

    public void saveFrameSettings() {
        ActivityKind activityKind = activityKindField.getValue();
        if (StringUtils.isNotBlank(activityMetaClassName)) {
            userSettingService.saveSetting(
                    getNameSettings() + ".initActivityKind",
                    activityKind != null ? activityKind.getId().toString() : ""
            );
        }
    }

    protected String getNameSettings() {
        return getFrame().getId() + "." + activityMetaClassName;
    }

    // TODO: 25.03.2018 remove ???
    protected boolean isEnableSaveSettings() {
        return true;
    }

    protected ActivityKind activityKindById(String activityKindIdString) {
        if (StringUtils.isNotBlank(activityKindIdString)) {
            LoadContext ctx = new LoadContext(ActivityKind.class)
                    .setId(UuidProvider.fromString(activityKindIdString))
                    .setView("browse");
            return dataSupplier.load(ctx);
        }
        return null;
    }

    protected boolean createActivity() {
        ActivityKind activityKind = activityKindField.getValue();
        if (activityKind != null) {
            ExtEmployee employee = (ExtEmployee) orgStructureService.getEmployeeByUser(userSession.getCurrentOrSubstitutedUser());
            activity = activityService.createActivity(activityKind, activityKind.getEntityType(), employee);
            if (contactPerson != null){
                activity.setContactPerson(contactPerson);
                if (contactPerson.getCompany() != null){
                    activity.setCompany((ExtCompany) contactPerson.getCompany());
                }
            }
            return true;
        }
        return false;
    }

    protected void setCreatorCaption() {
        String caption = "Caption.create";
        String captionCard = "Caption.activity";
        String packageName = metadata.getClassNN(activityMetaClassName).getJavaClass().getPackage().getName();
        String propertyName = String.format("%s.detailedDescription.ACCUSATIVE", activityMetaClassName.replaceFirst(".+\\$", ""));
        String localizedCaption = StringUtils.defaultIfEmpty(messages.getMessage(packageName, propertyName), getMessage(captionCard));

        setCaption(getMessage(caption) + " " + localizedCaption + "...");
    }
}