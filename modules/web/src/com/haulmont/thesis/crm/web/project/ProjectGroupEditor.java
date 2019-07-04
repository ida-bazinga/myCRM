package com.haulmont.thesis.crm.web.project;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.thesis.core.entity.ProjectGroup;
import com.haulmont.thesis.crm.entity.ExtProject;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ProjectGroupEditor<T extends ExtProject> extends AbstractEditor<T> {

    @Inject
    private DataManager dataManager;

    @Named("parentGroup")
    protected LookupPickerField parentGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initParentProject(params);

    }

    private void initParentProject(final Map<String, Object> params) {
        PickerField.LookupAction projectLookupAction = (PickerField.LookupAction)parentGroup.getActionNN(PickerField.LookupAction.NAME);

        projectLookupAction.setLookupScreen("tm$ProjectGroup.lookup");
        projectLookupAction.setLookupScreenParams(params);

/*        PickerField.OpenAction projectOpenAction = (PickerField.OpenAction)parentGroup.getActionNN(PickerField.OpenAction.NAME);
        projectOpenAction.setEditScreen("tm$ProjectGroup.edit");
        projectOpenAction.setEditScreenParams(params);*/
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setProjectGroup(getProjectGroup());
        item.setIsGroup(true);
    }

    private ProjectGroup getProjectGroup() {
        LoadContext loadContext = new LoadContext(ProjectGroup.class).setView("_local");
        loadContext.setQueryString("select e from tm$ProjectGroup e where e.name = :groupName").setParameter("groupName", "Основной проект");
        List<ProjectGroup> pg = dataManager.loadList(loadContext);
        return pg.get(0);
    }

}