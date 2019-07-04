package com.haulmont.thesis.crm.web.room;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.core.entity.CorrespondentAttachment;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.web.ui.avatarimage.ExtAvatarImageFrame;
import com.haulmont.thesis.web.ui.common.CardTabSheetHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class RoomEditor<T extends Room> extends AbstractEditor<T> {

    protected ExtAvatarImageFrame embeddedImageFrame;
    protected String messageVaidate;

    @Named("tabsheet")
    protected TabSheet tabsheet;
    @Named("attachmentsDs")
    protected CollectionDatasource<CorrespondentAttachment, UUID> attachmentsDs;
    @Named("roomSeatingTypeDs")
    protected CollectionDatasource<RoomSeatingType, UUID> roomSeatingTypeDs;
    @Named("roomGeolocationDs")
    protected CollectionDatasource<RoomGeolocation, UUID> roomGeolocationDs;
    @Named("roomExceptionDs")
    protected CollectionDatasource<RoomException, UUID> roomExceptionDs;
    @Named("attachmentsSeatingTypeDs")
    protected CollectionDatasource<RoomSeatingTypeAttachment, UUID> attachmentsSeatingTypeDs;
    //@Named("roomSeatingTypeTable.create")
    //private CreateAction roomSeatingTypeTablecreateAction;
    @Named("mainDs")
    protected Datasource mainDs;

    @Named("roomSeatingTypeTable")
    private Table roomSeatingTypeTable;

    @Inject
    protected CardTabSheetHelper cardTabSheetHelper;
    @Inject
    protected Metadata metadata;
    @Inject
    public DataManager dataManager;



    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        cardTabSheetHelper.addCounterOnTab(attachmentsDs, tabsheet, "attachmentsTab", null);
        roomSeatingTypeTable.addAction(RoomSeatingTypeEditAction());
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setIsGroup(false);
    }

    @Override
    protected void postInit() {
        super.postInit();
        initPhoto();
        this.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (WINDOW_CLOSE.equals(actionId) && embeddedImageFrame.isAvatarNew()) {
                    embeddedImageFrame.removeAvatar();
                }
            }
        });
        //roomSeatingTypeTablecreateAction.setInitialValues(Collections.<String, Object>singletonMap("room", getItem()));
    }

    @Override
    public void commitAndClose() {
        if (embeddedImageFrame != null)
            if (!embeddedImageFrame.commit()) {
                showNotification(getMessage("avatarMsg"), NotificationType.WARNING);
                return;
            }
        if (!roomGeolocationValidate() || !roomExceptionValidate()) {
            showNotification(this.messageVaidate, NotificationType.WARNING);
            return;
        }

        super.commitAndClose();
    }


    public void initPhoto() {
        embeddedImageFrame = getComponent("embeddedImageFrame");
        embeddedImageFrame.setMessagesPack(getMessagesPack());
        embeddedImageFrame.setImageProperty("photo");
        embeddedImageFrame.setImageProperty("avatar");
        embeddedImageFrame.init();
        embeddedImageFrame.setImageSize(195, 195);
        embeddedImageFrame.setItem(getItem());
    }

    /*
    public void roomSeatingType() {
        RoomSeatingType roomSeatingType = metadata.create(RoomSeatingType.class);
        roomSeatingType.setRoom(getItem());
        roomSeatingTypeDs.addItem(roomSeatingType);
    }


    public void roomSeatingTypeEdit() {
        setRoomSeatingTypeOpenEditor((RoomSeatingType)roomSeatingTypeTable.getSingleSelected());
    }*/

    public void roomSeatingType() {
        RoomSeatingType roomSeatingType = metadata.create(RoomSeatingType.class);
        roomSeatingType.setRoom(getItem());
        setRoomSeatingTypeOpenEditor(roomSeatingType);
    }

    protected Action RoomSeatingTypeEditAction()  {
        return new EditAction(roomSeatingTypeTable) {
            @Override
            public void actionPerform(Component component) {
                setRoomSeatingTypeOpenEditor((RoomSeatingType)roomSeatingTypeTable.getSingleSelected());
            }
        };
    }

    protected void setRoomSeatingTypeOpenEditor(RoomSeatingType roomSeatingType) {
        final Window window = openEditor("crm$RoomSeatingType.edit", roomSeatingType, WindowManager.OpenType.THIS_TAB);
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                //конфигурация
                RoomSeatingType rstw = (RoomSeatingType) window.getDsContext().get("mainDs").getItem();
                loadRoomSeatingType(rstw);
                //вложения
                CollectionDatasource<RoomSeatingTypeAttachment, UUID> attachmentsDs = window.getDsContext().get("attachmentsDs");
                loadRoomSeatingTypeAttachment(attachmentsDs);
            }
        });
    }

    protected void loadRoomSeatingTypeAttachment(CollectionDatasource<RoomSeatingTypeAttachment, UUID> attachmentsDs) {
        if (attachmentsDs.getItems().size() > 0) {
            for (RoomSeatingTypeAttachment attachment : attachmentsDs.getItems()) {
                boolean isAttachmentNew = true;

                for (RoomSeatingTypeAttachment rsta: attachmentsSeatingTypeDs.getItems()) {
                    if (attachment.equals(rsta)) {
                        isAttachmentNew = false;
                    }
                }
                if (isAttachmentNew) {
                    attachmentsSeatingTypeDs.addItem(attachment);
                }
            }
        }
    }

    protected void loadRoomSeatingType(RoomSeatingType rstw) {
        //конфигурация
        boolean isNew = true;
        for (RoomSeatingType rst:roomSeatingTypeDs.getItems()) {
            if (rst.getId().equals(rstw.getId())) {
                isNew = false;
                if (!rst.getCode().equals(rstw.getCode())) {
                    rst.setCode(rstw.getCode());
                }
                if(!rst.getName_ru().equals(rstw.getName_ru())) {
                    rst.setName_ru(rstw.getName_ru());
                }
                if((rst.getName_en() != null && rstw.getName_en() != null) && !rst.getName_en().equals(rstw.getName_en())) {
                    rst.setName_en(rstw.getName_en());
                }
                if(!rst.getMinCapacity().equals(rstw.getMinCapacity())) {
                    rst.setMinCapacity(rstw.getMinCapacity());
                }
                if(!rst.getMaxCapacity().equals(rstw.getMaxCapacity())) {
                    rst.setMaxCapacity(rstw.getMaxCapacity());
                }
                if(!rst.getSeatingType().equals(rstw.getSeatingType())) {
                    rst.setSeatingType(rstw.getSeatingType());
                }
                if (!rst.getHasAttachments().equals(rstw.getHasAttachments())) {
                    rst.setHasAttachments(rst.getHasAttachments());
                }
            }
        }
        if (isNew) {
            roomSeatingTypeDs.addItem(rstw);
        }
    }

    public void roomGeolocation() {
        RoomGeolocation roomGeolocation = metadata.create(RoomGeolocation.class);
        roomGeolocation.setRoom(getItem());
        roomGeolocationDs.addItem(roomGeolocation);
    }

    public void roomException() {
        RoomException roomException = metadata.create(RoomException.class);
        roomException.setRoom(getItem());
        roomExceptionDs.addItem(roomException);
    }

    protected boolean roomGeolocationValidate() {
        this.messageVaidate = "";
        for (RoomGeolocation roomGeolocation:roomGeolocationDs.getItems()) {
            if (roomGeolocation.getGeolocationType() == null) {
                this.messageVaidate = "Не заполнено поле \"Локация\"!";
                return false;
            }
            if(roomGeolocation.getLength() == null) {
                this.messageVaidate = "Не заполнено поле \"Длина\"!";
                return false;
            }
            if(roomGeolocation.getWidth() == null) {
                this.messageVaidate = "Не заполнено поле \"Ширина\"!";
                return false;
            }
            if(roomGeolocation.getPointX() == null) {
                this.messageVaidate = "Не заполнено поле \"Координата Х\"!";
                return false;
            }
            if(roomGeolocation.getPointY() == null) {
                this.messageVaidate = "Не заполнено поле \"Координата У\"!";
                return false;
            }
            if ("LOCATION".equals(roomGeolocation.getGeolocationType().getId())) {
                if (roomGeolocation.getLocationId() == null) {
                    this.messageVaidate = "Не заполнено поле \"Идентификатор локации\"!";
                    return false;
                }
            }

        }
        return true;
    }

    protected boolean roomExceptionValidate() {
        this.messageVaidate = "";
        for (RoomException roomException:roomExceptionDs.getItems()) {
            if (roomException.getRoomConflicting() == null) {
                this.messageVaidate = "Не заполнено поле \"Конфликтное помещение\"!";
                return false;
            }
        }
        return true;
    }


}