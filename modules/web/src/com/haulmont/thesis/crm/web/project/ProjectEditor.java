package com.haulmont.thesis.crm.web.project;

import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.core.entity.ProjectGroup;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.entity.ProjectRoom;
import com.haulmont.thesis.crm.web.guideline.GuideLineFrame;
import com.haulmont.thesis.crm.web.ui.salesdochistory.ProjectSalesDocHistoryFrame;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ProjectEditor<T extends ExtProject> extends AbstractEditor<T> {

    private List<String> initializedTabs = new ArrayList<>();


    @Named("tabsheet")
    protected TabSheet tabsheet;
    @Named("integrationStateLabel")
    protected Label integrationStateLabel;
    @Inject
    private DataManager dataManager;
    @Inject
    protected LookupPickerField parentProject;
    @Inject
    private UniqueNumbersService un;
    @Inject
    private Work1C work1C;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Datasource<T> projectDs;
    @Inject
    protected CollectionDatasource<ExtProject, UUID> projectRoomDs;
    @Inject
    protected DateField dateStartFact, datefield_3;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected OrgStructureService orgStructureService;
    @Inject
    protected UserSession userSession;

    private int priority;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initParentProject(params);
        initDialogParams();
        initLazyTabs();
    }

    @Override
    protected void postInit() {
        initWork1CExtSystem();
        integrationStateLabel.setValue(work1C.getStausIntegrationResolver(getItem().getId(), getItem().getMetaClass().getName()));
        priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1;
    }

    private void initWork1CExtSystem() {
        work1C.setExtSystem(work1C.getExtSystem(getItem().getOrganization()));
    }

    private void initParentProject(final Map<String, Object> params) {
        PickerField.LookupAction projectLookupAction = (PickerField.LookupAction)parentProject.getActionNN(PickerField.LookupAction.NAME);
        projectLookupAction.setLookupScreen("tm$ProjectGroup.lookup");
        projectLookupAction.setLookupScreenParams(params);
        //parentProject.addAction(projectLookupAction);
        parentProject.addOpenAction();
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        List<ProjectGroup> pg = loadProjectGroup();
        item.setProjectGroup(pg.get(0));
        item.setIsGroup(false);
        if (item.getOrganization() == null) {
            Organization org = configuration.getConfigCached(DefaultEntityConfig.class).getOrganizationDefault();
            item.setOrganization(org != null ?
                    org : orgStructureService.getOrganizationByUser(userSession.getCurrentOrSubstitutedUser()));
        }
    }

    private List<ProjectGroup> loadProjectGroup() {
        LoadContext loadContext = new LoadContext(ProjectGroup.class)
                .setView("_local");
        loadContext.setQueryString("select e from tm$ProjectGroup e where e.name = :groupName")
                .setParameter("groupName", "Основной проект");
        return dataManager.loadList(loadContext);
    }

    protected void initDialogParams() {
        getDialogParams().setWidth(850);
        getDialogParams().setHeight(650);
    }

    public void export1cBtn() {
        dateStartFact.setRequired(true);
        datefield_3.setRequired(true);
        //to log1c
        boolean isCommit = projectCommit();
        if (isCommit) {
            initWork1CExtSystem();
            goTo1C();
            showNotification(getMessage("Проект отправлен в 1С"), NotificationType.HUMANIZED);
        }
    }

    protected boolean projectCommit() {
        if(PersistenceHelper.isNew(getItem()) || projectDs.isModified() ) {
           return super.commit();
        }
        return true;
    }

    protected void goTo1C() {
        Map<String, Object> params = new HashMap<>();
        params.put(getItem().getMetaClass().getName(), getItem().getId());
        work1C.regLog(params, priority);
    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem())){
            Long l = un.getNextNumber("Project");
            getItem().setCode(String.format("%05d",l));
        }
        return true;
    }

    public void createProjectRoom() {
        ProjectRoom newEntity = null;
            if(PersistenceHelper.isNew(getItem())) {
                showNotification(getMessage("Сохраните Проект перед выбором помещений."), NotificationType.HUMANIZED);
                return;
            }
            newEntity = metadata.create(ProjectRoom.class);
            newEntity.setProject(getItem());
        Map<String, Object> params = new HashMap<>();
        Window window = openEditor("crm$ProjectRoom.edit", newEntity, WindowManager.OpenType.DIALOG, params);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                projectRoomDs.refresh();
            }
        });
    }

    private void initLazyTabs() {
        tabsheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                String tabName = newTab.getName();
                if ("salesDocHistoryTab".equals(tabName) && !initializedTabs.contains(tabName)) {
                    ProjectSalesDocHistoryFrame docFrame = getComponentNN("salesDocHistoryFrame");
                    docFrame.init();
                    docFrame.refreshDs();
                    initializedTabs.add(tabName);
                }

                if("guideLineTab".equals(tabName) && !initializedTabs.contains(tabName)) {
                    //GuideLineFrame guideLineFrame = getComponentNN("guidLineFrame");
                    //guideLineFrame.init(Collections.<String, Object>singletonMap("project", getItem()));
                    initializedTabs.add(tabName);
                }
            }
        });
    }

}