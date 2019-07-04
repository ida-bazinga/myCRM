package com.haulmont.thesis.crm.web.ui.contactperson;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.ExtContactPerson;
import com.haulmont.thesis.crm.web.ui.baseactivity.ActivityCreatorCloseListener;
import com.haulmont.thesis.crm.web.ui.baseactivity.ActivityHolderWindow;
import com.haulmont.thesis.web.app.ThesisPrintManager;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ContactPersonFrame extends AbstractFrame {

    protected boolean printActionsAlreadyInited = false;

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected Security security;

    @Named("contactsTable")
    protected Table contactsTable;
    @Named("printContactTableButton")
    protected PopupButton printButton;
    @Named("createActivityButton")
    protected PopupButton createActivityButton;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        contactsTable.addGeneratedColumn("email1", new Table.ColumnGenerator<ExtContactPerson>() {
            @Override
            public Component generateCell(ExtContactPerson entity) {
                return getCellComponent(entity.getEmail1(), entity.getEmail1(), "mailto");
            }
        });
        contactsTable.addGeneratedColumn("phone1", new Table.ColumnGenerator<ExtContactPerson>() {
            @Override
            public Component generateCell(ExtContactPerson entity) {
                String linkValue = StringUtils.left(entity.getPhone1(),StringUtils.length(entity.getPhone1()) - (StringUtils.indexOf(entity.getPhone1(),",")-5));
                return getCellComponent(linkValue, entity.getPhone1(), "tel");
            }
        });
        contactsTable.addGeneratedColumn("phone2", new Table.ColumnGenerator<ExtContactPerson>() {
            @Override
            public Component generateCell(ExtContactPerson entity) {
                String linkValue = StringUtils.left(entity.getPhone2(),StringUtils.length(entity.getPhone2()) - (StringUtils.indexOf(entity.getPhone2(),",")-5));
                return getCellComponent(linkValue, entity.getPhone2(), "tel");
            }
        });
        contactsTable.addGeneratedColumn("phone3", new Table.ColumnGenerator<ExtContactPerson>() {
            @Override
            public Component generateCell(ExtContactPerson entity) {
                String linkValue = StringUtils.left(entity.getPhone3(),StringUtils.length(entity.getPhone3()) - (StringUtils.indexOf(entity.getPhone3(),",")-5));
                return getCellComponent(linkValue, entity.getPhone3(), "tel");
            }
        });

        initPrintActions();
        createActivityActions();
        //this.addAvatarColumn();
    }

    protected Component getCellComponent(String colValue, String caption, String pref) {
        if (StringUtils.isNotBlank(colValue)) {
            Link field = componentsFactory.createComponent(Link.NAME);
            field.setCaption(caption);
            field.setUrl(String.format("%s:%s", pref, colValue));
            return field;
        } else {
            return new Table.PlainTextCell(caption);
        }
    }

    protected void initPrintActions() {
        if (!printActionsAlreadyInited) {
            AppBeans.get(ThesisPrintManager.class).addReportActionToBrowse(printButton, contactsTable, this.getId(), Collections.<String>emptyList());
        }
        printActionsAlreadyInited = true;

    }

    protected void createActivityActions(){
        Set<MetaClass> activityTypes = getEntityTypes();
        if (!activityTypes.isEmpty()){
            for (MetaClass activityType :activityTypes){
                Action action = createActivityAction(activityType);
                contactsTable.addAction(action);
                createActivityButton.addAction(action);
            }
        }
    }

    protected Action createActivityAction(final MetaClass activityType) {
        return new CreateAction(contactsTable, WindowManager.OpenType.DIALOG ,"create_".concat(activityType.getName())) {

            @Override
            public void actionPerform(Component component) {
                ExtContactPerson contactPerson = contactsTable.getSingleSelected();
                if (contactPerson == null){
                    showNotification(getMessage("notSelectedСontactPerson"), NotificationType.HUMANIZED);
                    return;
                }

                Map<String, Object> params = new HashMap<>();
                params.put("metaClassName", activityType.getName());
                params.put("contactPerson", contactPerson);
                final ActivityHolderWindow creator = openWindow("activityCreator", WindowManager.OpenType.DIALOG, params);
                creator.addListener(
                        new ActivityCreatorCloseListener(creator, WindowManager.OpenType.DIALOG, activityType.getName(), null)
                );
            }

            @Override
            public String getCaption() {
                return messages.getTools().getEntityCaption(activityType);
            }

            @Override
            public boolean isEnabled() {
                return security.isEntityOpPermitted(activityType, EntityOp.CREATE);
            }
        };
    }

    protected Set<MetaClass> getEntityTypes() {
        CrmConfig crmConfig = configuration.getConfigCached(CrmConfig.class);
        List<String> excludedTypes = crmConfig.getExcludedActivityKindMetaClasses();
        Collection<MetaClass> metaClasses = activityService.getCardTypes(excludedTypes);

        return new HashSet<>(metaClasses);
    }

    //// TODO: 31.07.2016 реализовать  AvatarHelper.getСontactAvatarURL(contact) аналогично AvatarHelper.getEmployeeAvatarURL
    //// see EmbeddedImageFrame

    /*
    protected void addAvatarColumn() {
        this.contactsTable.addGeneratedColumn("avatar", new Table.ColumnGenerator() {
            public Component generateCell(ExtContactPerson contact) {
                Embedded embedded = (Embedded)ContactPersonFrame.this.componentsFactory.createComponent("embedded");
                embedded.setSource(AvatarHelper.getEmployeeAvatarURL(contact));
                embedded.setType(Embedded.Type.IMAGE);
                embedded.setStyleName("user-avatar");
                return embedded;
            }
        });
    }
    */
}