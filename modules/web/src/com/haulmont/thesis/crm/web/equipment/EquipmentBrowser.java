package com.haulmont.thesis.crm.web.equipment;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.thesis.crm.entity.Equipment;

import javax.inject.Inject;

public class EquipmentBrowser<T extends Equipment> extends AbstractLookup {
    protected HierarchicalDatasource<T, UUID> mainDs;
    protected TreeTable mainTable;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Security security;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    protected PopupButton createBtn;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        mainDs = getDsContext().get("mainDs");
        mainTable = getComponentNN("mainTable");
        mainTable.addGeneratedColumn("reference", new Table.ColumnGenerator<Equipment>() {
            @Override
            public Component generateCell(Equipment entity) {
                String value = (entity.getReference() != null) ? entity.getReference() : "";
                Link field = componentsFactory.createComponent(Link.NAME);
                field.setTarget("_blank");
                field.setCaption(value);
                field.setUrl(value);
                return field;
            }
        });
        mainTable.addAction(EditAction());
        createBtn.addAction(createCreateAction());
        createBtn.addAction(createCreateGroupAction());

    }

    protected Action createCreateAction() {
        return new AbstractAction("actions.createNew") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null)
                    createBtn.setPopupVisible(false);

                final Equipment newEntity = metadata.create(Equipment.class);
                Equipment currentEntity = mainDs.getItem();
                if (currentEntity !=null && currentEntity.getIsGroup() !=null && currentEntity.getIsGroup())
                    newEntity.setParent(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("crm$Equipment.edit", newEntity, WindowManager.OpenType.THIS_TAB, params);

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

    protected Action createCreateGroupAction() {
        return new AbstractAction("createGroup") {

            @Override
            public void actionPerform(Component component) {
                if (createBtn != null)
                    createBtn.setPopupVisible(false);

                final Equipment newEntity = metadata.create(Equipment.class);
                Equipment currentEntity = mainDs.getItem();

                if (currentEntity !=null && currentEntity.getIsGroup())
                    newEntity.setParent(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("crm$Equipmentgroup.edit", newEntity, WindowManager.OpenType.DIALOG, params);

                window.addListener(new CloseListener() {

                    @Override
                    public void windowClosed(String actionId) {
                        mainTable.refresh();
                    }
                });
            }

            @Override
            public String getCaption() {
                return getMessage("actions.CreateGroup");
            }
        };
    }

    protected Action EditAction()  {
        return new EditAction(mainTable) {

            @Override
            public void actionPerform(Component component) {
                Window window;
                Equipment item = mainDs.getItem();
                if (item == null) return;

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "edit");

                if (item.getIsGroup()!=null && item.getIsGroup()) {
                    window = openEditor("crm$Equipmentgroup.edit", item, WindowManager.OpenType.DIALOG, params);
                } else {
                    window = openEditor("crm$Equipment.edit", item, WindowManager.OpenType.THIS_TAB, params);
                }

                window.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        if (Window.COMMIT_ACTION_ID.equals(actionId))
                            mainDs.refresh();

                        mainTable.requestFocus();
                    }
                });
            }

            /*@Override
            public String getCaption() {
                return super.getCaption(); //getMessage("actions.Edit");
            }*/
        };
    }
}