package com.haulmont.thesis.crm.web.project;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.app.DataService;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.thesis.crm.entity.ExtProject;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//// TODO: 24.07.2016  Упростить на примере LineOfBussines
public class ProjectBrowser <T extends ExtProject> extends AbstractLookup {
    protected HierarchicalDatasource<T, UUID> mainDs;
    protected TreeTable mainTable;

    @Inject
    protected DataService dataService;

    @Inject
    protected PopupButton createBtn;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Security security;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        findStandardComponents();

        addCreateActions();

        mainTable.addAction(EditAction());
        mainTable.addAction(CopyAction());

    }

    protected void findStandardComponents() {
        mainTable = getMainTable();
        mainDs = getMainDs();

        if (mainTable == null) {
            throw new IllegalStateException("Project mainTable not found");
        }
        if (mainDs == null) {
            throw new IllegalStateException("Project mainDs not found");
        }
    }

    public TreeTable getMainTable() {
        return getComponentNN("mainTable");
    }

    public HierarchicalDatasource<T, UUID> getMainDs() {
        return getDsContext().get("mainDs");
    }

    protected void addCreateActions() {

        createBtn.addAction(createCreateAction());
        createBtn.addAction(createCreateGroupAction());
        createBtn.addAction(CopyAction());
    }

    protected Action createCreateAction() {
        return new AbstractAction("actions.createNew") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null)
                    createBtn.setPopupVisible(false);

                final ExtProject newEntity = metadata.create(ExtProject.class);
                ExtProject currentEntity = mainDs.getItem();

                if (currentEntity !=null && currentEntity.getIsGroup())
                    newEntity.setParentProject(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("tm$Project.edit", newEntity, WindowManager.OpenType.THIS_TAB, params);

                window.addListener(new CloseListener() {

                    @Override
                    public void windowClosed(String actionId) {
                        mainTable.refresh();
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("actions.createNew");
            }
            @Override
            public boolean isEnabled() {
                MetaClass metaClass = mainTable.getDatasource().getMetaClass();
                return security.isEntityOpPermitted(metaClass, EntityOp.CREATE);
            }
        };
    }

    protected Action createCreateGroupAction() {
        return new AbstractAction("createGroup") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null)
                    createBtn.setPopupVisible(false);

                final ExtProject newEntity = metadata.create(ExtProject.class);
                ExtProject currentEntity = mainDs.getItem();

                //// TODO: 12.03.2016; Хорошилов; Нельзя создать группу из проекта. Добавить сообщение об этом пользователю
                if (currentEntity !=null && currentEntity.getIsGroup())
                    newEntity.setParentProject(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("tm$ProjectGroup.edit", newEntity, WindowManager.OpenType.DIALOG, params);

                window.addListener(new CloseListener() {

                    @Override
                    public void windowClosed(String actionId) {
                        mainTable.refresh();
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("actions.CreateGroup");
            }

            @Override
            public boolean isEnabled() {
                MetaClass metaClass = mainTable.getDatasource().getMetaClass();
                return security.isEntityOpPermitted(metaClass, EntityOp.CREATE);
            }
        };
    }

    protected Action CopyAction() {
        return new AbstractAction("copy") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null) createBtn.setPopupVisible(false);
                createFromCurrent();
            }

            @Override
            public String getCaption() {
                return getMessage("actions.Copy");
            }

            @Override
            public boolean isEnabled() {
                MetaClass metaClass = mainTable.getDatasource().getMetaClass();
                return security.isEntityOpPermitted(metaClass, EntityOp.CREATE);
            }
        };
    }

    protected Action EditAction()  {
        return new EditAction(mainTable) {

            @Override
            public void actionPerform(Component component) {
                Window window;
                ExtProject item = mainDs.getItem();
                if (item == null) return;

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "edit");

                if (item.getIsGroup()) {
                    window = openEditor("tm$ProjectGroup.edit", item, WindowManager.OpenType.DIALOG, params);
                } else {
                    window = openEditor("tm$Project.edit", item, WindowManager.OpenType.THIS_TAB, params);
                }

                window.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (Window.COMMIT_ACTION_ID.equals(actionId))
                            mainDs.refresh();

                        mainTable.requestFocus();
                    }
                });
            }

            @Override
            public String getCaption() {
                return super.getCaption(); //getMessage("actions.Edit");
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected void createFromCurrent() {
        ExtProject item = mainDs.getItem();

        if (item == null) {
            showNotification(getMessage("selectProject.msg"), NotificationType.TRAY);
            return;
        }
        if (item.getIsGroup()) {
            showNotification(getMessage("noCopyGroup.msg"), NotificationType.TRAY);
            return;
        }

        ExtProject newItem;
        //todo may be getEffectiveClass(Project.class)
        Class entityClass = metadata.getExtendedEntities().getEffectiveClass(ExtProject.class);
        Preconditions.checkNotNullArgument(entityClass);

        View entityEditView = metadata.getViewRepository().getView(entityClass, "ext-edit");
        LoadContext ctx = new LoadContext(entityClass).setId(item.getId()).setView(entityEditView);
        item = dataService.load(ctx);

        //todo  projectService.cloneProject
        //newItem = taskmanService.cloneTask(currentEntity, true, true, NumeratorType.ON_CREATE == tmConfig.getTaskNumeratorType());
        newItem = metadata.create(ExtProject.class);
        newItem.setName(item.getName());
        newItem.setName_en(item.getName_en());
        newItem.setProjectGroup(item.getProjectGroup());
        newItem.setParentProject(item.getParentProject());
        newItem.setIsGroup(item.getIsGroup());
        newItem.setFullName_ru(item.getFullName_ru());
        newItem.setFullName_en(item.getFullName_en());

        Map<String, Object> params = new HashMap<>();
        params.put("mode", "create");

        Window window = openEditor("tm$Project.edit", newItem, WindowManager.OpenType.THIS_TAB, params);

        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                mainTable.refresh();
            }
        });
    }

}