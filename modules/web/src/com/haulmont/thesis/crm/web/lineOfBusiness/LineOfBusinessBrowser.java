package com.haulmont.thesis.crm.web.lineOfBusiness;

import java.util.HashMap;
import java.util.Map;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.thesis.crm.entity.LineOfBusiness;

import javax.inject.Inject;
import javax.inject.Named;

public class LineOfBusinessBrowser extends AbstractLookup {

    @Named("mainTable")
    protected TreeTable mainTable;

    @Named("createBtn")
    protected PopupButton createBtn;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Security security;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        addCreateActions();
        mainTable.addAction(EditAction());
    }

    protected void addCreateActions() {

        createBtn.addAction(createNewAction());
        createBtn.addAction(createGroupAction());
    }

    protected Action createNewAction() {
        return new AbstractAction("createNew") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null)
                    createBtn.setPopupVisible(false);

                final LineOfBusiness newEntity = metadata.create(LineOfBusiness.class);
                LineOfBusiness currentEntity = mainTable.getSingleSelected();

                //// TODO: 24.07.2016 Удалить после проверки
                //LineOfBusiness currentEntity = mainDs.getItem();

                if (currentEntity != null && currentEntity.getIsGroup())
                    newEntity.setParentLine(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("crm$LineOfBusiness.edit", newEntity, WindowManager.OpenType.DIALOG, params);

                window.addListener(new CloseListener() {

                    @Override
                    public void windowClosed(String actionId) {
                        mainTable.refresh();
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("actions.createNew");
            }

            @Override
            public boolean isEnabled() {
                MetaClass metaClass = mainTable.getDatasource().getMetaClass();
                return security.isEntityOpPermitted(metaClass, EntityOp.CREATE);
            }
        };
    }

    protected Action createGroupAction() {
        return new AbstractAction("createGroup") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null)
                    createBtn.setPopupVisible(false);

                final LineOfBusiness newEntity = metadata.create(LineOfBusiness.class);
                LineOfBusiness currentEntity = mainTable.getSingleSelected();

                if (currentEntity != null && currentEntity.getIsGroup())
                    newEntity.setParentLine(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("crm$LineOfBusinessGroup.edit", newEntity, WindowManager.OpenType.DIALOG, params);

                window.addListener(new CloseListener() {

                    @Override
                    public void windowClosed(String actionId) {
                        mainTable.getDatasource().refresh();
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("actions.CreateGroup");
            }

            @Override
            public boolean isEnabled() {
                MetaClass metaClass = mainTable.getDatasource().getMetaClass();
                return security.isEntityOpPermitted(metaClass, EntityOp.CREATE);
            }
        };
    }

    protected Action EditAction()  {
        return new EditAction(mainTable) {

            @Override
            public void actionPerform(Component component) {
                Window window;
                LineOfBusiness item = mainTable.getSingleSelected();
                if (item == null) return;

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "edit");

                if (item.getIsGroup()) {
                    window = openEditor("crm$LineOfBusinessGroup.edit", item, WindowManager.OpenType.DIALOG, params);
                } else {
                    window = openEditor("crm$LineOfBusiness.edit", item, WindowManager.OpenType.DIALOG, params);
                }

                window.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (Window.COMMIT_ACTION_ID.equals(actionId))
                            mainTable.refresh();

                        mainTable.requestFocus();
                    }
                });
            }

            @Override
            public String getCaption() {
                return super.getCaption();
            }
        };
    }

}