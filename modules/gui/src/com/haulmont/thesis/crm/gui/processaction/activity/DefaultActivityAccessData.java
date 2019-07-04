/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.processaction.activity;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.app.UserSessionTools;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.crm.entity.BaseActivity;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.context.annotation.Scope;

import javax.annotation.Nullable;
import java.util.*;

@org.springframework.stereotype.Component(DefaultActivityAccessData.NAME)
@Scope("prototype")
public class DefaultActivityAccessData<T extends BaseActivity> extends AbstractAccessData implements ActivityAccessData<T> {
    public static final String NAME = "crm_DefaultActivityAccessData";
    protected static final String JUST_CREATED_PARAM = "justCreated";

    protected T item;
    protected Map<String, FieldInfo> fieldInfoByName = new HashMap<>();
    protected List<FieldInfo> fieldInfos;

    protected Messages messages;
    protected UserSessionSource userSessionSource;
    protected UserSessionTools userSessionTools;

    public DefaultActivityAccessData(Map<String, Object> params) {
        super(params);
        messages = AppBeans.get(Messages.NAME);
        userSessionTools = AppBeans.get(UserSessionTools.NAME);
        userSessionSource = AppBeans.get(UserSessionSource.NAME);
    }

    @Override
    public void setItem(T item) {
        this.item = item;

        if (item.getKind() != null) {
            fieldInfos = item.getKind().getFields(true);
            for (FieldInfo fieldInfo : fieldInfos) {
                fieldInfoByName.put(fieldInfo.getName(), fieldInfo);
            }
        }
    }

    @Override
    public boolean getSaveEnabled() {
        return isEnableButtonsSaving();
    }

    protected boolean isEnableButtonsSaving() {
        return getGlobalEditable();
    }

    @Override
    public boolean getGlobalEditable() {
        if (PersistenceHelper.isNew(item)) return true;

        return userSessionTools.isCurrentUserAdministrator() || isEditAllowedForCardCreator();
    }

    protected boolean isEditAllowedForCardCreator() {
        if (isJustCreated())
            return true;

        return isUserCardCreator() && (item.getResult() == null || item.getState().equals(ActivityStateEnum.NEW.getId()));
    }

    protected boolean isUserCardCreator() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        return user.getLogin().equals(item.getCreatedBy()) || user.equals(item.getSubstitutedCreator()) ||
                (item.getOwner() != null && item.getOwner().getUser() != null && user.equals(item.getOwner().getUser()));
    }

    @SuppressWarnings("Duplicates")
    public void applyAccessDataForContainerEditable(Component.Container container, boolean globalEditable){
        for (final Component component : container.getComponents()) {
            applyToComponent(component, globalEditable, this, container.getComponents());

            Collection<Component> content = Collections.emptyList();
            if (component instanceof Component.Container)
                content = ((Component.Container) component).getComponents();
            else if (component instanceof FieldGroup)
                content = FluentIterable.from(((FieldGroup) component).getFields())
                        .transform(new Function<FieldGroup.FieldConfig, Component>() {
                            @Override
                            public Component apply(FieldGroup.FieldConfig fieldConfig) {
                                return ((FieldGroup) component).getFieldComponent(fieldConfig.getId());
                            }
                        }).toList();

            if (CollectionUtils.isNotEmpty(content))
                for (Component c : content)
                    applyToComponent(c, globalEditable, this, content);
        }
    }

    protected void applyToComponent(Component component, boolean editable, AbstractAccessData data, Collection<Component> components) {
        if (component instanceof Component.Editable && !editable) {
            ((Component.Editable) component).setEditable(false);
        }

        if (data != null) {
            data.visitComponent(component, components);
        }
    }

    @Override
    public void visitComponent(Component component, Collection<Component> components) {

        if (component instanceof DatasourceComponent && canVisitDatasourceComponent((DatasourceComponent) component)) {
            MetaProperty property = ((DatasourceComponent) component).getMetaPropertyPath().getMetaProperty();
            visitDatasourceComponent(component, components, property);
        }
    }

    protected boolean canVisitDatasourceComponent(DatasourceComponent component) {
        Datasource datasource = component.getDatasource();
        return datasource != null && datasource.getMetaClass().equals(item.getMetaClass());
    }

    protected void visitDatasourceComponent(Component component, Collection<Component> components, MetaProperty property) {
        FieldInfo fieldInfo = findField(property);
        if (fieldInfo != null) {
            Label label = findLabel(component, components);

            if (!fieldInfo.getVisible()) {
                component.setVisible(false);
                if (label != null) {
                    label.setVisible(false);
                }
            } else {
                if (component instanceof OptionsField) {
                    CollectionDatasource collectionDatasource = ((OptionsField) component).getOptionsDatasource();
                    activateDs(collectionDatasource);
                }
            }

            if (component instanceof Field && (fieldInfo.getRequired() || property.isMandatory())) {
                ((Field) component).setRequired(true);
                if (label != null && label.getValue() != null)
                    ((Field) component).setRequiredMessage(
                            messages.formatMessage(messages.getMainMessagePack(), "error.requiredValue", label.getValue().toString()));
            }
        }
    }

    @Nullable
    protected FieldInfo findField(MetaProperty property) {
        return fieldInfoByName.get(property.getName());
    }

    protected Label findLabel(Component component, Collection<Component> components) {
        String id = component.getId();
        if (id == null)
            return null;

        String labelId = id + "Label";
        for (Component c : components) {
            if (c instanceof Label && labelId.equals(c.getId())) {
                return (Label) c;
            }
        }
        return null;
    }

    public void activateDs(Datasource datasource) {
        if (datasource instanceof CollectionDatasource.SupportsRefreshMode &&
                ((CollectionDatasource.SupportsRefreshMode) datasource)
                        .getRefreshMode().equals(CollectionDatasource.RefreshMode.NEVER)) {
            ((CollectionDatasource.SupportsRefreshMode) datasource)
                    .setRefreshMode(CollectionDatasource.RefreshMode.ALWAYS);
            datasource.refresh();
        }
    }

    protected boolean isJustCreated() {
        return BooleanUtils.isTrue((Boolean) params.get(JUST_CREATED_PARAM));
    }
}
