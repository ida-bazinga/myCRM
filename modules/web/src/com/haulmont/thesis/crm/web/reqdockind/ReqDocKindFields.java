/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.reqdockind;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.core.entity.DocCategory;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.crm.entity.ReqDetail;
import com.haulmont.thesis.crm.entity.ReqDocKind;

import java.util.ArrayList;
import java.util.List;

public class ReqDocKindFields {

    protected DataManager dataManager;
    protected DocCategory category;
    protected Table table;
    protected List<FieldInfo> fields;
    protected IFrame frame;
    protected Messages messages;
    protected List<String> removeColumns = new ArrayList<>();

    public ReqDocKindFields(DocCategory category, Table table, IFrame frame) {
        this.category = category;
        this.table = table;
        this.frame = frame;
        this.messages = AppBeans.get(Messages.NAME);
        this.dataManager = AppBeans.get(DataManager.class);
    }

    protected boolean isFields() {
        ReqDocKind reqDocKind = getReqDocKind();
        if (reqDocKind != null) {
            reqDocKind.setEntityType(PersistenceHelper.getEntityName(ReqDetail.class));
            this.fields = reqDocKind.getFields();
            return true;
        }
        return false;
    }

    protected ReqDocKind getReqDocKind() {
        LoadContext loadContext = new LoadContext(ReqDocKind.class);
        loadContext.setQueryString("select e from crm$ReqDocKind e where e.docCategory.id = :categoryId")
                .setParameter("categoryId", category.getId())
                .getFirstResult();
        return dataManager.load(loadContext);
    }

    public void initTable() {
        if (isFields()) {      
            List<Table.Column> columns = this.table.getColumns();

            for (Table.Column column : columns) {
                settingTable(column);
            }

            removeColumn();
        }
    }

    protected FieldInfo getFieldInfo(String column) {
        for (FieldInfo field : this.fields) {
            if (column.equals(field.getName()))
                    return field;
        }
        return null;
    }

    protected void removeColumn() {
        for (String columnId : removeColumns) {
            this.table.removeColumn(this.table.getColumn(columnId));
        }
    }

    protected void showWarning(String message){
        if (frame instanceof Window) {
            final Window window = (Window) frame;
            String mes = messages.getMessage(this.getClass(), "reqDocKindFildsInfo");
            window.showNotification(String.format(mes, message), IFrame.NotificationType.TRAY);

        }
    }

    protected void settingTable(Table.Column column) {
        String columnId = column.getId().toString();
        FieldInfo field = getFieldInfo(columnId);

        if (field != null) {
            boolean isRequired = field.getRequired();
            boolean isVisible = field.getVisible();
            String locName = field.getLocName();

            if (isVisible) {
                this.table.setColumnCaption(column, locName);
                this.table.setRequired(column, isRequired, "REQ");
                if (isRequired) {
                    validateTable(columnId, locName);
                }
            } else {
               this.removeColumns.add(columnId);
            }
        }
    }

    protected void validateTable(final String columnId, final String locName) {
        com.haulmont.cuba.gui.components.Field.Validator validator = new Field.Validator() {
            @Override
            public void validate(Object value) throws ValidationException {
                List<ReqDetail> details = new ArrayList(table.getDatasource().getItems());
                for (ReqDetail d : details) {
                    boolean isException = validateColumn(d, columnId);
                    if (isException) {
                        showWarning(String.format("%s (%s)", locName, columnId));
                        throw new ValidationException("ddd");
                    }
                }
            }
        };
        this.table.addValidator(validator);
    }

    protected boolean validateColumn(ReqDetail d, String columnId) {
        if (d.getValue(columnId) == null) {
            return true;
        }
        return false;
    }


}
