package com.haulmont.thesis.crm.web.ui.cashdocument;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.crm.entity.CashDocumentPosition;

import javax.inject.Named;
import java.util.Map;

public class CashDocumentBrowser extends AbstractLookup {

    @Named("documentsTable")
    GroupTable documentsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initProductColumnTable();
    }

    @Override
    public void ready() {
        documentsTable.expandAll();
    }

    protected void initProductColumnTable() {
        final String columnName = "product";
        Table docPositionsTable = getComponentNN("docPositionsTable");
        docPositionsTable.addGeneratedColumn(columnName, new Table.ColumnGenerator<CashDocumentPosition>() {
            @Override
            public Component generateCell(CashDocumentPosition entity) {
                if (entity.getCost() != null && entity.getCost().getProduct() != null){
                    return new Table.PlainTextCell(entity.getCost().getProduct().getTitle_ru());
                }else{
                    return new Table.PlainTextCell(entity.getCommodityName());
                }
            }
        });
        docPositionsTable.getColumn(columnName).setCaption(getMessage(columnName));
    }

    public void importDoc() {
        openWindow("cashDocumentImporter", WindowManager.OpenType.DIALOG);
    }
}