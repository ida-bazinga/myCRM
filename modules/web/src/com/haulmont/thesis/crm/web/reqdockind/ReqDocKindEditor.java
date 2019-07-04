package com.haulmont.thesis.crm.web.reqdockind;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.core.entity.DocCategory;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.crm.entity.City;
import com.haulmont.thesis.crm.entity.ReqDetail;
import com.haulmont.thesis.crm.entity.ReqDocKind;
import com.haulmont.thesis.web.ui.dockind.DocKindFieldInfoDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReqDocKindEditor<T extends ReqDocKind> extends AbstractEditor<T> {

    @Named("categoryDs")
    protected Datasource<T> categoryDs;
    @Named("fieldsDs")
    protected DocKindFieldInfoDatasource<FieldInfo, UUID> fieldsDs;
    @Named("fieldsTable")
    protected Table fieldsTable;
    @Named("fieldsGroup")
    protected FieldGroup fieldGroup;

    @Inject
    private DataManager dataManager;

    protected static final String ENTITY_TYPE_FIELD = PersistenceHelper.getEntityName(ReqDetail.class);



    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void initNewItem(final T item){
        LookupField typeField = (LookupField) fieldGroup.getFieldComponent("docCategory");

        typeField.setEditable(true);
        typeField.addListener(new ValueListener() {
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                item.resetFields();
                item.setEntityType(ENTITY_TYPE_FIELD);
                fieldsDs.refresh();
            }
        });
    }

    @Override
    protected void postInit() {
        categoryDs.addListener(new DsListenerAdapter<T>() {
            @Override
            public void valueChanged(T source, String property, Object prevValue, Object value) {
                switch (property) {
                    case "docCategory":
                        DocCategory category = getItem().getDocCategory();
                        if (category != null) {
                            getItem().setName(String.format("%s(%s)", category.getName(), category.getDocType().getName()));
                        }

                        break;
                }
            }
        });

        getItem().setEntityType(ENTITY_TYPE_FIELD);

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

    @Override
    public void commitAndClose() {
        if (PersistenceHelper.isNew(getItem()) && isSameKindExist()) {
            showNotification(getMessage("kindAlreadyExist"), NotificationType.WARNING);
            return;
        }

        getItem().updateFieldsXml();
        super.commitAndClose();
    }

    private boolean isSameKindExist() {
        LoadContext loadContext = new LoadContext(City.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$ReqDocKind e where e.docCategory.id = :Id")
                .setParameter("Id", getItem().getDocCategory().getId());
        List<ReqDocKind> reqDocKinds = dataManager.loadList(loadContext);
        if (reqDocKinds.size() > 0) {
            return true;
        }
        return false;
    }

}