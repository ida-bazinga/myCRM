/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.basic.edit;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.crm.entity.ActivityRes;
import com.haulmont.thesis.crm.entity.BaseActivity;
import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.gui.processaction.activity.DefaultActivityAccessData;
import com.haulmont.thesis.crm.web.CrmApp;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class AbstractActivityCardEditor<T extends BaseActivity> extends AbstractEditor<T> {

    protected boolean justCreated;
    protected DefaultActivityAccessData<T> accessData;

    @Inject
    protected Datasource<T> cardDs;

    protected DefaultActivityAccessData<T> createAccessData() {
        return AppBeans.getPrototype(DefaultActivityAccessData.NAME, getContext().getParams());
    }

    protected DefaultActivityAccessData<T> getAccessData() {
        return accessData;
    }

    @Override
    public void init(Map<String, Object> params) {
        justCreated = BooleanUtils.isTrue((Boolean) params.get("justCreated"));

        findStandardComponents();
        initDsListeners();
        addCloseListener();
    }

    protected void findStandardComponents() {
    }

    protected void initDsListeners(){
        cardDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                if ("campaign".equals(property)) {
                    BaseCampaign campaign = (BaseCampaign) value;
                    if (campaign!= null) {
                        getItem().setProject(campaign.getProject());
                    }
                }
                if ("result".equals(property)) {
                    ActivityRes result = (ActivityRes) value;

                    boolean isNeedDetails = result!= null && result.getIsNeedDetails();

                    setRequiredGridRow("details", isNeedDetails);
                }
            }
        });
    }

    protected void addCloseListener() {
        addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
                    reloadAppFolders();
                }
            }
        });
    }

    protected void reloadAppFolders() {
        CrmApp.getInstance().getAppWindow().reloadAppFolders();
    }

    protected boolean isNewItem(T item) {
        return PersistenceHelper.isNew(item) || justCreated;
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setItem(Entity item) {
        if (isNewItem((T) item)) {
            DatasourceImplementation parentDs = (DatasourceImplementation) ((Editor) frame).getParentDs();
            if (parentDs == null || !parentDs.getItemsToCreate().contains(item)) {
                initNewItem((T) item);
            }
        }
        ((Editor) frame).setItem(item);

        accessData = createAccessData();
        if (accessData != null) {
            getAccessData().setItem(getItem());
            getContext().getParams().put("accessData", accessData);
        }
        postInit();
    }

    @Override
    protected void postInit() {
        super.postInit();
        T item = getItem();

        if (!isNewItem(item)){
            setCaption(item.getInstanceName());
        }

        applyAccessData(getMainContainerEditable());
    }

    protected void applyAccessData(Boolean editable) {
        Component.Container mainContainer = getMainContainer();
        getAccessData().applyAccessDataForContainerEditable(mainContainer, editable);
    }

    @Nonnull
    protected Component.Container getMainContainer() {
        return getComponentNN("fieldsPane");
    }

    protected boolean getMainContainerEditable() {
        return true;
    }


    protected void setVisibleGridRow(String componentId, boolean visibility) {
        if (StringUtils.isBlank(componentId)) return;
        visibility = BooleanUtils.isTrue(visibility);

        Component fieldsComponent = getComponent("fields");
        if (fieldsComponent == null) {
            throw new GuiDevelopmentException("ActivityCard editor must contain component 'fields'", frame.getId());
        }

        Collection<Component> components = getContainerFields();
        Component component = findComponent(componentId, components);

        if (component != null){
            component.setVisible(visibility);
            String labelId = componentId + "Label";
            Component label = findComponent(labelId, components);
            if (label != null) label.setVisible(visibility);

            if (visibility && component instanceof OptionsField) {
                CollectionDatasource collectionDatasource = ((OptionsField) component).getOptionsDatasource();
                activateDs(collectionDatasource);
            }
        }
    }

    protected void setRequiredGridRow(String componentId, boolean required) {
        if (StringUtils.isBlank(componentId)) return;
        required = BooleanUtils.isTrue(required);

        Collection<Component> components = getContainerFields();

        Component component = findComponent(componentId, components);
        if (component instanceof Field){
            ((Field) component).setRequired(required);
        }
    }


    @SuppressWarnings("Duplicates")
    protected Collection<Component> getContainerFields(){
        final Component fieldsComponent = getComponent("fields");
        if (fieldsComponent == null) {
            throw new GuiDevelopmentException("ActivityCard editor must contain component 'fields'", frame.getId());
        }

        Collection<Component> content = Collections.emptyList();

        if (fieldsComponent instanceof Component.Container)
            content = ((Component.Container) fieldsComponent).getComponents();
        else if (fieldsComponent instanceof FieldGroup)
            content = FluentIterable.from(((FieldGroup) fieldsComponent).getFields())
                    .transform(new Function<FieldGroup.FieldConfig, Component>() {
                        @Override
                        public Component apply(FieldGroup.FieldConfig fieldConfig) {
                            return ((FieldGroup) fieldsComponent).getFieldComponent(fieldConfig.getId());
                        }
                    }).toList();
        return content;
    }

    @Nullable
    protected Component findComponent(String componentId, Collection<Component> components) {
        Component result = null;
        for (final Component component : components) {
            if (component.getId() != null && component.getId().equals(componentId)){
                result = component;
                break;
            }
        }
        return result;
    }

    protected void activateDs(Datasource datasource) {
        getAccessData().activateDs(datasource);
    }
}