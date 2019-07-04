package com.haulmont.thesis.crm.web.room;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.thesis.crm.entity.LogCatalog;
import com.haulmont.thesis.crm.entity.Room;
import com.haulmont.thesis.crm.entity.RoomSeatingType;
import com.haulmont.thesis.crm.entity.ServiceOperationResultsEnum;

import java.util.*;

import javax.inject.Inject;

public class RoomBrowser<T extends Room> extends AbstractLookup {
    protected HierarchicalDatasource<T, UUID> mainDs;
    protected TreeTable mainTable;

    @Inject
    protected Metadata metadata;

    @Inject
    public DataManager dataManager;

    @Inject
    protected Security security;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    protected PopupButton createBtn;

    protected Set<Entity> toCommit = new HashSet<>();


    @Override
    public void init(Map<String, Object> params) {
    super.init(params);

        mainDs = getDsContext().get("mainDs");
        mainTable = getComponentNN("mainTable");
        mainTable.addGeneratedColumn("reference", new Table.ColumnGenerator<Room>() {
            @Override
            public Component generateCell(Room entity) {
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

                final Room newEntity = metadata.create(Room.class);
                Room currentEntity = mainDs.getItem();
                if (currentEntity !=null && currentEntity.getIsGroup() !=null && currentEntity.getIsGroup())
                    newEntity.setParent(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("crm$Room.edit", newEntity, WindowManager.OpenType.THIS_TAB, params);

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

                final Room newEntity = metadata.create(Room.class);
                Room currentEntity = mainDs.getItem();

                if (currentEntity !=null && currentEntity.getIsGroup())
                    newEntity.setParent(currentEntity);

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "create");

                Window window = openEditor("crm$RoomGroup.edit", newEntity, WindowManager.OpenType.DIALOG, params);

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
                Room item = mainDs.getItem();
                if (item == null) return;

                Map<String, Object> params = new HashMap<>();
                params.put("mode", "edit");

                if (item.getIsGroup()!=null && item.getIsGroup()) {
                    window = openEditor("crm$RoomGroup.edit", item, WindowManager.OpenType.DIALOG, params);
                } else {
                    window = openEditor("crm$Room.edit", item, WindowManager.OpenType.THIS_TAB, params);
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

    public void sendToLog() {
        toCommit.clear();
        Room room = mainTable.getSingleSelected();
        if (room == null) {
            showNotification("Не выбрано помещение!", NotificationType.HUMANIZED);
            return;
        }
        if (room.getIsUseConfigurator()) {
            List<RoomSeatingType> roomSeatingTypeList = getRoomSeatingTypeList(room);
            for (RoomSeatingType roomSeatingType:roomSeatingTypeList) {
                logCatalogCommit(roomSeatingType);
            }
            doCommit();
        }

    }

    protected List<RoomSeatingType> getRoomSeatingTypeList(Room room) {
        LoadContext loadContext = new LoadContext(RoomSeatingType.class).setView("edit");
        loadContext.setQueryString("select e from crm$RoomSeatingType e where e.room.id = :roomId")
                .setParameter("roomId", room.getId());
        return dataManager.loadList(loadContext);
    }

    protected void logCatalogCommit(RoomSeatingType roomSeatingType) {
        LogCatalog logCatalog = metadata.create(LogCatalog.class);
        logCatalog.setEntityId(roomSeatingType.getId());
        logCatalog.setEntityName(roomSeatingType.getMetaClass().getName());
        logCatalog.setShortServiceOperationResults(ServiceOperationResultsEnum.notwork);
        logCatalog.setStartDate(java.util.Calendar.getInstance().getTime());
        toCommit.add(logCatalog);
    }

    private void doCommit()
    {
        if (toCommit.size() > 0) {
            Set<Entity> isCommit = dataManager.commit(new CommitContext(toCommit));
            if(!isCommit.isEmpty()) {
                showNotification("Конфигурации помещения отправлены.", NotificationType.HUMANIZED);
            }
        }
    }

}