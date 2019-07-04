package com.haulmont.thesis.crm.web.ui.activityresult;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.ActivityRes;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ActivityResEditor extends AbstractEditor<ActivityRes> {
    protected static final String ENTITY_TYPE_FIELD = "entityType";
    protected CrmConfig crmConfig;

    @Named("fieldGroup")
    protected FieldGroup fieldGroup;

    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected ActivityService activityService;

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
            items.put(messages.getTools().getEntityCaption(metaClass), metaClass.getName());
        }
        return items;
    }
}