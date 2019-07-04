package com.haulmont.thesis.crm.web.nomenclature;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.web.gui.components.WebFilter;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.web.actions.ThesisExcelAction;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class NomenclatureBrowser extends AbstractLookup {

    @Named("nomenclatureTable")
    protected GroupTable table;
    @Named("nomenclatureTable.create")
    protected CreateAction createAction;

    @Named("nomenclatureTable.edit")
    protected EditAction editAction;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Work1C work1C;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        WindowManager.OpenType openType = (WindowManager.OpenType) params.get("openType");
        createAction.setOpenType(openType == null ? WindowManager.OpenType.THIS_TAB : openType);
        editAction.setOpenType(openType == null ? WindowManager.OpenType.THIS_TAB : openType);

        Button excel = getComponent("excelBtn");
        if(excel != null) {
            excel.setAction(new ThesisExcelAction(table, (WebFilter)this.getComponent("genericFilter")));
        }
    }

    @Override
    public void ready(){
        table.expandAll();
    }

}