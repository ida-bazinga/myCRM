package com.haulmont.thesis.crm.web.ui.campaignkind;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;

import java.util.*;

import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.core.app.TypedEntitiesTools;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.core.enums.CategoryAttrsPlace;
import com.haulmont.thesis.crm.core.app.service.CampaignService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CampaignKind;
import com.haulmont.thesis.crm.web.ui.common.BaseFieldInfoDatasource;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.MappedSuperclass;

public class CampaignKindEditor<T extends CampaignKind> extends AbstractEditor<T> {
    protected static final String ENTITY_TYPE_FIELD = "entityType";

    protected T campaignKind;
    protected CrmConfig crmConfig;

    @Named("categoryDs")
    protected Datasource<T> categoryDs;
    @Named("fieldsDs")
    protected BaseFieldInfoDatasource<FieldInfo, UUID> fieldsDs;
    @Named("fieldsTable")
    protected Table fieldsTable;
    @Named("mainFieldsGroup")
    protected FieldGroup mainFieldsGroup;
    @Named("addFieldsGroup")
    protected FieldGroup addFieldsGroup;
    @Named("addFieldsGroup.categoryAttrsPlace")
    protected LookupField categoryAttrsPlace;
    @Named("addFieldsGroup.additionalFieldsTabName")
    protected TextField additionalFieldsTabName;

    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected CampaignService campaignService;
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

        mainFieldsGroup.addCustomField(ENTITY_TYPE_FIELD, new FieldGroup.CustomFieldGenerator() {
            @Override
            public Component generateField(Datasource datasource, String propertyId) {
                LookupField typeField = componentsFactory.createComponent(LookupField.NAME);
                typeField.setDatasource(datasource, propertyId);
                typeField.setOptionsMap(getEntityTypeOptionsMap());
                return typeField;
            }
        });

        categoryAttrsPlace.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                CategoryAttrsPlace attributesPlace = (CategoryAttrsPlace) value;

                additionalFieldsTabName.setVisible(attributesPlace == CategoryAttrsPlace.SEPARATE_TAB);
                additionalFieldsTabName.setRequired(attributesPlace == CategoryAttrsPlace.SEPARATE_TAB);

                if (attributesPlace == CategoryAttrsPlace.SEPARATE_TAB && StringUtils.isBlank(getItem().getAdditionalFieldsTabName())) {
                    additionalFieldsTabName.setValue(getMessage("additionalFieldsTabNameDefault"));
                }

            }
        });
    }

    protected Map<String, Object> getEntityTypeOptionsMap() {
        Collection<MetaClass> metaClasses = getCardTypes("crm$BaseCampaign");

        Map<String, Object> items = new TreeMap<>();
        for (MetaClass metaClass : metaClasses) {
            items.put(messages.getTools().getDetailedEntityCaption(metaClass), metaClass.getName());
        }

        return items;
    }

    // TODO: 10.03.2018 to service
    public Set<MetaClass> getCardTypes(String entityName) {
        MetaClass cardMetaClass = metadata.getClassNN(entityName);
        Collection<MetaClass> metaClasses = cardMetaClass.getDescendants();

        List<String> excludedTypes = crmConfig.getExcludedCampaignKindMetaClasses();

        Set<MetaClass> items = new HashSet<>();
        for (MetaClass metaClass : metaClasses) {
            metaClass = metadata.getClassNN(metaClass.getName());
            if (!excludedTypes.contains(metaClass.getName()) && metaClass.getJavaClass().getAnnotation(MappedSuperclass.class) == null) {
                items.add(metaClass);
            }
        }

        return items;
    }

    @Override
    protected void initNewItem(final T item){
        LookupField typeField = (LookupField) mainFieldsGroup.getFieldComponent(ENTITY_TYPE_FIELD);

        typeField.setEditable(true);
        typeField.addListener(new ValueListener() {
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                item.resetFields();
                item.setIsDefault(false);
                fieldsDs.refresh();
            }
        });
    }

    @Override
    protected void postInit() {
        campaignKind = getItem();

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

    protected void initFieldsTableColumn(final String columnName) {
        fieldsTable.addGeneratedColumn(columnName, new Table.ColumnGenerator<FieldInfo>() {
            @Override
            public Component generateCell(final FieldInfo entity) {
                String typeName = campaignKind.getEntityType();
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
        if (campaignService.isSameKindExist(getItem()))
            showNotification(getMessage("kindNameAlreadyExist"), NotificationType.WARNING);
        else {
            getItem().updateFieldsXml();
            super.commitAndClose();
        }
    }
}