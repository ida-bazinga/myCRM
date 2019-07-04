package com.haulmont.thesis.crm.web.product;

import java.util.Map;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.web.gui.components.WebFilter;
import com.haulmont.thesis.web.actions.ThesisExcelAction;

import javax.inject.Named;

public class ProductBrowser extends AbstractLookup {

    protected GroupTable table;

    @Named("mainTable.create")
    protected CreateAction mainTableCreateAction;

    @Named("mainTable.edit")
    protected EditAction mainTableEditAction;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        WindowManager.OpenType openType = (WindowManager.OpenType) params.get("openType");
        mainTableCreateAction.setOpenType(openType == null ? WindowManager.OpenType.THIS_TAB : openType);
        mainTableEditAction.setOpenType(openType == null ? WindowManager.OpenType.THIS_TAB : openType);

        table = getComponent("mainTable");
        Button excel = getComponent("excelBtn");
        if(excel != null) {
            excel.setAction(new ThesisExcelAction(table, (WebFilter)this.getComponent("genericFilter")));
        }
    }
}