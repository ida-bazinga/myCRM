package com.haulmont.thesis.crm.web.equipment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.thesis.crm.entity.Currency;
import com.haulmont.thesis.crm.entity.Equipment;
import com.haulmont.thesis.crm.web.ui.avatarimage.ExtAvatarImageFrame;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class EquipmentEditor extends AbstractEditor<Equipment> {

    @Named("supplierBox")
    private BoxLayout supplierBox;

    @Named("currency")
    private LookupField currency;

    @Inject
    private DataManager dataManager;

    protected ExtAvatarImageFrame embeddedImageFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (PersistenceHelper.isNew(getItem()) && currency.getValue() == null) {
            currency.setValue(getDefCurrency());
        }
        initPhoto();

        this.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (WINDOW_CLOSE.equals(actionId) && embeddedImageFrame.isAvatarNew()) {
                    embeddedImageFrame.removeAvatar();
                }
            }
        });
    }

    private Currency getDefCurrency() { //Поиск валюты по коду
        LoadContext loadContext = new LoadContext(Currency.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$Currency e where e.code = :code")
                .setParameter("code", "643");
        return dataManager.load(loadContext);
    }

    @Override
    public void commitAndClose() {
        if (embeddedImageFrame != null)
            if (!embeddedImageFrame.commit()) {
                showNotification(getMessage("avatarMsg"), NotificationType.WARNING);
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
}