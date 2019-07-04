package com.haulmont.thesis.crm.web.ui.callcampaigntarget;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;

import java.text.SimpleDateFormat;
import java.util.*;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.crm.entity.CallActivity;
import com.haulmont.thesis.crm.entity.CallCampaignTrgt;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import com.haulmont.thesis.gui.components.TableSettingsPopupButton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CallCampaignTargetsFrame<T extends CallCampaignTrgt> extends AbstractFrame {

    protected final static String SHOW_ONLY_GROUP = "showOnlyGroup";
    protected final static String BULK_EDITOR_ID = "bulkEditor";
    protected boolean showOnlyGroup = false;

    protected HierarchicalDatasource<T, UUID> targetsDs;
    protected TreeTable targetsTable;
    protected TableSettingsPopupButton tableSettings;


    @Override
    public void init(final Map<String, Object> params) {

        findStandardComponents();

        initNextActivityColumns();
        initTableColoring();

        targetsTable.addAction(EditAction());
        targetsTable.addAction(RemoveAction());

        initTableSettings();
    }

    protected void initTableSettings() {
        if (tableSettings == null) {
            return;
        }

        tableSettings.removeAllActions();
        tableSettings.removeAllLabels();
        tableSettings.removeAllSeparators();

        tableSettings.addAction(createShowGroupAction());
        tableSettings.addFilterMaxResultsActions();
        setShowOnlyGroupChecked(isShowOnlyGroup());
    }

    protected Action createShowGroupAction(){
        return new BaseAction(SHOW_ONLY_GROUP) {
            @Override
            public void actionPerform(Component component) {
                setShowOnlyGroup(!isShowOnlyGroup());
                setShowOnlyGroupChecked(isShowOnlyGroup());
            }
        };
    }

    protected boolean isShowOnlyGroup() {
        return showOnlyGroup;
    }

    protected void setShowOnlyGroup(boolean showOnlyGroupValue) {
        this.showOnlyGroup = showOnlyGroupValue;
        BulkEditor bulkEditor;
        ButtonsPanel panel = targetsTable.getButtonsPanel();
        final Map<String, Object> refreshParams = new HashMap<>();
        final String HIDE_PARAM_NAME = "hideGroup";
        if (showOnlyGroupValue){
            refreshParams.put(HIDE_PARAM_NAME, true);

            bulkEditor = createStateBulkEditor();
            panel.add(bulkEditor);
        }else{
            refreshParams.put(HIDE_PARAM_NAME, null);

            bulkEditor = panel.getComponent(BULK_EDITOR_ID);
            if (bulkEditor != null){
                panel.remove(bulkEditor);
            }
        }
        targetsDs.refresh(refreshParams);
        setShowOnlyGroupChecked(showOnlyGroupValue);
    }

    @Nonnull
    protected BulkEditor createStateBulkEditor() {
        ComponentsFactory componentFactory = AppBeans.get(ComponentsFactory.NAME);
        BulkEditor bulkEditor = componentFactory.createComponent(BulkEditor.NAME);
        bulkEditor.setId(BULK_EDITOR_ID);
        bulkEditor.setExcludePropertiesRegex("^(?!(state|operator)$).*$");
        bulkEditor.setListComponent(targetsTable);
        bulkEditor.setVisible(true);
        return bulkEditor;
    }

    protected void setShowOnlyGroupChecked(boolean showShowBulkEditChecked) {
        if (tableSettings != null) {
            tableSettings.setChecked(SHOW_ONLY_GROUP, showShowBulkEditChecked);
        }
    }

    public void hideTableColumn(String name){
        targetsTable.getColumn(name).setCollapsed(true);
    }

    protected void findStandardComponents() {
        targetsTable = getComponentNN("targetsTable");
        targetsDs = getDsContext().getNN("targetsDs");

        tableSettings = getComponentNN("tableSettings");
    }

    protected void initNextActivityColumns(){
        ArrayList<String> columnNames = Lists.newArrayList("nextCallTime", "nextCommunication", "nextOwner", "nextContact");

        for (final String columnName : columnNames){
            targetsTable.addGeneratedColumn(columnName , new Table.ColumnGenerator<T>() {
                @Override
                public Component generateCell(T entity) {
                    CallActivity activity = entity.getNextActivity();
                    String cellContent = "";
                    switch (columnName){
                        case "nextCallTime" :
                            SimpleDateFormat sdf = new SimpleDateFormat(messages.getMainMessage("dateTimeFormat"));
                            cellContent = activity != null ? sdf.format(activity.getEndTimePlan()) : "";
                            break;
                        case "nextCommunication":
                            cellContent = activity != null && activity.getCommunication() != null ? activity.getCommunication().getMaskedAddress() : "";
                            break;
                        case "nextOwner":
                            cellContent = activity != null && activity.getOwner() != null ? activity.getOwner().getName() : "";
                            break;
                        case "nextContact":
                            cellContent = activity != null && activity.getContactPerson() != null ? activity.getContactPerson().getFullName() : "";
                            break;
                    }
                    return new Table.PlainTextCell(cellContent);
                }
            });
            targetsTable.getColumn(columnName).setCollapsed(true);
        }
    }

    protected void initTableColoring() {
        targetsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null) {
                    if (entity.getState() != null && entity.getState().equals(ActivityStateEnum.LOCKED)) return "taskremind";

                    T group = entity.getIsGroup() ? entity : (T) entity.getParent();
                    if (group != null && group.getState() != null){
                        if (group.getState().equals(ActivityStateEnum.COMPLETED)) return "thesis-task-finished";
                    }
                }
                return null;
            }
        });
    }

    protected Action EditAction()  {
        return new EditAction(targetsTable) {

            @Override
            public void actionPerform(Component component) {
                Window window;
                T item = targetsDs.getItem();
                if (item == null) return;

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "edit");

                if (item.getIsGroup()) {
                    window = openEditor("crm$CallCampaignTargetGroup.edit", item, WindowManager.OpenType.DIALOG, params);
                } else {
                    window = openEditor("crm$CallCampaignTrgt.edit", item, WindowManager.OpenType.DIALOG, params);
                }

                window.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (Window.COMMIT_ACTION_ID.equals(actionId))
                            targetsDs.refresh();

                        targetsTable.requestFocus();
                    }
                });
            }
        };
    }

    // TODO: 02.04.2018 Добавить удаление групп
    protected Action RemoveAction()  {
        return new RemoveAction(targetsTable) {

            @Override
            public void actionPerform(Component component) {
                if (!isEnabled())
                    return;

                Set<T> selected = target.getSelected();
                if (!selected.isEmpty()) {
                    Set<T> removed = new HashSet<>();

                    for (T item : selected) {
                        if (item.getIsGroup()) {
                            removed.addAll(getSubTargets(item.getId()));
                        }
                        removed.add(item);
                    }
                    confirmAndRemove(removed);
                }
            }

            @Override
            protected void afterRemove(Set selected) {
                if (selected.isEmpty())
                    return;

                Set<T> removed = new HashSet<>();
                for (UUID groupId : targetsDs.getRootItemIds()) {
                    if (isEmptyGroup(groupId) ) {
                        removed.add(targetsDs.getItem(groupId));
                    }
                }
                doRemove(removed, autocommit);
            }
        };
    }

    protected Collection<T> getSubTargets(UUID groupId){
        if (targetsDs.getHierarchyPropertyName() != null) {
            final T groupItem = targetsDs.getItem(groupId);
            if (groupItem == null)
                return Collections.emptyList();

            return Lists.newArrayList(filterTargets(groupItem));
        }
        return Collections.emptyList();
    }

    protected boolean isEmptyGroup(UUID groupId){
        if (targetsDs.getHierarchyPropertyName() != null) {
            final T groupItem = targetsDs.getItem(groupId);
            if (groupItem == null)
                return false;

            return Iterables.isEmpty(filterTargets(groupItem));
        }
        return false;
    }

    protected Iterable<T> filterTargets(final T groupItem) {
        return Iterables.filter(targetsDs.getItems(), new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return input.getParent() != null && input.getParent() == groupItem;
            }
        });
    }
}