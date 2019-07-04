package com.haulmont.thesis.crm.web.ui.activitykind;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.core.app.TypedEntitiesTools;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.ActivityKind;
import com.haulmont.thesis.crm.web.ui.common.BaseFieldInfoDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ActivityKindEditor<T extends ActivityKind> extends AbstractEditor<T> {
    protected static final String ENTITY_TYPE_FIELD = "entityType";

    protected T activityKind;
    protected CrmConfig crmConfig;

    @Named("categoryDs")
    protected Datasource<T> categoryDs;
    @Named("fieldsDs")
    protected BaseFieldInfoDatasource<FieldInfo, UUID> fieldsDs;
    @Named("fieldsTable")
    protected Table fieldsTable;
    @Named("fieldsGroup")
    protected FieldGroup fieldGroup;

    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected TypedEntitiesTools typedEntitiesTools;

    @SuppressWarnings("Duplicates")
    @Override
    public void init(Map<String, Object> params) {
        crmConfig = configuration.getConfigCached(CrmConfig.class);

        fieldGroup.addCustomField(ENTITY_TYPE_FIELD, new FieldGroup.CustomFieldGenerator() {
            @Override
            public Component generateField(Datasource datasource, String propertyId) {
                LookupField typeField = componentsFactory.createComponent(LookupField.NAME);
                typeField.setDatasource(datasource, propertyId);
                typeField.setOptionsMap(getEntityTypeOptionsMap());
                return typeField;
            }
        });
    }

    protected Map<String, Object> getEntityTypeOptionsMap() {
        List<String> excludedTypes = crmConfig.getExcludedActivityKindMetaClasses();

        Collection<MetaClass> metaClasses = activityService.getCardTypes(excludedTypes);

        Map<String, Object> items = new TreeMap<>();
        for (MetaClass metaClass : metaClasses) {
            items.put(messages.getTools().getDetailedEntityCaption(metaClass), metaClass.getName());
        }
        return items;
    }

    @Override
    protected void initNewItem(final T item){
        LookupField typeField = (LookupField) fieldGroup.getFieldComponent(ENTITY_TYPE_FIELD);

        typeField.setEditable(true);
        typeField.addListener(new ValueListener() {
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                item.resetFields();
                item.setIsDefault(false);
                fieldsDs.refresh();
            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected void postInit() {
        activityKind = getItem();

        initFieldsTableColumn("visible");
        initFieldsTableColumn("required");

        fieldsDs.addListener(new CollectionDsListenerAdapter<FieldInfo>() {
            @Override
            public void valueChanged(FieldInfo source, String property, Object prevValue, Object value) {
                //noinspection unchecked
                ((DatasourceImplementation) categoryDs).modified(categoryDs.getItem());
            }
        });

        if (Datasource.State.INVALID.equals(fieldsDs.getState())) {
            fieldsDs.refresh();
        }

        fieldsDs.init();
    }

    @SuppressWarnings("Duplicates")
    protected void initFieldsTableColumn(final String columnName) {
        fieldsTable.addGeneratedColumn(columnName, new Table.ColumnGenerator<FieldInfo>() {
            @Override
            public Component generateCell(final FieldInfo entity) {
                String typeName = activityKind.getEntityType();
                if (columnName.equals("required") && !typedEntitiesTools.isFieldRequirable(typeName, entity))
                    return new Table.PlainTextCell("");

                return createCheckBox(columnName, entity);
            }
        });
    }

    protected CheckBox createCheckBox(final String columnName, final FieldInfo entity) {
        CheckBox cb = componentsFactory.createComponent(CheckBox.NAME);
        cb.setValue(entity.getValue(columnName));
        cb.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                entity.setValue(columnName, value);
            }
        });
        return cb;
    }

    @Override
    public void commitAndClose() {
        if (activityService.isSameKindExist(getItem()))
            showNotification(getMessage("kindNameAlreadyExist"), NotificationType.WARNING);
        else {
            getItem().updateFieldsXml();
            super.commitAndClose();
        }
    }
}