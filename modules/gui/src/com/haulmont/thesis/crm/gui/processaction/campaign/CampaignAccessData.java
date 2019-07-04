/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.processaction.campaign;

import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.OptionsField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.core.app.CardService;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.core.enums.CategoryAttrsPlace;
import com.haulmont.thesis.core.process.EndorsementConstants;
import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.gui.processaction.DefaultCardAccessData;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ManagedBean(CampaignAccessData.NAME)
@Scope("prototype")
public class CampaignAccessData<T extends BaseCampaign> extends DefaultCardAccessData<T> {
    public static final String NAME = "crm_CampaignAccessData";
    protected static final String JUST_CREATED_PARAM = "justCreated";

    protected List<FieldInfo> fieldInfos;

    @Inject
    protected CardService cardService;

    public CampaignAccessData(Map<String, Object> params) {
        super(params);
    }

    @Override
    public void setItem(T item) {
        super.setItem(item);

        if (item.getKind() != null) {
            fieldInfos = item.getKind().getFields(true);
            for (FieldInfo fieldInfo : fieldInfos) {
                fieldInfoByName.put(fieldInfo.getName(), fieldInfo);
            }
        }
    }

    @Override
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
        super.visitDatasourceComponent(component, components, property);
    }

    protected void activateDs(Datasource datasource) {
        if (datasource instanceof CollectionDatasource.SupportsRefreshMode &&
                ((CollectionDatasource.SupportsRefreshMode) datasource)
                        .getRefreshMode().equals(CollectionDatasource.RefreshMode.NEVER)) {
            ((CollectionDatasource.SupportsRefreshMode) datasource)
                    .setRefreshMode(CollectionDatasource.RefreshMode.ALWAYS);
            datasource.refresh();
        }
    }

    public boolean getRuntimePropertiesInMainTab() {
        return (item.getKind().getCategoryAttrsPlace() == CategoryAttrsPlace.MAIN_TAB);
    }

    public boolean getRuntimePropertiesInSeparateTab() {
        return (item.getKind().getCategoryAttrsPlace() == CategoryAttrsPlace.SEPARATE_TAB);
    }

    @Override
    public boolean isAttachmentMainColumnEnabled() {
        return true;
    }

    @Override
    public boolean isShowProcessHeaderVisible() {
        return StringUtils.isNotBlank(item.getState());
    }

    public boolean getGlobalEditable() {
        if (PersistenceHelper.isNew(item)) return true;

        return validateDocIfNotTemplate();
    }

    protected boolean validateDocIfNotTemplate() {
        if (isJustCreated())
            return true;
        //Initiators of all processes have name 'Initiator' so we'll compare only with one constant
        if (userSessionTools.isCurrentUserInProcRole(item, EndorsementConstants.PROC_ROLE_INITIATOR))
            return isEditAllowedForInitiator();
        return userSessionTools.isCurrentUserAdministrator();

    }

    protected boolean isEditAllowedForInitiator() {
        return true;
    }

    protected boolean isJustCreated() {
        return (BooleanUtils.isTrue((Boolean) params.get(JUST_CREATED_PARAM)) || (item.getJbpmProcessId() == null && isUserCardCreator()));
    }
}
