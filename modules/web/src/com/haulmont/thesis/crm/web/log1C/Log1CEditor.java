package com.haulmont.thesis.crm.web.log1C;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.entity.Log1C;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class Log1CEditor<T extends Log1C> extends AbstractEditor<T> {

    @Inject
    private Work1C work1C;
    @Inject
    private Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }


    @Override
    protected void postInit() {
        LinkButton openWinEdit = getComponent("openWinEdit");
        openWinEdit.setAction(new BaseAction("openWin") {
            @Override
            public void actionPerform(Component component) {
                openEditWindow();
            }
        });
    }

    public void openEditWindow() {
        Log1C log1C = getItem();
        Map<String, Object> params = new HashMap<>();
        params.put("view", "edit");
        params.put("id", log1C.getEntityId());
        String entityName = "crm$VatDoc".equals(log1C.getEntityName()) ? "crm$AcDoc" : log1C.getEntityName();
        String windowAlias = String.format("%s.edit", entityName);
        Class aClass = metadata.getExtendedEntities().getEffectiveClass(entityName);
        Entity<T> entity = ((Entity) work1C.buildQuery(aClass, params));
        final Window editor = openEditor(windowAlias, entity, WindowManager.OpenType.THIS_TAB);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {

            }
        });
    }

}